#!/bin/bash

PREFIX="$HOME/.loa/data"

echo "Stopping any running containers"
docker compose down
sleep 1

echo "Starting and unsealing Vault"
docker compose up -d vault
sleep 1

echo "Unsealing Vault"
./vault/unseal.sh
sleep 1

echo "Unwrapping for API"
./vault/unwrap.sh
sleep 1

echo "Starting other services"
docker compose up -d mongo
docker compose up -d redis

echo "Done"
sleep 2