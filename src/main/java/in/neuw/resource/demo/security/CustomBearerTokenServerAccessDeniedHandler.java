package in.neuw.resource.demo.security;

import com.google.gson.Gson;
import in.neuw.resource.demo.models.ErrorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.BearerTokenErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Karanbir Singh on 04/21/2020
 */
public class CustomBearerTokenServerAccessDeniedHandler implements ServerAccessDeniedHandler {

    private String realmName;

    @Autowired
    public Gson gson;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {

        Map<String, String> parameters = new LinkedHashMap<>();

        if (this.realmName != null) {
            parameters.put("realm", this.realmName);
        }

        return exchange.getPrincipal()
                .filter(AbstractOAuth2TokenAuthenticationToken.class::isInstance)
                .map(token -> errorMessageParameters(parameters))
                .switchIfEmpty(Mono.just(parameters))
                .flatMap(params -> respond(exchange, params));
    }

    /**
     * Set the default realm name to use in the bearer token error response
     *
     * @param realmName
     */
    public final void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    private static Map<String, String> errorMessageParameters(Map<String, String> parameters) {
        parameters.put("error", BearerTokenErrorCodes.INSUFFICIENT_SCOPE);
        parameters.put("error_description", "The request requires higher privileges than provided by the access token.");
        parameters.put("error_uri", "https://tools.ietf.org/html/rfc6750#section-3.1");

        return parameters;
    }

    private Mono<Void> respond(ServerWebExchange exchange, Map<String, String> parameters) {
        String wwwAuthenticate = computeWWWAuthenticateHeaderValue(parameters);
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);

        var errorDto = new ErrorDto(parameters.get("error_description"), HttpStatus.FORBIDDEN);
        errorDto.setCode(101);
        errorDto.setError(parameters.get("error"));

        return exchange.getResponse().writeWith(Mono.just(new DefaultDataBufferFactory().wrap(gson.toJson(errorDto).getBytes())));
    }

    private static String computeWWWAuthenticateHeaderValue(Map<String, String> parameters) {
        StringBuilder wwwAuthenticate = new StringBuilder();
        wwwAuthenticate.append("Bearer");
        if (!parameters.isEmpty()) {
            wwwAuthenticate.append(" ");
            int i = 0;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                wwwAuthenticate.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                if (i != parameters.size() - 1) {
                    wwwAuthenticate.append(", ");
                }
                i++;
            }
        }

        return wwwAuthenticate.toString();
    }
}