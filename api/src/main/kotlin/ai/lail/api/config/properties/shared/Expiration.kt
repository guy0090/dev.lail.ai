package ai.lail.api.config.properties.shared

/**
 * Expiration times for various data services.
 * All times in seconds.
 */
class Expiration {

    var users: Long = 1800
    var encounters: Long = 3600
    var encounterSummaries: Long = 3000
    var permissions: Long = 1800
    var roles: Long = 0 // Don't expire
    var guilds: Long = 7200
    var systemSettings: Long = 0 // Don't expire
    var vault: Long = 4000
    var apiKeys: Long = 4000
    var userSettings: Long = 4000
    var followers: Long = 4000

    fun toMap(): Map<String, Long> {
        return mapOf(
            "users" to users,
            "encounters" to encounters,
            "encounter-summaries" to encounterSummaries,
            "permissions" to permissions,
            "roles" to roles,
            "guilds" to guilds,
            "system-settings" to systemSettings,
            "vault" to vault,
            "api-keys" to apiKeys,
            "user-settings" to userSettings,
            "followers" to followers
        )
    }
}