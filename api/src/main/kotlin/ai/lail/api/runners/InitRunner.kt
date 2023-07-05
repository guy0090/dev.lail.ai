package ai.lail.api.runners

import ai.lail.api.data.permissions.Role
import ai.lail.api.data.settings.system.Settings
import ai.lail.api.data.users.User
import ai.lail.api.enums.ServiceStatus
import ai.lail.api.exceptions.settings.system.SystemSettingsNotFoundException
import ai.lail.api.runners.fixtures.InitFixtures
import ai.lail.api.services.*
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("!test")
@Component
class InitRunner(
    val permissionService: PermissionService,
    val adminService: AdminService,
    val vaultService: VaultService,
    val jwtService: JwtService,
    val roleService: RoleService,
    val userService: UserService,
    val appContext: ApplicationContext
) : ApplicationRunner {
    val logger: Logger = LoggerFactory.getLogger(InitRunner::class.java)

    override fun run(args: ApplicationArguments?) {
        val start = System.currentTimeMillis()
        adminService.setSystemStatus(ServiceStatus.STARTING)
        logger.info("Waiting for vault to be ready")
        waitForVault()
        logger.info("Vault ready after ${System.currentTimeMillis() - start}ms")

        logger.info("================ STARTUP ================")
        loadDefaults()

        if (!isInitialized()) {
            logger.info("Initializing service")
            firstStart()
            Thread.sleep(500)
        }
        setupIngestUser()

        if (appContext.environment.activeProfiles.contains("dev")) {
            logger.info("DEV MODE: Adding dummy users")
            for (i in 1..9) {
                createTestUser(i)
            }
        }

        logger.info("Service ready to accept connections")
        logger.info("Startup took ${System.currentTimeMillis() - start}ms")
        logger.info("================ STARTUP ================")
        adminService.setSystemStatus(ServiceStatus.READY)
    }


    /**
     * Check if service is initialized
     */
    fun isInitialized(): Boolean {
        return try {
            val settings = adminService.getSettings()
            settings.initDone
        } catch (e: Exception) {
            false
        }
    }


    fun firstStart() {
        try {
            adminService.getSettings()
        } catch (e: SystemSettingsNotFoundException) {
            val settings = Settings()
            adminService.saveSettings(settings)
            logger.info("Created init settings")
        }

        if (vaultService.createJwtKey()) logger.info("Generated JWT Signing Key")
        var initKey = vaultService.getInitKey()
        if (initKey == null) {
            logger.info("Generated Init Key:")
            initKey = vaultService.createInitKey()
        } else {
            logger.info("Init Key found:")
        }
        logger.info("- $initKey")
        logger.info("COPY OR FETCH FROM VAULT LATER TO ACTIVATE SERVICE")
        logger.info("=========================================")
    }


    private fun waitForVault() {
        if (vaultService.isReady()) return
        Thread.sleep(50)
        waitForVault()
    }

    /// Defaults

    /**
     * Load default roles and permissions into MongoDB
     * Data is taken from roles.json in class path resources
     *
     * @see Role
     */
    fun loadDefaults() {
        val defaults = listOf(
            InitFixtures.defaultUserRole,
            InitFixtures.defaultAdminRole,
            InitFixtures.defaultSuperuserRole,
            InitFixtures.defaultIngestRole
        )
        if (roleService.findAllById(defaults.map { it.id }).isNotEmpty()) {
            logger.debug("Roles already loaded")
            return
        }

        logger.info("Loading default permissions/roles")
        roleService.saveAll(defaults.toList())
        defaults.forEach { roleService.findById(it.id) }.also { logger.info("Precached roles") }
        logger.info("Default permissions/roles loaded")
        logger.info("=========================================")
    }

    fun setupIngestUser() {
        if (userService.exists(InitFixtures.ingestBotUser.id)) {
            refreshIngestJwt()
            return
        }

        roleService.save(InitFixtures.defaultIngestRole)
        val created = adminService.createNewUser(InitFixtures.ingestBotUser)
        permissionService.addRole(created.id, InitFixtures.defaultIngestRole.id)
        val token = jwtService.generateToken(created.id)
        vaultService.saveIngestToken(token)

        logger.info("Created Ingest user: ${created.discordUsername}")
        logger.info("=========================================")
    }

    fun refreshIngestJwt() {
        val token = jwtService.generateToken(InitFixtures.ingestBotUser.id)
        vaultService.saveIngestToken(token)
        logger.info("Refreshed Ingest JWT")
        logger.info("=========================================")
    }

    // Create test users

    fun createTestUser(id: Int) {
        if (id > 9) throw IllegalArgumentException("id must be between 0 and 9 (inclusive)")
        val objectId = ObjectId("00000000000000000000001$id")
        if (userService.find(objectId) != null) return

        val user = adminService.createNewUser(User(objectId, discordUsername = "TEST_$id", discriminator = "000$id"))
        logger.info("Created test user: ${user.discordUsername}")
    }
}