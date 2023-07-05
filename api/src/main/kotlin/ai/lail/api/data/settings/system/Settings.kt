package ai.lail.api.data.settings.system

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

/**
 * Represents the settings of the application in the database.
 * @property id The ID of the settings.
 * @property initDone Whether the application has been initialized.
 * @property uploadsEnabled Whether uploads are enabled.
 * @property showPlayerNames Whether player names are shown in returned logs by default.
 * @property signUpAllowed Whether new user sign up is allowed.
 * @property limits The limits of the application.
 * @property defaults The defaults of the application.
 */
@Document("settings")
data class Settings(
    @Id
    val id: ObjectId = STATIC_ID,
    @Field("init_done")
    var initDone: Boolean = false,
    @Field("uploads_enabled")
    var uploadsEnabled: Boolean = false,
    var showPlayerNames: Boolean = false,
    var signUpAllowed: Boolean = true,
    var limits: Limits = Limits(),
    var defaults: Defaults = Defaults()
) : Serializable {

    companion object {
        val STATIC_ID: ObjectId = ObjectId("64175f13e3edc808366d7ce1")
    }
}