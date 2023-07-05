#!/bin/bash

PREFIX="$HOME/.loa/data"
DOCKER_HOST="vault.service.loa"

echo "Waiting for Vault to start"
sleep 2
curl -X PUT -k -d "{\"secret_shares\":1, \"secret_threshold\":1}" "https://$DOCKER_HOST:8200/v1/sys/init" > "$PREFIX/vault.tokens"
sleep 2