package com.example.produits.Controller;


import com.example.produits.DTO.DistanceRequestDTO;
import com.example.produits.Service.OSRMClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
// Contrôleur REST pour gérer les requêtes liées aux distances
@RestController
@RequestMapping("/api/distance") // URL de base pour toutes les routes du contrôleur
public class DistanceController {
    @Autowired
    private OSRMClient osrmClient; // Injection du service OSRMClient

    /**
     * Endpoint pour calculer la distance routière entre deux points.
     *
     * @param request DTO contenant les coordonnées des deux points.
     * @return La distance en mètres entre les deux points.
     */
    @PostMapping()
    public double getDistance(@Valid @RequestBody DistanceRequestDTO request) {
        return osrmClient.getDistance(
                request.getLatitudeFrom(),
                request.getLongitudeFrom(),
                request.getLatitudeTo(),
                request.getLongitudeTo()
        );
    }
}
