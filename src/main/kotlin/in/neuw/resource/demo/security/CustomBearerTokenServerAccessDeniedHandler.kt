package `in`.neuw.resource.demo.security

import `in`.neuw.resource.demo.models.ErrorDto
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.oauth2.server.resource.BearerTokenErrorCodes
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.*

@Component
class CustomBearerTokenServerAccessDeniedHandler: ServerAccessDeniedHandler {

    @Autowired
    private lateinit var jacksonObjectMapper: ObjectMapper

    private var realmName: String? = null

    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException?): Mono<Void?>? {
        val parameters: MutableMap<String, String?> = LinkedHashMap()
        if (realmName != null) {
            parameters["realm"] = realmName
        }
        return exchange.getPrincipal<Principal>()
                .filter { obj: Principal? -> AbstractOAuth2TokenAuthenticationToken::class.java.isInstance(obj) }
                .map { token: Principal? -> errorMessageParameters(parameters) }
                .switchIfEmpty(Mono.just<Map<String, String?>>(parameters))
                .flatMap { params: Map<String, String?> -> respond(exchange, params) }
    }

    /**
     * Set the default realm name to use in the bearer token error response
     *
     * @param realmName
     */
    fun setRealmName(realmName: String?) {
        this.realmName = realmName
    }

    private fun errorMessageParameters(parameters: MutableMap<String, String?>): Map<String, String?> {
        parameters["error"] = BearerTokenErrorCodes.INSUFFICIENT_SCOPE
        parameters["error_description"] = "The request requires higher privileges than provided by the access token."
        parameters["error_uri"] = "https://tools.ietf.org/html/rfc6750#section-3.1"
        return parameters
    }

    private fun respond(exchange: ServerWebExchange, parameters: Map<String, String?>): Mono<Void?>? {
        return Mono.defer {
            val wwwAuthenticate = computeWWWAuthenticateHeaderValue(parameters)
            exchange.response.statusCode = HttpStatus.FORBIDDEN
            exchange.response.headers[HttpHeaders.WWW_AUTHENTICATE] = wwwAuthenticate

            var errorDto = ErrorDto(parameters.get("error_description"), HttpStatus.FORBIDDEN)
            errorDto.code = 101
            errorDto.error = parameters["error"]!!

            return@defer exchange.response.writeWith(Mono.just(
                    DefaultDataBufferFactory().wrap(jacksonObjectMapper.writeValueAsBytes(errorDto))))
        }
        //return exchange.response.setComplete()
    }

    private fun computeWWWAuthenticateHeaderValue(parameters: Map<String, String?>): String {
        val wwwAuthenticate = StringBuilder()
        wwwAuthenticate.append("Bearer")
        if (!parameters.isEmpty()) {
            wwwAuthenticate.append(" ")
            var i = 0
            for ((key, value) in parameters) {
                wwwAuthenticate.append(key).append("=\"").append(value).append("\"")
                if (i != parameters.size - 1) {
                    wwwAuthenticate.append(", ")
                }
                i++
            }
        }
        return wwwAuthenticate.toString()
    }

}