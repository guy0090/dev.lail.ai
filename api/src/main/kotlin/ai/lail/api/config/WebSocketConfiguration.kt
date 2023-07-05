package ai.lail.api.config

import ai.lail.api.controllers.websocket.WebSocketController
import ai.lail.api.security.interceptors.WsAuthenticationInterceptor
import ai.lail.api.services.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean

@Configuration
@EnableWebSocket
class WebSocketConfiguration(
    val handler: WebSocketController,
    val userService: UserService,
    val jwtService: JwtService,
    val vaultService: VaultService,
    val adminService: AdminService,
    val permissionService: PermissionService
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(handler, "/events")
            .setAllowedOrigins("*")
            .addInterceptors(
                WsAuthenticationInterceptor(
                    jwtService,
                    userService,
                    vaultService,
                    adminService,
                    permissionService
                )
            )
    }

    @Bean
    fun createWebSocketContainer(): ServletServerContainerFactoryBean {
        val container = ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(1024); // 1mb
        return container;
    }
}