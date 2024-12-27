package com.example.produits.DTO;

import jakarta.validation.constraints.NotNull;

// Classe DTO pour recevoir les coordonnées des points de départ et d'arrivée
public class DistanceRequestDTO {
    @NotNull(message = "LatitudeFrom est obligatoire")
    private double latitudeFrom;
    @NotNull(message = "longitudeFrom est obligatoire")
    private double longitudeFrom;
    @NotNull(message = "LatitudeTo est obligatoire")
    private double latitudeTo;
    @NotNull(message = "LongitudeTo est obligatoire")
    private double longitudeTo;

    // Getters et Setters
    public double getLatitudeFrom() {
        return latitudeFrom;
    }

    public void setLatitudeFrom(double latitudeFrom) {
        this.latitudeFrom = latitudeFrom;
    }

    public double getLongitudeFrom() {
        return longitudeFrom;
    }

    public void setLongitudeFrom(double longitudeFrom) {
        this.longitudeFrom = longitudeFrom;
    }

    public double getLatitudeTo() {
        return latitudeTo;
    }

    public void setLatitudeTo(double latitudeTo) {
        this.latitudeTo = latitudeTo;
    }

    public double getLongitudeTo() {
        return longitudeTo;
    }

    public void setLongitudeTo(double longitudeTo) {
        this.longitudeTo = longitudeTo;
    }
}
