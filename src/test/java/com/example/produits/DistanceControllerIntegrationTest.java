package com.example.produits;

import com.example.produits.DTO.DistanceRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WithMockUser(username = "testUser", roles = {"USER"})
public class DistanceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Pour convertir un objet en JSON

    /**
     * Requête valide.
     * On s’attend à :
     *  - un code HTTP 200 (OK)
     *  - une distance > 0 dans la réponse
     */
    @Test
    void testGetDistanceOk() throws Exception {
        // On construit notre DTO de requête
        DistanceRequestDTO request = new DistanceRequestDTO();
        request.setLatitudeFrom(48.8566);   // Paris
        request.setLongitudeFrom(2.3522);
        request.setLatitudeTo(41.9028);     // Rome
        request.setLongitudeTo(12.4964);

        // On convertit l'objet en JSON
        String requestJson = objectMapper.writeValueAsString(request);

        // On exécute la requête POST /api/distance
        String responseContent = mockMvc.perform(post("/api/distance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)) // Ajout du contenu JSON ici
                .andExpect(status().isOk())      // On s'attend à 200 OK
                .andReturn()
                .getResponse()
                .getContentAsString();

        // On récupère la distance depuis la réponse
        double distance = Double.parseDouble(responseContent);

        // On vérifie que la distance est > 0
        Assertions.assertTrue(distance > 0,
                "La distance entre Paris et Rome devrait être > 0");
    }

    @Test
    void testGetDistanceInvalidRequest() throws Exception {
        // Requête incomplète ou mal formée
        DistanceRequestDTO invalidRequestDTO = new DistanceRequestDTO();
        invalidRequestDTO.setLatitudeFrom(41.9028);   // Paris
        invalidRequestDTO.setLongitudeFrom(2.3522);

        // On exécute la requête POST /api/distance
        mockMvc.perform(post("/api/distance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO))) // On envoie une requête mal formée
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isInternalServerError()); // On s'attend à un 500
               // .andExpect(status().isInternalServerError()); // On s'attend à un 400
    }

}
