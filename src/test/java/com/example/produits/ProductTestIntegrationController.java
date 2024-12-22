package com.example.produits;

import com.example.produits.DTO.ProductDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProductTestIntegrationController {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetProductById_Exist() throws Exception {
        // Créer un produit
        ProductDTO productDTO = new ProductDTO();
        productDTO.setCode("ART_097");
        productDTO.setName("Chaise en bois");
        productDTO.setDescription("Chaise en bois beige avec assise en mousse");
        productDTO.setAvailable(true);
        productDTO.setPrice(24.99);
        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //récupérer l'id du produit
        int idProduct = objectMapper.readValue(response, ProductDTO.class).getId();

        //requête get pour vérifier que le produit est crée
        mockMvc.perform(get("/products/" + idProduct)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Vérification du statut
                .andExpect(jsonPath("$.id").value(idProduct))
                .andExpect(jsonPath("$.name").value(productDTO.getName()))
                .andExpect(jsonPath("$.available").value(productDTO.getAvailable()))
                .andExpect(jsonPath("$.code").value(productDTO.getCode()))
                .andExpect(jsonPath("$.description").value(productDTO.getDescription()))
                .andExpect(jsonPath("$.price").value(productDTO.getPrice()));
    }

    @Test void testGetProductById_NotFound() throws Exception {
        mockMvc.perform(get("/products/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found"));
    }

    @Test void testDeleteProductById_NotFound() throws Exception {
        mockMvc.perform(delete("/products/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found"));
    }
    @Test void testDeleteProductById() throws Exception {
        // Créer un produit
        ProductDTO productDTO = new ProductDTO();
        productDTO.setCode("ART_098");
        productDTO.setName("Chaise en bois");
        productDTO.setDescription("Chaise en bois beige avec assise en mousse");
        productDTO.setAvailable(true);
        productDTO.setPrice(24.99);
        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //récupérer l'id du produit
        int idProduct = objectMapper.readValue(response, ProductDTO.class).getId();
        //Supprimer le produit avec requête delete puis vérifier qu'il n'existe plus
        mockMvc.perform(delete("/products/" + idProduct)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/products/" + idProduct)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found"));
    }

    @Test void testUpdateProductById_NotFound() throws Exception {
        mockMvc.perform(put("/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                        .andExpect(status().isNotFound())
                        .andExpect(content().string("Product not found"));
    }
    //TODO update with another code that exists

    //TODO update OK

    //TODO create with code already exist

    @Test void testCreateProduct_OK() throws Exception {
        // Créer un produit
        ProductDTO productDTO = new ProductDTO();
        productDTO.setCode("ART_096");
        productDTO.setName("Chaise en bois");
        productDTO.setDescription("Chaise en bois beige avec assise en mousse");
        productDTO.setAvailable(true);
        productDTO.setPrice(24.99);
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(productDTO.getName()))
                .andExpect(jsonPath("$.available").value(productDTO.getAvailable()))
                .andExpect(jsonPath("$.code").value(productDTO.getCode()))
                .andExpect(jsonPath("$.description").value(productDTO.getDescription()))
                .andExpect(jsonPath("$.price").value(productDTO.getPrice()));

    }

}
