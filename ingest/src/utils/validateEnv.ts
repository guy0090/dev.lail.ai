import { cleanEnv, port, str } from 'envalid';

export const ValidateEnv = () => {
  cleanEnv(process.env, {
    NODE_ENV: str(),
    PORT: port(),
    VAULT_HOST: str(),
    VAULT_PORT: port(),
    VAULT_TOKEN: str(),
    VAULT_CA: str(),
  });
};
