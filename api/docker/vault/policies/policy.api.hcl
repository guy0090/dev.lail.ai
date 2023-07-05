#
# access everything in the secrets/ path
#
path "secret/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

#
# access everything in the loa/ path (pki)
#
path "loa/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}
