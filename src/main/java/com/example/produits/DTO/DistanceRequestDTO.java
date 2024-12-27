package com.example.produits.DTO;

import jakarta.validation.constraints.NotNull;

// Classe DTO pour recevoir les coordonnées des points de départ et d'arrivée
public class DistanceRequestDTO {
    @NotNull(message = "LatitudeFrom est obligatoire")
    private Double latitudeFrom;
    @NotNull(message = "longitudeFrom est obligatoire")
    private Double longitudeFrom;
    @NotNull(message = "LatitudeTo est obligatoire")
    private Double latitudeTo;
    @NotNull(message = "LongitudeTo est obligatoire")
    private Double longitudeTo;

    // Getters et Setters
    public Double getLatitudeFrom() {
        return latitudeFrom;
    }

    public void setLatitudeFrom(Double latitudeFrom) {
        this.latitudeFrom = latitudeFrom;
    }

    public Double getLongitudeFrom() {
        return longitudeFrom;
    }

    public void setLongitudeFrom(Double longitudeFrom) {
        this.longitudeFrom = longitudeFrom;
    }

    public Double getLatitudeTo() {
        return latitudeTo;
    }

    public void setLatitudeTo(Double latitudeTo) {
        this.latitudeTo = latitudeTo;
    }

    public Double getLongitudeTo() {
        return longitudeTo;
    }

    public void setLongitudeTo(Double longitudeTo) {
        this.longitudeTo = longitudeTo;
    }
}
