package ai.lail.api.data.encounters.summaries

import ai.lail.api.enums.EncounterSummaryStatus
import ai.lail.api.enums.PrivacySetting
import ai.lail.api.util.ObjectIdListConverter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

@Document("encountersummaries") // Lower-case because of mongoose ingest
data class EncounterSummary(
    @Id
    @JsonSerialize(using = ToStringSerializer::class)
    @JsonProperty("id")
    var id: ObjectId,

    @Field("visibility")
    var visibility: PrivacySetting = PrivacySetting.PUBLIC,

    @Field("association")
    var association: EncounterAssociation,

    @Field("owner")
    @JsonProperty("owner")
    @JsonSerialize(using = ToStringSerializer::class)
    var owner: ObjectId,

    @Field("users")
    @JsonProperty("users")
    @JsonSerialize(converter = ObjectIdListConverter::class)
    var users: List<ObjectId> = listOf(),

    @Field("bossId")
    var bossId: Int,

    @Field("participants")
    var participants: List<EncounterParticipant> = listOf(),

    @Field("duration")
    var duration: Int,

    @Field("created")
    var created: Long,

    @Field("status")
    var status: EncounterSummaryStatus = EncounterSummaryStatus.PROCESSING,
) : Serializable {
    @JsonIgnore
    fun getParties(): List<String> = participants.mapNotNull { it.partyId }.distinct()
}