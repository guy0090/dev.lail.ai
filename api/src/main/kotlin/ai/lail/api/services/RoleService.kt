package ai.lail.api.services

import ai.lail.api.data.permissions.Role
import ai.lail.api.dto.requests.roles.CreateRoleDto
import ai.lail.api.exceptions.roles.MissingSuperuserRoleException
import ai.lail.api.exceptions.permissions.RoleNotFoundException
import ai.lail.api.exceptions.roles.RolesDeletePermanentException
import ai.lail.api.exceptions.roles.RolesDeleteSuperuserException
import ai.lail.api.repositories.RoleRepository
import ai.lail.api.util.CacheHelper
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
@CacheConfig(cacheNames = [CacheHelper.ROLES_CACHE])
class RoleService(
    val roleRepository: RoleRepository,
    val mongoTemplate: MongoTemplate,
) {
    val logger: Logger = LoggerFactory.getLogger(RoleService::class.java)

    fun count(): Long {
        return roleRepository.count()
    }

    @CachePut(key = "#role.id")
    fun save(role: Role): Role? {
        return roleRepository.save(role)
    }

    fun saveAll(roles: List<Role>) {
        roleRepository.saveAll(roles)
    }

    @Cacheable(key = "#id", unless = "#result == null")
    fun findById(id: ObjectId): Role? {
        return roleRepository.findById(id.toHexString()).orElse(null)
    }

    fun findAllById(ids: List<ObjectId>): List<Role> {
        if (ids.isEmpty()) return listOf()
        return roleRepository.findAllById(ids.map { it.toHexString() })
    }

    fun findByName(name: String): List<Role>? {
        val roles = roleRepository.findRoleByName(name)
        if (roles.isNullOrEmpty()) return null
        return roles
    }

    @CachePut(key = "#role.id")
    fun addPermission(role: Role, permission: String) {
        role.addPermission(permission)
        save(role)
    }

    @CachePut(key = "#role.id")
    fun addPermissions(role: Role, permissions: Set<String>) {
        role.addPermissions(permissions)
        save(role)
    }

    @CachePut(key = "#role.id")
    fun removePermission(role: Role, permission: String) {
        role.removePermission(permission)
        save(role)
    }

    @CachePut(key = "#role.id")
    fun removePermissions(role: Role, permissions: Set<String>) {
        role.removePermissions(permissions)
        save(role)
    }

    /**
     * Creates a new role.
     * @param dto The new role to add as CreateRoleDto.
     * @return The newly added role.
     */
    fun createNewRole(dto: CreateRoleDto): Role? {
        val role = Role(dto)
        return save(role)
    }

    @CachePut(key = "#role.id")
    fun addParent(role: Role, parent: Role) {
        role.addParent(parent)
        save(role)
    }

    @CachePut(key = "#role.id")
    fun removeParent(role: Role, parent: Role) {
        role.removeParent(parent)
        save(role)
    }

    @CachePut(key = "#role.id")
    fun setDefault(role: Role, default: Boolean) {
        role.default = default
        save(role)
    }

    fun getDefaultRoles(): List<Role> {
        val criteria = Criteria.where("default").`is`(true)
        val query = Query.query(criteria)
        return mongoTemplate.find(query, Role::class.java)
    }

    @Throws(MissingSuperuserRoleException::class)
    fun getSuperuserRole(): Role {
        val criteria = Criteria.where("superUser").`is`(true)
        val query = Query.query(criteria)
        return mongoTemplate.findOne(query, Role::class.java) ?: throw MissingSuperuserRoleException()
    }

    fun exists(role: ObjectId): Boolean {
        return roleRepository.existsById(role.toHexString())
    }

    fun allExists(roles: Collection<ObjectId>): Boolean {
        val count = roleRepository.countByIds(roles).orElse(0)
        return count == roles.size
    }

    /**
     * Deletes a role.
     * @param roleId The role to be deleted as ObjectId .
     */
    @CacheEvict(key = "#roleId")
    fun delete(roleId: ObjectId) {
        canDelete(roleId)
        deleteById(roleId)
    }

    /**
     * Checks if a role can be deleted.
     * @param roleId The role to check if it can be deleted.
     */
    fun canDelete(roleId: ObjectId) {
        val role = findById(roleId) ?: throw RoleNotFoundException()
        if (role.superUser) throw RolesDeleteSuperuserException()
        if (role.permanent) throw RolesDeletePermanentException()
    }

    @CacheEvict(key = "#id")
    fun deleteById(id: ObjectId) {
        roleRepository.deleteById(id.toHexString())
    }

    /**
     * Recursively get all permissions for a list of roles, including those inherited from parents
     * @param role The role to get permissions for
     * @param enc The set of roles already encountered in the recursion, only use for internal recursion
     */
    fun getAllPermissions(role: List<Role>, enc: MutableSet<String>?): MutableSet<String> {
        val parents = mutableSetOf<Role>()
        val permissions = mutableSetOf<String>()
        val encountered = enc ?: mutableSetOf()

        role.forEach {
            encountered.add(it.id.toHexString())
            permissions.addAll(it.permissions)
            parents.addAll(findAllById(it.inherits.toList()))
        }

        for (parent in parents) {
            // Prevent infinite recursion by potential circular inheritance
            if (encountered.contains(parent.id.toHexString())) continue

            permissions.addAll(parent.permissions)
            if (parent.inherits.isNotEmpty()) {
                val nested = findAllById(parent.inherits.filter { !encountered.contains(it.toHexString()) })
                nested.forEach { permissions.addAll(getAllPermissions(listOf(it), encountered)) }
            }
        }

        return permissions
    }
}