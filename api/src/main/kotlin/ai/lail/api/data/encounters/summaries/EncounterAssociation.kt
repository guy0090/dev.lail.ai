package ai.lail.api.data.encounters.summaries

import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

data class EncounterAssociation(
    @Field("players")
    val players: String,
    @Field("zone")
    val zone: Int
) : Serializable {
    override fun toString(): String = "$players|$zone"

    companion object {
        fun fromString(string: String): EncounterAssociation {
            val split = string.split("|")
            return EncounterAssociation(split[0], split[1].toInt())
        }
    }
}