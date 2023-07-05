#!/bin/sh

PREFIX="$HOME/.loa/data"
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

if [[ $OSTYPE == "msys" ]]; then
   echo "Running on windows with mingw"
   PREFIX=$(echo $PREFIX | sed 's/\(..\)/\0:/' | sed 's/.//')
   DIR=$(echo $DIR | sed 's/\(..\)/\0:/' | sed 's/.//')
fi

echo "PREFIX=$PREFIX"
echo "DIR=$DIR"

cat << EOF > $HOME/.loa/data/ct-config.hcl
template {
  source      = "$DIR/vault.ca.pem.tpl"
  destination = "$PREFIX/vault.ca.pem"
  perms = 0600
}

vault {
  address = "https://vault.service.loa:8200"
  renew_token = false
  ssl {
    enabled = true
    verify = false
    ca_cert = "$PREFIX/vault.server.pem"
  }
}
EOF

TOKENS=$PREFIX/vault.tokens

export VAULT_SKIP_VERIFY=true # self signed certs
export VAULT_CAPATH="$PREFIX/vault.server.pem"
export VAULT_TOKEN=$(cat "$TOKENS" | jq ".root_token" -r)
echo $VAULT_TOKEN

cd $PREFIX
consul-template -config "./ct-config.hcl" -once
