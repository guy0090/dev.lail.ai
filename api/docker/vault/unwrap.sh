#!/bin/sh

# Provisions an unwrap token for the backend to use
PREFIX="$HOME/.loa/data"
TOKENS="$PREFIX/vault.tokens"

if [ ! -f "$TOKENS" ]; then
    echo "No tokens file found. Please run ../setup.sh first"
    exit 1
fi

VAULT_TOKEN=$(cat "$TOKENS" | jq -r '.root_token')
DOCKER_MACHINE="https://vault.service.loa:8200"

WRAPPED=$(curl -s -k -XPOST -H "X-Vault-Token: $VAULT_TOKEN" -H "X-Vault-Wrap-TTL:20m" -d '{"policies": ["api"], "renewable": true, "ttl": "20m", "period": "48h"}' $DOCKER_MACHINE/v1/auth/token/create)
echo "$WRAPPED" | jq -r '.wrap_info.token' > "$PREFIX/vault.backend.token"
echo "Backend token written to $PREFIX/vault.backend.token"