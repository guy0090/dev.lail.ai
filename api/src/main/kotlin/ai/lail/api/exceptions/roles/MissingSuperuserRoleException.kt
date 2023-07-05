package ai.lail.api.exceptions.roles

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.springframework.http.HttpStatus

class MissingSuperuserRoleException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Superuser role is missing"
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.ROLES_DELETE_SUPER_USER_ROLE

    constructor() : this(null, null)
}