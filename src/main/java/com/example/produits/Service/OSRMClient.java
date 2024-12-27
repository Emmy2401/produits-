package com.example.produits.Service;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Locale;

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
        // Construction correcte de l'URL avec des points comme séparateurs décimaux
        String url = String.format(
                Locale.US,
                "/route/v1/driving/%f,%f;%f,%f?overview=false",
                lon1, lat1, lon2, lat2
        );

        // Appel de l'API OSRM
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(OSRMResponse.class)
                .block()
                .getRoutes()
                .get(0)
                .getDistance();
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
