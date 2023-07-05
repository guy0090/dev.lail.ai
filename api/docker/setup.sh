#!/bin/bash

# Pre-setup for unsealing/initiating Vault
PREFIX="$HOME/.loa/data"
mkdir -p "$PREFIX"

echo "Setting up and preparing initial environment"
sleep 1
echo "Generating CA and keys"
./certs/keygen.sh
sleep 2

echo "Starting Vault"
docker compose up --build -d vault
echo "Copying Vault keys to container"
docker cp "$PREFIX/vault.server.key" loa_vault:/vault/config/vault.server.key
docker cp "$PREFIX/vault.server.pem" loa_vault:/etc/vault/vault.server.pem
sleep 1
docker compose restart vault

sleep 2
echo "Initializing Vault"
./vault/init.sh
echo "Unsealing Vault"
./vault/unseal.sh
echo "Setting up Vault policies"
./vault/policies/setup.sh
sleep 2
echo "Copying consul templates"
./consul-templates/config.sh

echo "Starting other services"
sleep 2
docker compose up --force-recreate --build -d mongo
docker compose up --force-recreate --build -d redis

echo "Getting backend wrapping token"
sleep 2
./vault/unwrap.sh