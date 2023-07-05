{{ with secret "loa/cert/ca" }} # no defined TTL because this is the root cert and lives for 100 years
{{ .Data.certificate }}{{ end }}
