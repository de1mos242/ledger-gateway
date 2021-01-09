package net.de1mos.ledger.gateway.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.LogoutSpec
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter
import reactor.core.publisher.Mono


class SucHandler(private val redirect: String) : ServerAuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        webFilterExchange.exchange.response.statusCode = HttpStatus.TEMPORARY_REDIRECT
        webFilterExchange.exchange.response.headers[HttpHeaders.LOCATION] = redirect
        return Mono.empty()
    }
}

@Configuration
class SecurityConfig(@Value("\${login.redirect}") private val loginRedirect: String) {
    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        registrationRepository: ReactiveClientRegistrationRepository?
    ): SecurityWebFilterChain? {
        http.oauth2Login().authenticationSuccessHandler(SucHandler(loginRedirect))
        http.logout { logout: LogoutSpec ->
            logout.logoutSuccessHandler(
                OidcClientInitiatedServerLogoutSuccessHandler(registrationRepository)
            )
        }
        http.authorizeExchange().pathMatchers("/actuator/**").permitAll()
        http.authorizeExchange().anyExchange().authenticated()
        http.headers().frameOptions().mode(XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN)
        http.csrf().disable()

        return http.build()
    }

    @Bean
    fun authorizedClientRepository(): ServerOAuth2AuthorizedClientRepository? {
        return WebSessionServerOAuth2AuthorizedClientRepository()
    }
}