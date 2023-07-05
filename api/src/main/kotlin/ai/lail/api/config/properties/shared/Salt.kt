package ai.lail.api.config.properties.shared

class Salt {

    /**
     * The size of the salt to be generated
     * Will be stored in Vault on first startup
     */
    var length: Int = 32

    /**
     * The algorithm to use when hashing
     */
    var hasher: String = "SHA-256"
}