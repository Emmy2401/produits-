package com.example.produits.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Cette classe permet de créer, signer et valider des tokens JWT.
 * Elle contient notamment la logique pour :
 *  - Générer un token basé sur un nom d'utilisateur et/ou des claims
 *  - Signer le token à l'aide d'une clé secrète
 *  - Extraire diverses informations contenues dans le token (sujet, date d'expiration, etc.)
 *  - Vérifier la validité du token (nom d'utilisateur correspondant et non-expié)
 */
@Component
public class JwtUtils {
    // Récupération de la clé secrète depuis le fichier de configuration (application.properties, par exemple).
    // Cette clé secrète est utilisée pour signer nos tokens JWT.
    @Value("${app.secret-key}")
    private String secretKey;

    // Durée de validité (en millisecondes) du token avant qu'il n'expire.
    @Value("${app.expiration-time}")
    private long expirationTime;

    /**
     * Méthode principale pour générer un token à partir du nom d'utilisateur.
     * Elle crée un Map de claims vide, et appelle createToken pour fabriquer le JWT.
     *
     * @param username Le nom d'utilisateur pour lequel on génère le token
     * @return Le token JWT généré (sous forme de String)
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims,username);
    }

    /**
     * Méthode qui construit réellement le token JWT.
     * Elle s'appuie sur la librairie JJWTS (io.jsonwebtoken) pour :
     *  - Définir les claims (données embarquées dans le token)
     *  - Définir le "subject" (souvent le nom d'utilisateur)
     *  - Définir la date de création et la date d'expiration
     *  - Signer le token avec la clé secrète et l'algorithme choisi
     *
     * @param claims  Les informations que l'on souhaite inclure dans le payload du token
     * @param subject Le sujet (souvent l'utilisateur) du token
     * @return Le token JWT sous forme de String
     */
    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Méthode privée qui retourne la clé utilisée pour signer le token.
     * Elle convertit la chaîne de caractères (secretKey) en tableau de bytes,
     * puis crée une clé spécifique (SecretKeySpec) en utilisant l'algorithme HS256.
     *
     * @return Un objet Key (SecretKeySpec) correspondant à la clé de signature
     */
    private Key getSignKey() {
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * Valide le token en vérifiant deux conditions principales :
     *  1) Le nom d'utilisateur (extrait du token) correspond à celui de l'objet UserDetails
     *  2) Le token n'est pas expiré
     *
     * @param token        Le token JWT à vérifier
     * @param userDetails  Les informations de l'utilisateur courant (principalement username)
     * @return true si le token est valide, false sinon
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token); 
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Vérifie si le token est expiré.
     * On compare la date d'expiration du token (extrait via extractExpirationDate)
     * avec la date système actuelle. Si la date d'expiration est avant la date
     * actuelle, le token n'est plus valide.
     *
     * @param token Le token JWT à vérifier
     * @return true si le token est expiré, false sinon
     */
    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    /**
     * Extrait le nom d'utilisateur (subject) contenu dans le token.
     * Utilise la méthode générique extractClaims pour lire les claims.
     *
     * @param token Le token JWT
     * @return Le nom d'utilisateur (subject) contenu dans le token
     */
    private String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration du token pour en vérifier la validité.
     *
     * @param token Le token JWT
     * @return La date d'expiration contenue dans le token
     */
    private Date extractExpirationDate(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
    /**
     * Méthode générique qui sert à extraire n'importe quelle information (claim) d'un token,
     * en appliquant une fonction (claimsResolver) qui indique quoi récupérer (subject, expiration, etc.).
     *
     * @param token          Le token JWT
     * @param claimsResolver Une fonction (souvent une référence à une méthode de Claims, ex: getSubject())
     * @param <T>            Le type de la donnée qu'on souhaite extraire
     * @return La donnée extraite (par exemple un String pour le subject, ou un Date pour l'expiration)
     */
    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Méthode interne permettant de parser le token et d'en extraire
     * la totalité des informations (claims) qu'il contient.
     * Elle utilise la clé de signature pour valider et lire le token.
     *
     * @param token Le token JWT
     * @return Un objet Claims (qui contient toutes les données du token)
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSignKey()).parseClaimsJws(token).getBody();
    }

}
