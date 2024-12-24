package com.example.produits.Filter;

import com.example.produits.Config.JwtUtils;
import com.example.produits.Service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Récupération de l'en-tête Authorization de la requête HTTP
        final String authHeader = request.getHeader("Authorization");

        // Déclaration de variables pour stocker le JWT et le nom d'utilisateur
        String username = null;
        String jwt = null;

        /*
         * Vérification que l'en-tête Authorization existe et commence par "Bearer ".
         * Si oui, on extrait la partie correspondant au token JWT (après "Bearer ").
         */
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Enlève "Bearer " (7 caractères)
            username = jwtUtils.extractUsername(jwt); // Extrait le username à partir du token
        }

        /*
         * Si on a trouvé un nom d'utilisateur dans le token ET
         * que le SecurityContext n'a pas déjà d'authentification en cours,
         * alors on va vérifier la validité du token et charger l'utilisateur.
         */
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Récupère un UserDetails à partir de la couche de service (customUserDetailsService)
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // Vérifie si le token est valide (notamment expiration et correspondance du username)
            if (jwtUtils.validateToken(jwt, userDetails)) {

                /*
                 * Si le token est valide, on crée un objet Authentication
                 * (UsernamePasswordAuthenticationToken) qui contient l'utilisateur,
                 * son mot de passe (null ici, car on en a pas le besoin),
                 * ainsi que ses rôles/autorisations.
                 */
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                /*
                 * On associe quelques détails de la requête à l'objet d'authentification
                 * (adresse IP, sessionID, etc.) via WebAuthenticationDetailsSource.
                 */
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                /*
                 * Enfin, on enregistre cet objet d'authentification
                 * dans le SecurityContext de Spring.
                 * Ceci permet au framework de considérer l'utilisateur comme authentifié
                 * pour la suite du traitement de la requête (contrôleurs, etc.).
                 */
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        /*
         * Important : appelle la suite de la chaîne des filtres
         * (sinon la requête ne parviendra jamais aux controllers ).
         */
        filterChain.doFilter(request, response);
}
}
