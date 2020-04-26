package in.neuw.resource.demo.security;

import net.minidev.json.JSONArray;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Karanbir Singh on 04/21/2020
 */
public class HasScope implements ReactiveAuthorizationManager<AuthorizationContext> {

    private Set<String> scopes;

    public HasScope(String... scopes) {
        this.scopes = new HashSet<String>(Arrays.asList(scopes));
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext object) {
        return authentication.flatMap(a -> {
            JwtAuthenticationToken auth = (JwtAuthenticationToken) a;
            JSONArray scope = ((Jwt) auth.getPrincipal()).getClaim("scope");
            Boolean allow = scope.stream().anyMatch(s -> scopes.contains(s));
            return Mono.just(new AuthorizationDecision(allow));
        });
    }
}
