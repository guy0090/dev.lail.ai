#
# access everything in the loa/ path (pki)
#
path "loa/cert/ca" {
  capabilities = ["read"]
}

path "loa/issue/mongo" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

path "loa/issue/redis" {
    capabilities = ["create", "read", "update", "delete", "list"]
}

path "loa/issue/app" {
  capabilities = ["create", "read", "update", "delete", "list"]
}
