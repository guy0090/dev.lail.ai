package ai.lail.api.data.encounters.summaries

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

data class EncounterParticipant(
    @Field("id")
    var id: String,
    @Field("name")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var name: String?,
    @Field("classId")
    var classId: Int,
    @Field("gearScore")
    var gearScore: Int,
    @Field("damage")
    var damage: Long,
    @Field("dps")
    var dps: Double,
    @Field("partyId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var partyId: String? = null,
    @Field("userId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var userId: String? = null
) : Serializable