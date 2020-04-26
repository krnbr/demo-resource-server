package in.neuw.resource.demo.config;

import com.google.gson.Gson;
import in.neuw.resource.demo.security.CustomBearerTokenServerAccessDeniedHandler;
import in.neuw.resource.demo.security.CustomBearerTokenServerAuthenticationEntryPoint;
import in.neuw.resource.demo.security.HasScope;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * @author Karanbir Singh on 04/21/2020
 */
@EnableReactiveMethodSecurity
public class SecurityConfig implements WebFluxConfigurer {

    @Bean
    public CustomBearerTokenServerAuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomBearerTokenServerAuthenticationEntryPoint();
    }

    @Bean
    public CustomBearerTokenServerAccessDeniedHandler accessDeniedHandler() {
        return new CustomBearerTokenServerAccessDeniedHandler();
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange()
                // health and info url's will be open(permitted to all) others will be checked for authorization
                .matchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class)).permitAll()
                // all other endpoints will require the scope to be "admin"
                .matchers(EndpointRequest.toAnyEndpoint()).access(new HasScope("admin"))
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
                .jwt();

        return http.build();
    }

}
