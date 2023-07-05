import fs from 'fs';
import https from 'https';
import axios, { AxiosRequestConfig } from 'axios';
import { normalizePath } from '@/utils/normalize';
import { VAULT_HOST, VAULT_PORT, VAULT_TOKEN, VAULT_CA } from '@config';
import { logger } from '@/utils/logger';

export class VaultService {
  protected static host = `https://${VAULT_HOST}:${VAULT_PORT}`;
  protected static rootToken: string | undefined;
  protected static unwrapped: string | undefined;
  protected static salts: { [key: string]: string } = {};
  static signingKey: string | undefined;
  static socketToken: string | undefined;

  static loadRootToken() {
    if (!VaultService.rootToken) {
      const tokenFile = fs.readFileSync(normalizePath(VAULT_TOKEN));
      VaultService.rootToken = JSON.parse(tokenFile.toString()).root_token;
      logger.info('Vault root token loaded', tokenFile.toString());
    }
  }

  static async renewLease() {
    const unwrapped = VaultService.unwrapped;
    if (!unwrapped) throw new Error('No unwrapped token');

    const renewed = await this.renewSelf(unwrapped);
    VaultService.unwrapped = renewed.client_token;
    return renewed;
  }

  static async getUserSalt(userId: string) {
    let salt = VaultService.salts[userId];
    if (salt) return salt;

    const response = await this.read(`secret/data/user/${userId}`);
    salt = response.salt;
    if (!salt) throw new Error('No salt found');
    VaultService.salts[userId] = salt;
    return salt;
  }

  static async getSocketToken() {
    const socketToken = await this.read(`secret/data/system/ingestToken`);
    return socketToken.ingestToken;
  }

  static async setupSecrets() {
    if (!VaultService.rootToken) {
      this.loadRootToken();
    }

    if (VaultService.signingKey || VaultService.socketToken) throw new Error('Secrets already set');

    const token = (await this.createToken(VaultService.rootToken)).wrap_info.token;
    const unwrapped = await this.unwrap(token);
    VaultService.unwrapped = unwrapped;

    const jwtResponse = await this.read('secret/data/system/jwt');
    const key = jwtResponse.signingKey;
    VaultService.signingKey = key;

    return key;
  }

  /// Vault API

  protected static async createToken(rootToken: string) {
    const policy = {
      policies: ['api'],
      renewable: true,
      period: '48h',
    };

    const request = {
      url: `${VaultService.host}/v1/auth/token/create`,
      method: 'POST',
      headers: {
        'X-Vault-Token': rootToken,
        'X-Vault-Wrap-TTL': '20m',
        'Content-Type': 'application/json',
      },
      data: policy,
      httpsAgent: this.getAgent(),
    } as AxiosRequestConfig;

    const response = await axios(request);
    return response.data as AuthResponse;
  }

  protected static async unwrap(token: string) {
    const request = {
      url: `${VaultService.host}/v1/sys/wrapping/unwrap`,
      method: 'POST',
      headers: {
        'X-Vault-Token': token,
      },
      httpsAgent: this.getAgent(),
    } as AxiosRequestConfig;

    const response = await axios(request);
    const data = response.data as UnwrapResponse;
    return data.auth.client_token;
  }

  protected static async renewSelf(token: string) {
    const request = {
      url: `${VaultService.host}/v1/auth/token/renew-self`,
      method: 'POST',
      headers: {
        'X-Vault-Token': token,
      },
      httpsAgent: this.getAgent(),
    } as AxiosRequestConfig;

    const response = await axios(request);
    const data = response.data as UnwrapResponse;
    return data.auth;
  }

  protected static async read(path: string) {
    if (path.startsWith('/')) path = path.substring(1);

    const request = {
      url: `${VaultService.host}/v1/${path}`,
      method: 'GET',
      headers: {
        'X-Vault-Token': VaultService.unwrapped,
      },
      httpsAgent: this.getAgent(),
    };

    const response = await axios(request);
    const data = response.data as ReadResponse;
    return data.data.data; // ???
  }

  protected static getAgent(): https.Agent {
    const caFile = fs.readFileSync(normalizePath(VAULT_CA));

    return new https.Agent({
      ca: caFile,
    });
  }
}

/**
 * All unused fields omitted
 */
interface AuthResponse {
  wrap_info: {
    token: string;
  };
}

/**
 * All unused fields omitted
 */
interface UnwrapResponse {
  auth: {
    client_token: string;
    lease_duration: number;
  };
}

/**
 * All unused fields omitted
 */
interface ReadResponse {
  data: {
    data: {
      signingKey?: string;
      salt?: string;
      ingestToken?: string;
    };
  };
}
