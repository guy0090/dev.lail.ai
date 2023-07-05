package ai.lail.api.config.properties.shared

class SslConfiguration {
    var enabled: Boolean = false
    var ca: CertificateAuthority = CertificateAuthority()
}