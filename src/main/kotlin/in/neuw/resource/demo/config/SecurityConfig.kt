package `in`.neuw.resource.demo.config

import `in`.neuw.resource.demo.security.CustomBearerTokenServerAccessDeniedHandler
import `in`.neuw.resource.demo.security.CustomBearerTokenServerAuthenticationEntryPoint
import `in`.neuw.resource.demo.security.HasScope
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest
import org.springframework.boot.actuate.health.HealthEndpoint
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.config.WebFluxConfigurer

@EnableReactiveMethodSecurity
class SecurityConfig: WebFluxConfigurer {

    @Bean
    fun authenticationEntryPoint(): CustomBearerTokenServerAuthenticationEntryPoint {
        return CustomBearerTokenServerAuthenticationEntryPoint()
    }

    @Bean
    fun accessDeniedHandler(): CustomBearerTokenServerAccessDeniedHandler {
        return CustomBearerTokenServerAccessDeniedHandler()
    }

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        http
                .authorizeExchange()
                // health and info url's will be open(permitted to all) others will be checked for authorization
                .matchers(EndpointRequest.to(HealthEndpoint::class.java, InfoEndpoint::class.java)).permitAll()
                // all other endpoints will require the scope to be "admin"
                .matchers(EndpointRequest.toAnyEndpoint()).access(HasScope("admin"))
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
                .jwt()

        return http.build()
    }

}