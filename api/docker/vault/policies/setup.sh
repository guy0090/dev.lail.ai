#!/bin/bash

PREFIX="$HOME/.loa/data"
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
TOKENS="$PREFIX/vault.tokens"

if [[ $OSTYPE == "msys" ]]; then
   echo "Running on windows with mingw"
   PREFIX=$(echo $PREFIX | sed 's/\(..\)/\0:/' | sed 's/.//')
   DIR=$(echo $DIR | sed 's/\(..\)/\0:/' | sed 's/.//')
fi

export DOCKER_HOST="vault.service.loa"
export VAULT_ADDR="https://$DOCKER_HOST:8200"
export VAULT_SKIP_VERIFY=true
export VAULT_CAPATH="$PREFIX/vault.server.pem"
export VAULT_TOKEN=$(cat "$TOKENS" | jq ".root_token" -r)

SECRETS_LIST=$(vault secrets list)

if ! echo "$SECRETS_LIST" | grep "loa/"; then
  echo "Enabling loa/ secrets engine (pki)"
  SETUP_PKI="1"
  vault secrets enable -path=loa -description="Lost Ark Logs Vault CA" -max-lease-ttl=876000h pki
fi

if ! echo "$SECRETS_LIST" | grep "secret/"; then
  echo "Enabling secret/ secrets engine"
  vault secrets enable -version=2 -path=secret -description="Lost Ark Logs Vault Secrets" kv
fi

if [ -n "$SETUP_PKI" ]; then
  echo "Setting up PKI & root certificate"
  vault write loa/root/generate/internal common_name="Lost Ark Logs Vault CA" ttl=876000h key_bits=4096 exclude_cn_from_sans=true
  vault write loa/config/urls issuing_certificates="https://$DOCKER_HOST:8200/v1/loa"
fi

# Setup roles for each service or certificate
vault write loa/roles/api key_bits=2048 max_ttl=8760h allow_any_name=true allow_ip_sans=true

# Setup access policies - files suffixed with -ct are for
# consul-template to regenerate certificates before they expire
echo "Setting up policies"
vault policy write api "$DIR/policy.api.hcl"
vault policy write api_ct "$DIR/policy.api-ct.hcl"

vault secrets list