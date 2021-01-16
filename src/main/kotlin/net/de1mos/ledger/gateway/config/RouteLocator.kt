package net.de1mos.ledger.gateway.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppRouteLocator(private val tokenRelayGatewayFilterFactory: TokenRelayGatewayFilterFactory) {

    @Value("\${recorder.url}")
    var recorderUrl: String? = null

    @Value("\${accounting.url}")
    var accountingUrl: String? = null

    @Bean
    fun recorderRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes().route("recorder") {
            it.path("/api/recorder/**")
                .filters { f ->
                    f.filters(tokenRelayGatewayFilterFactory.apply())
                        .removeRequestHeader("Cookie")
                }
                .uri("$recorderUrl/api/recorder/")
        }.build()
    }

    @Bean
    fun accountingRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes().route("recorder") {
            it.path("/api/accounting/**")
                .filters { f ->
                    f.filters(tokenRelayGatewayFilterFactory.apply())
                        .removeRequestHeader("Cookie")
                }
                .uri("$accountingUrl/api/accounting/")
        }.build()
    }
}
