package `in`.neuw.resource.demo.security

import net.minidev.json.JSONArray
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.ReactiveAuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.server.authorization.AuthorizationContext
import reactor.core.publisher.Mono

class HasScope(vararg scopes: String) : ReactiveAuthorizationManager<AuthorizationContext> {

    private var scopes = setOf(*scopes)
        get() = field

    override fun check(authentication: Mono<Authentication>, `object`: AuthorizationContext?): Mono<AuthorizationDecision>? {
        return authentication
                .flatMap { it: Authentication ->
                    val auth: JwtAuthenticationToken = it as JwtAuthenticationToken

                    // changes as per the access_token JWT's format needed to be done here
                    val scope:JSONArray = (auth.principal as Jwt).getClaim("scope")

                    var allow: Boolean = scope.asSequence().any { scopes.contains(it.toString().toLowerCase()) }

                    /*val requestScopes: Set<String> = auth.get
                    val allow = requestScopes.containsAll(scopes!!)
                    Mono.just(AuthorizationDecision(allow))*/
                    Mono.just(AuthorizationDecision(allow))
                }
    }

}