#!/bin/sh

PREFIX="$HOME/.loa/data"
mkdir -p "$PREFIX"

# Generate CA & keys for services

echo "Generating Root CA"
keytool -genkeypair -alias loa_root -dname "CN=Lost Ark Logs Root CA, OU=LOA, O=Lost Ark, L=Seoul, ST=Seoul, C=KR" -keyalg RSA -keysize 4096 -validity 36500 -keystore "$PREFIX/loa_root.jks" -storepass "loa_foo" -keypass "loa_foo" -storetype jceks -ext BasicConstraints:"critical=ca:true" -ext KeyUsage:"critical=keyCertSign"
echo "Generating Vault cert"
keytool -genkeypair -alias loa_vault -dname CN=vault.service.loa -keyalg RSA -keysize 4096 -validity 36500 -keystore "$PREFIX/loa_root.jks" -storepass "loa_foo" -keypass "loa_foo" -storetype jceks -ext san=dns:vault.service.loa,dns:localhost
echo "Signing Vault cert"
keytool -certreq -alias loa_vault -storepass "loa_foo" -keystore "$PREFIX/loa_root.jks" -storetype jceks | keytool -storepass "loa_foo" -keystore "$PREFIX/loa_root.jks" -storetype jceks -gencert -alias loa_root -ext ku:c=dig,keyEncipherment,keyAgreement -ext san=dns:vault.service.loa,dns:localhost -rfc > vault.crt
echo "Importing signed Vault cert"
keytool -importcert -alias loa_vault -storepass "loa_foo" -keystore "$PREFIX/loa_root.jks" -storetype jceks  -file vault.crt
echo "Exporting Vault cert as PKCS12"
keytool -importkeystore -srckeystore "$PREFIX/loa_root.jks" -srcstoretype jceks -destkeystore loa.p12 -deststoretype PKCS12 -srcalias loa_vault -deststorepass "loa_foo" -destkeypass "loa_foo" -srcstorepass "loa_foo" -srckeypass "loa_foo"
echo "Extracting Vault server key in pem format"
openssl pkcs12 -in loa.p12 -nodes -nocerts -out vault.key -passin pass:loa_foo
echo "Cleaning up temporary files"
rm vault.crt loa.p12
mv vault.key "$PREFIX/vault.server.key"
echo "Export Vault server certificate"
keytool -list -rfc -keystore "$PREFIX/loa_root.jks" -alias loa_vault -storetype jceks -keypass "loa_foo" -storepass "loa_foo" > $PREFIX/vault.server.pem
