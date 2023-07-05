package ai.lail.api.config.exceptions

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerExecutionChain
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method
import java.util.*

/**
 * Global exception handler for all controllers.
 *
 * - [AbstractException]s are handled by [handleCustomExceptions]
 * - All other exceptions are handled by [handleExceptions]
 *
 * @see AbstractException
 */
@ControllerAdvice
class GlobalExceptionHandler {

    @Autowired
    lateinit var appContext: ApplicationContext

    @ResponseBody
    @ExceptionHandler(AbstractException::class)
    fun handleCustomExceptions(
        req: HttpServletRequest,
        res: HttpServletResponse,
        ex: AbstractException
    ): ExceptionResponse {
        logExceptionDetails(req, ex)

        res.status = ex.status ?: HttpStatus.BAD_REQUEST.value()
        return ExceptionResponse(res.status, ex.message ?: "Bad Request", req.requestURI, code = ex.code.value)
    }

    @ResponseBody
    @ExceptionHandler(Exception::class)
    fun handleExceptions(req: HttpServletRequest, res: HttpServletResponse, ex: Exception): ExceptionResponse {
        logExceptionDetails(req, ex)

        if (ex is MethodArgumentNotValidException) {
            res.status = HttpStatus.BAD_REQUEST.value()
            return ExceptionResponse(
                res.status,
                "Invalid Request Body",
                req.requestURI,
                code = ExceptionCode.VALIDATION_FAILED.value
            )
        }

        res.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        return ExceptionResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred",
            req.requestURI
        )
    }

    /**
     * Attempt to get the controller method that threw the exception.
     *
     * @param request The request that caused the exception
     */
    private fun getExceptionOriginMethod(request: HttpServletRequest): Method? {
        val reqHandlerMapping =
            appContext.getBean("requestMappingHandlerMapping") as RequestMappingHandlerMapping

        val handlerExeChain: HandlerExecutionChain = reqHandlerMapping.getHandler(request) as HandlerExecutionChain
        var handlerMethod: HandlerMethod? = null
        if (Objects.nonNull(handlerExeChain)) handlerMethod = handlerExeChain.handler as HandlerMethod
        if (handlerMethod == null) return null
        return handlerMethod.method
    }

    /**
     * Log the exception details. Attempts to get the controller method that threw the exception,
     * and logs the exception with the class name and method name for easier debugging.
     */
    private fun logExceptionDetails(req: HttpServletRequest, ex: Exception) {
        val caller = getExceptionOriginMethod(req)
        val clazz = caller?.declaringClass

        val logger: Logger = LoggerFactory.getLogger(clazz ?: this::class.java)
        if (clazz != null) {
            logger.error("${ex.javaClass.simpleName}::${clazz.simpleName}.${caller.name}() request failed - ${ex.message} : ${ex.cause}")
        } else {
            logger.error("${ex.javaClass.simpleName}::${this::class.java.simpleName}(${req.requestURI}) request failed - ${ex.message} : ${ex.cause}")
        }
    }

}