package com.example.produits.Service;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

// Service pour interagir avec l'API OSRM (Open Source Routing Machine)
@Service
public class OSRMClient {
    // Client HTTP réactif utilisé pour appeler des API REST
    private final WebClient webClient;
    // Constructeur : initialise le client avec l'URL de base de l'API OSRM
    public OSRMClient() {
        this.webClient = WebClient.create("http://router.project-osrm.org");
    }
    /**
     * Méthode pour calculer la distance routière entre deux points géographiques.
     *
     * @param lat1 Latitude du point de départ.
     * @param lon1 Longitude du point de départ.
     * @param lat2 Latitude du point d'arrivée.
     * @param lon2 Longitude du point d'arrivée.
     * @return La distance en mètres entre les deux points.
     */
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        // Construction de l'URL pour appeler l'API OSRM
        String url = String.format(
                "/route/v1/driving/%f,%f;%f,%f?overview=false",
                lon1, lat1, lon2, lat2
        );

        // Appel de l'API via WebClient
        return webClient.get()
                .uri(url) // URL relative construite précédemment
                .retrieve() // Envoie la requête HTTP
                .bodyToMono(OSRMResponse.class) // Conversion de la réponse JSON en objet Java
                .block() // Récupère le résultat de manière synchrone
                .getRoutes()
                .get(0)
                .getDistance(); // Extrait la distance de la première route dans la réponse
    }

    // Classe interne pour modéliser la réponse JSON de l'API OSRM
    static class OSRMResponse {
        private List<Route> routes;

        public List<Route> getRoutes() {
            return routes;
        }

        static class Route {
            private double distance;

            public double getDistance() {
                return distance;
            }
        }
    }
}
