package ai.lail.api.services

import ai.lail.api.data.followers.Followers
import ai.lail.api.exceptions.followers.FollowersAlreadyExistsException
import ai.lail.api.exceptions.followers.FollowersNotFoundException
import ai.lail.api.exceptions.followers.FollowersUpdateException
import ai.lail.api.repositories.FollowerRepository
import ai.lail.api.util.CacheHelper
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
@CacheConfig(cacheNames = [CacheHelper.FOLLOWERS_CACHE])
class FollowerService(
    val followerRepository: FollowerRepository,
    val mongoTemplate: MongoTemplate,
    val redisTemplate: RedisTemplate<String, String>
) {
    val logger: Logger = LoggerFactory.getLogger(FollowerService::class.java)


    /**
     * Creates a new [Followers] object and saves it to the database.
     * @param id The ID of the [Followers] object to create.
     *
     * @return The newly created [Followers] object.
     */
    @Cacheable(key = "#id", unless = "#result == null")
    @Throws(FollowersAlreadyExistsException::class)
    fun create(id: ObjectId): Followers {
        val exists = exists(id)
        if (exists) throw FollowersAlreadyExistsException()

        val followers = Followers(id)
        return followerRepository.save(followers)
    }


    /**
     * Finds a [Followers] object by its ID.
     * @param id The ID of the [Followers] object to find.
     *
     * @return The [Followers] object with the given ID.
     */
    @Cacheable(key = "#id", unless = "#result == null")
    @Throws(FollowersNotFoundException::class)
    fun findById(id: String): Followers = followerRepository.findById(id).orElseThrow { FollowersNotFoundException() }


    /**
     * Finds a [Followers] object by its Object ID.
     * @param id The ID of the [Followers] object to find.
     *
     * @return The [Followers] object with the given ID.
     */
    @Cacheable(key = "#id", unless = "#result == null")
    fun findById(id: ObjectId): Followers = findById(id.toHexString())


    /**
     * Finds many [Followers] objects by their IDs. Returns null if any of the IDs are invalid (if the result array is
     * unequal in length to the input array).
     * @param ids The IDs of the [Followers] objects to find.
     *
     * @return The [Followers] objects with the given IDs.
     */
    fun findManyById(ids: Collection<String>): List<Followers> {
        val results = followerRepository.findAllById(ids)
        if (results.size != ids.size) throw FollowersNotFoundException("Could not find all Followers objects with the given IDs.")
        return results
    }


    /**
     * Finds many [Followers] objects by their Object IDs. It can be assumed that all
     * [Followers] objects exist, if not the [findManyByObjectId] will throw an exception.
     * @param ids The IDs of the [Followers] objects to find.
     *
     * @return The [Followers] objects with the given IDs.
     */
    fun findManyByObjectId(ids: Collection<ObjectId>): List<Followers> = findManyById(ids.map { it.toHexString() })


    /**
     * Finds many [Followers] objects by their Object IDs and returns them as a map. It can be assumed that all
     * [Followers] objects exist, if not the [findManyByObjectId] will throw an exception.
     * @param ids The IDs of the [Followers] objects to find.
     *
     * @return The [Map] with [Followers] associated by [ObjectId].
     */
    fun findManyAndAssociateById(ids: Collection<String>): Map<ObjectId, Followers> = findManyById(ids).associateBy { it.id }


    /**
     * Finds many [Followers] objects by their Object IDs and returns them as a map. It can be assumed that all
     * [Followers] objects exist, if not the [findManyByObjectId] will throw an exception.
     * @param ids The IDs of the [Followers] objects to find.
     *
     * @return The [Map] with [Followers] associated by [ObjectId].
     *
     * @see findManyByObjectId
     */
    fun findManyAndAssociateByObjectId(ids: Collection<ObjectId>): Map<ObjectId, Followers> = findManyByObjectId(ids).associateBy { it.id }

    /**
     * Check if a [Followers] object exists by its ID.
     * @param id The ID of the [Followers] object to check.
     *
     * @return True if the [Followers] object exists, false otherwise.
     */
    fun exists(id: ObjectId): Boolean = followerRepository.existsById(id.toHexString())


    /**
     * Check if many [Followers] objects exist by their IDs.
     * @param ids The IDs of the [Followers] objects to check.
     *
     * @return True if all [Followers] objects exist, false otherwise.
     */
    fun existsMany(ids: Collection<ObjectId>): Boolean {
        val criteria = Criteria.where("_id").`in`(ids)
        val query = Query.query(criteria)
        return mongoTemplate.exists(query, Followers::class.java)
    }


    /**
     * Updates a [Followers] object. Creates a new one if it does not exist.
     * @param followers The [Followers] object to update.
     *
     * @return The updated [Followers] object.
     */
    @CacheEvict(key = "#followers.id")
    fun update(followers: Followers): Followers {
        return followerRepository.save(followers)
    }


    /**
     * Updates many [Followers] objects. Creates new ones if they do not exist.
     * @param followers The [Followers] objects to update.
     *
     * @return The updated [Followers] objects.
     */
    fun updateMany(followers: List<Followers>): List<Followers> {
        val keys = followers.map { "${CacheHelper.FOLLOWERS_CACHE}::${it.id}"}
        redisTemplate.delete(keys)

        return followerRepository.saveAll(followers)
    }


    /**
     * Deletes a [Followers] object by its ID.
     * @param id The ID of the [Followers] object to delete.
     */
    @CacheEvict(key = "#id")
    fun delete(id: ObjectId) {
        followerRepository.deleteById(id.toHexString())
    }


    /**
     * Deletes many [Followers] objects by their IDs.
     * @param ids The IDs of the [Followers] objects to delete.
     */
    fun deleteMany(ids: Collection<ObjectId>) {
        val keys = ids.map { "${CacheHelper.FOLLOWERS_CACHE}::${it}" }
        redisTemplate.delete(keys)

        followerRepository.deleteAllById(ids.map { it.toHexString() })
    }

    /// Helper functions

    /**
     * Follow a user. Adds both users to each others [Followers.followers] and [Followers.following] lists.
     * @param userId The ID of the user who is following.
     * @param target The ID of the user who is being followed.
     *
     * @throws FollowersNotFoundException if either user does not exist.
     * @throws FollowersUpdateException if the update fails.
     */
    @Throws(FollowersNotFoundException::class, FollowersUpdateException::class)
    fun follow(userId: ObjectId, target: ObjectId): List<Followers> {
        if (userId == target) throw FollowersUpdateException("Users cannot follow themselves")

        val users = findManyAndAssociateByObjectId(listOf(userId, target))
        val user = users[userId] ?: throw FollowersNotFoundException()
        val targetUser = users[target] ?: throw FollowersNotFoundException()

        if (user.isFollowing(target) || targetUser.isFollowedBy(userId))
            throw FollowersUpdateException("User $userId is already following $target")

        user.addFollowing(target)
        targetUser.addFollower(userId)

        return updateMany(listOf(user, targetUser))
    }


    /**
     * Unfollow a user. Removes both users from each others [Followers.followers] and [Followers.following] lists.
     * @param userId The ID of the user who is unfollowing.
     * @param following The ID of the user who is being unfollowed.
     *
     * @throws FollowersNotFoundException if either user does not exist.
     * @throws FollowersUpdateException if the update fails.
     *
     * @return Nothing.
     */
    @Throws(FollowersNotFoundException::class, FollowersUpdateException::class)
    fun unfollow(userId: ObjectId, following: ObjectId): List<Followers> {
        if (userId == following) throw FollowersUpdateException("Users cannot unfollow themselves")

        val users = findManyAndAssociateByObjectId(listOf(userId, following))
        val user = users[userId] ?: throw FollowersNotFoundException()
        val followingUser = users[following] ?: throw FollowersNotFoundException()

        if (!user.isFollowing(following) || !followingUser.isFollowedBy(userId))
            throw FollowersUpdateException("User $userId is not following $following")

        user.removeFollowing(following)
        followingUser.removeFollower(userId)

        return updateMany(listOf(user, followingUser))
    }


    /**
     * Count how many users are following a user.
     * @param userId The ID of the user to count followers for.
     *
     * @return The number of users following the user.
     */
    fun countFollowers(userId: ObjectId): Int {
        val followers = findById(userId)
        return followers.followers.size

        // Aggregations 😎
        // val match = Aggregation.match(Criteria.where("_id").`is`(userId))
        // val projection = Aggregation.project().and("followers").size().`as`("count")
        // val aggregation = Aggregation.newAggregation(match, projection)
        // val results: AggregationResults<Map<*, *>> = mongoTemplate.aggregate(aggregation, Followers::class.java, Map::class.java)
        // return results.uniqueMappedResult?.get("count") as? Int ?: 0
    }


    /**
     * Count how many users are following a user.
     * @param userId The ID of the user to count followers for.
     *
     * @return The number of users following the user.
     */
    fun countFollowers(userId: String): Int = countFollowers(ObjectId(userId))
}