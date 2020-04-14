package `in`.neuw.resource.demo.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.server.resource.BearerTokenError
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*
import `in`.neuw.resource.demo.models.ErrorDto
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.oauth2.server.resource.BearerTokenErrorCodes.INVALID_TOKEN

@Component
class CustomBearerTokenServerAuthenticationEntryPoint: ServerAuthenticationEntryPoint {

    @Autowired
    private lateinit var jacksonObjectMapper: ObjectMapper

    private var realmName: String? = null

    fun setRealmName(realmName: String?) {
        this.realmName = realmName
    }

    override fun commence(exchange: ServerWebExchange, authException: AuthenticationException): Mono<Void>? {
        return Mono.defer {
            val status = getStatus(authException)
            val parameters = createParameters(authException)
            val wwwAuthenticate = computeWWWAuthenticateHeaderValue(parameters)
            val response = exchange.response
            response.headers[HttpHeaders.WWW_AUTHENTICATE] = wwwAuthenticate
            response.statusCode = status

            if(authException is OAuth2AuthenticationException) {
                response.headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
                if (authException.error is BearerTokenError) {
                    var errorDto = ErrorDto(authException.error.description, (authException.error as BearerTokenError).httpStatus)
                    errorDto.code = 101
                    errorDto.error = authException.error.errorCode
                    return@defer response.writeWith(Mono.just(
                            DefaultDataBufferFactory().wrap(jacksonObjectMapper.writeValueAsBytes(errorDto))))
                }
                return@defer response.writeWith(Mono.just(
                        DefaultDataBufferFactory().wrap(jacksonObjectMapper.writeValueAsBytes(authException.error))))
            } else if (authException is AuthenticationCredentialsNotFoundException) {
                val ex:AuthenticationCredentialsNotFoundException = authException as AuthenticationCredentialsNotFoundException
                response.headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
                var errorDto = ErrorDto(ex.message, HttpStatus.UNAUTHORIZED)
                errorDto.code = 102
                errorDto.error = INVALID_TOKEN
                return@defer response.writeWith(Mono.just(
                        DefaultDataBufferFactory().wrap(jacksonObjectMapper.writeValueAsBytes(errorDto))))
            }
            response.setComplete()
        }
    }

    private fun createParameters(authException: AuthenticationException): Map<String, String?> {
        val parameters: MutableMap<String, String?> = LinkedHashMap()
        if (realmName != null) {
            parameters["realm"] = realmName
        }
        if (authException is OAuth2AuthenticationException) {
            val error = authException.error
            parameters["error"] = error.errorCode
            if (StringUtils.hasText(error.description)) {
                parameters["error_description"] = error.description
            }
            if (StringUtils.hasText(error.uri)) {
                parameters["error_uri"] = error.uri
            }
            if (error is BearerTokenError) {
                val bearerTokenError = error
                if (StringUtils.hasText(bearerTokenError.scope)) {
                    parameters["scope"] = bearerTokenError.scope
                }
            }
        }
        return parameters
    }

    private fun getStatus(authException: AuthenticationException): HttpStatus {
        if (authException is OAuth2AuthenticationException) {
            val error = authException.error
            if (error is BearerTokenError) {
                return error.httpStatus
            }
        }
        return HttpStatus.UNAUTHORIZED
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