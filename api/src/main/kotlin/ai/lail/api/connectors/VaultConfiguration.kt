package ai.lail.api.connectors

import ai.lail.api.config.services.VaultConfigurationService
import com.bettercloud.vault.SslConfig
import com.bettercloud.vault.Vault
import com.bettercloud.vault.VaultConfig
import com.bettercloud.vault.VaultException
import com.bettercloud.vault.response.AuthResponse
import com.bettercloud.vault.rest.Rest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

@Configuration
class VaultConfiguration(final val service: VaultConfigurationService) {
    final val logger: Logger = LoggerFactory.getLogger(VaultConfiguration::class.java)
    var init = true
    var ready = false

    var token: String = service.getVaultToken()
    val host = service.getUrl()
    val sslEnabled = service.isSslEnabled()
    val caCert = service.getCaCert()
    val secretEngineVersion = service.getSecretEngineVersion()

    final fun vault(): Vault = Vault(config().build())

    private fun config(): VaultConfig {
        var config = VaultConfig()
            .address(host)
            .token(token)
            .openTimeout(5)
            .readTimeout(30)
            .engineVersion(secretEngineVersion)

        if (sslEnabled) {
            config = config.sslConfig(
                SslConfig()
                    .verify(true)
                    .pemFile(caCert)
                    .build()
            )
        }

        return config
    }

    /**
     * Renews the lease on the token every hour
     * Will run immediately on startup, then every hour after that
     *
     * Is also used to initialize the service if it's the first run
     */
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    private fun renewToken() {
        logger.info("Attempting scheduled lease renew...")
        try {
            if (init) {
                init = false
                val unwrapped = unwrap()
                token = unwrapped.authClientToken
            }

            val response = vault().auth().renewSelf()
            token = response.authClientToken
            if (!ready) ready = true
            logger.info("New lease acquired, now valid for ${response.authLeaseDuration / 3600} hours")
        } catch (e: VaultException) {
            logger.error("Error renewing lease, please restart the service", e)
            exitProcess(1)
        }
    }

    /**
     * Grabs a wrapped token from Vault
     */
    @Throws(VaultException::class)
    protected final fun unwrap(): AuthResponse {
        val config = config()
        var retries = 0

        while (true) {
            try {
                val url = "${config.address}/v1/sys/wrapping/unwrap"
                val response = Rest()
                    .url(url)
                    .header("X-Vault-Token", config.token)
                    .connectTimeoutSeconds(config.openTimeout)
                    .readTimeoutSeconds(config.readTimeout)
                    .sslVerification(config.sslConfig.isVerify)
                    .sslContext(config.sslConfig.sslContext)
                    .post()

                if (response.status != 200) {
                    throw VaultException(
                        "Vault responded with HTTP status code: ${response.status} -> ${String(response.body)}",
                        response.status
                    )
                }

                val mimeType = response.mimeType ?: null
                if (mimeType == null || mimeType != "application/json") {
                    throw VaultException("Vault responded with unexpected content type: $mimeType", response.status)
                }

                return AuthResponse(response, retries)
            } catch (e: Exception) {
                if (retries < config.maxRetries) {
                    retries++
                    try {
                        val retryIntervalMilliseconds = config.retryIntervalMilliseconds
                        Thread.sleep(retryIntervalMilliseconds.toLong())
                    } catch (e1: InterruptedException) {
                        e1.printStackTrace()
                    }
                } else if (e is VaultException) {
                    throw e
                } else {
                    throw VaultException(e)
                }
            }
        }
    }
}