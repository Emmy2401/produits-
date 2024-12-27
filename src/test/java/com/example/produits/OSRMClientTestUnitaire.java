package com.example.produits;


import com.example.produits.Service.OSRMClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
public class OSRMClientTestUnitaire {
    /**
     * Test "qui fonctionne" : on vérifie qu'entre Paris et Rome,
     * la distance renvoyée par l'API est supérieure à 0.
     */
    @Test
    void testGetDistanceOk() {
        OSRMClient osrmClient = new OSRMClient();
        // Paris (48.8566, 2.3522)
        // Rome  (41.9028, 12.4964)
        double distance = osrmClient.getDistance(
                48.8566, 2.3522,
                41.9028, 12.4964
        );

        // On s'attend à une distance significative entre ces deux villes
        Assertions.assertTrue(distance > 0,
                "La distance entre Paris et Rome devrait être > 0");
    }

    /**
     * Test "qui échoue" : provoque une erreur en passant une latitude incomplète (valeur hors limites).
     */
    @Test
    void testGetDistanceWithInvalidLatitude() {
        OSRMClient osrmClient = new OSRMClient();

        // Latitude invalide (-9999 est en dehors des limites géographiques)
        double invalidLatitude = -9999.0;

        // Vérifie qu'une exception est levée
        Assertions.assertThrows(Exception.class, () -> {
            osrmClient.getDistance(
                    invalidLatitude, 2.3522, // Latitude invalide
                    41.9028, 12.4964
            );
        }, "Un appel avec une latitude invalide devrait lever une exception");
    }
}
