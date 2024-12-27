package com.example.produits.Config;

import com.example.produits.Filter.JwtFilter;
import com.example.produits.Service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsServices;
    private final JwtUtils jwtUtils;

    /**
     * Bean pour l'encodeur de mots de passe.
     * BCrypt est une méthode de hachage  recommandée pour stocker
     * de manière sécurisée les mots de passe.
     *
     * @return Un PasswordEncoder utilisant l'algorithme BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean pour créer et configurer l'AuthenticationManager.
     * C'est lui qui gère la logique d'authentification (comparaison du mot de passe, etc.).
     *
     * @param http                   Objet HttpSecurity que Spring Security nous fournit
     * @param passwordEncoder        Le bean PasswordEncoder
     * @param customUserDetailsService Le service pour charger les utilisateurs (UserDetails)
     * @return L'AuthenticationManager configuré
     * @throws Exception En cas de problème lors de la construction
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder, CustomUserDetailsService customUserDetailsService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();

    }

    /**
     * Bean principal pour configurer la chaîne de filtres de Spring Security.
     * C'est ici que l'on définit:
     *  - Les règles de protection (autorisation, etc.)
     *  - Les filtres qui s'exécuteront avant/après l'authentification
     *
     * @param http Objet HttpSecurity qui permet de définir la config de sécurité
     * @return Un SecurityFilterChain qui englobe toutes les règles
     * @throws Exception Si un problème de configuration survient
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( auth ->
                        auth.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/distance/**").permitAll()
                                .anyRequest().authenticated())
                .addFilterBefore(new JwtFilter(customUserDetailsServices,jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
