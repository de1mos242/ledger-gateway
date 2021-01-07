package net.de1mos.ledger.gateway.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppRouteLocator {

    @Value("\${recorder.url}")
    var recorderUrl: String? = null

    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder) = builder.routes {
        route(id = "recorder") {
            path("/api/recorder/**")
            uri("$recorderUrl/api/recorder/")
        }
    }
}