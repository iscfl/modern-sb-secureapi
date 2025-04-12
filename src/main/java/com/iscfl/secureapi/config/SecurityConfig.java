package com.iscfl.secureapi.config;


import com.iscfl.secureapi.security.AuthenticationProviders;
import com.iscfl.secureapi.utils.AppConstants;
import com.iscfl.secureapi.utils.JWTtoUserConverter;
import com.iscfl.secureapi.utils.KeyUtils;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	JWTtoUserConverter jwtToUserConverter;
	@Autowired
	KeyUtils keyUtils;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	UserDetailsManager userDetailsManager;


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests((authorize) -> authorize
						.requestMatchers("/api/auth/*", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
						.anyRequest().authenticated()
				)
				.csrf(AbstractHttpConfigurer::disable)
				.cors(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.oauth2ResourceServer((oauth2Configurer) ->
						oauth2Configurer.jwt((jwtConfigurer) ->
								jwtConfigurer
										.jwtAuthenticationConverter(jwtToUserConverter).decoder(jwtAccessTokenDecoder())
						)
				)
				.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling((exceptions) -> exceptions
						.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
						.accessDeniedHandler(new BearerTokenAccessDeniedHandler())
				);
		return http.build();
	}

	@Bean
	@Primary
	JwtDecoder jwtAccessTokenDecoder() {
		return NimbusJwtDecoder.withPublicKey(keyUtils.getAccessTokenPublicKey()).build();
	}

	@Bean
	@Primary
	JwtEncoder jwtAccessTokenEncoder() {
		JWK jwk = new RSAKey
				.Builder(keyUtils.getAccessTokenPublicKey())
				.privateKey(keyUtils.getAccessTokenPrivateKey())
				.build();
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}

	@Bean
	@Qualifier("jwtRefreshTokenDecoder")
	JwtDecoder jwtRefreshTokenDecoder() {
		return NimbusJwtDecoder.withPublicKey(keyUtils.getRefreshTokenPublicKey()).build();
	}

	@Bean
	@Qualifier("jwtRefreshTokenEncoder")
	JwtEncoder jwtRefreshTokenEncoder() {
		JWK jwk = new RSAKey
				.Builder(keyUtils.getRefreshTokenPublicKey())
				.privateKey(keyUtils.getRefreshTokenPrivateKey())
				.build();
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}

	@Bean
	AuthenticationProviders authenticationProviders(){
		AuthenticationProviders authenticationProviders = new AuthenticationProviders();
		authenticationProviders.addProvider(AppConstants.REFRESHTOKEN_AUTH_PROVIDER, jwtRefreshTokenAuthProvider());
		authenticationProviders.addProvider(AppConstants.ACCESSTOKEN_AUTH_PROVIDER, jwtAccessTokenAuthProvider());
		authenticationProviders.addProvider(AppConstants.DAO_AUTH_PROVIDER, daoAuthenticationProvider());
		return authenticationProviders;
	}

	JwtAuthenticationProvider jwtRefreshTokenAuthProvider() {
		JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtRefreshTokenDecoder());
		provider.setJwtAuthenticationConverter(jwtToUserConverter);
		return provider;
	}

	JwtAuthenticationProvider jwtAccessTokenAuthProvider() {
		JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtAccessTokenDecoder());
		provider.setJwtAuthenticationConverter(jwtToUserConverter);
		return provider;
	}

	DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder);
		provider.setUserDetailsService(userDetailsManager);
		return provider;
	}
}
