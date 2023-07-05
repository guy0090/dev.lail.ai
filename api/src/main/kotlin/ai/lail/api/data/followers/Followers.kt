package ai.lail.api.data.followers

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable

/**
 * Represents a list of users a person follows and the users that follow them.
 *
 * @property id The ID of the follower.
 * @property followers The IDs of the users that follow the user.
 * @property following The IDs of the users the user follows.
 */
@Document("followers")
data class Followers(
    @Id
    var id: ObjectId = ObjectId(),
    var followers: MutableSet<ObjectId> = mutableSetOf(),
    var following: MutableSet<ObjectId> = mutableSetOf()
) : Serializable {

    fun addFollower(followerId: ObjectId) = followers.add(followerId)

    fun removeFollower(followerId: ObjectId) = followers.remove(followerId)

    fun clearFollowers() = followers.clear()

    fun isFollowing(followingId: ObjectId): Boolean = following.contains(followingId)

    fun addFollowing(followingId: ObjectId) = following.add(followingId)

    fun removeFollowing(followingId: ObjectId) = following.remove(followingId)

    fun clearFollowing() = following.clear()

    fun isFollowedBy(user: ObjectId): Boolean = followers.contains(user)
}