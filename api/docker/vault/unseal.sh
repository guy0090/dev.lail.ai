#!/bin/bash

PREFIX="$HOME/.loa/data"
TOKENS="$PREFIX/vault.tokens"
DOCKER_HOST="vault.service.loa"
KEY=$(cat "$TOKENS" | jq ".keys[0]" -r)
echo "Vault Key=$KEY"

if [ "$KEY" = "null" ] || [ -z "$KEY" ]; then
    echo "Could not find Vault key in $TOKENS"
    exit 1
fi

echo "Unsealing Vault"
sleep 1
curl -X PUT -k -d "{\"key\":\"$KEY\"}" "https://$DOCKER_HOST:8200/v1/sys/unseal"
sleep 1