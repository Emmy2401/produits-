package com.example.produits;

import com.example.produits.DTO.ProductDTO;
import com.example.produits.Repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
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
    @Autowired
    private ProductRepository productRepository;
    @BeforeEach
    public void setUp() {
        // Nettoyer la base de données avant chaque test
        productRepository.deleteAll();
    }

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
    @Test void testCreateProduct_CodeAlreadyExist() throws Exception {
        // Créer un produit
        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setCode("ART_050");
        productDTO1.setName("Chaise en bois");
        productDTO1.setDescription("Chaise en bois beige avec assise en mousse");
        productDTO1.setAvailable(true);
        productDTO1.setPrice(24.99);

        // Enregistrer le premier produit dans le système
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO1)))
                .andExpect(status().isOk());

        // Créer un autre produit avec le même code
        ProductDTO productDTO2 = new ProductDTO();
        productDTO2.setCode("ART_050");
        productDTO2.setName("Table en bois");
        productDTO2.setDescription("Table en bois assortie à la chaise");
        productDTO2.setAvailable(true);
        productDTO2.setPrice(49.99);

        // Tenter d'enregistrer le deuxième produit et vérifier que l'exception est levée
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO2)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("code already exist")));
    }


    @Test
    void testUpdateProduct_CodeAlreadyExist() throws Exception {
        // Étape 1 : Créer et enregistrer un premier produit
        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setCode("CODE_001");
        productDTO1.setName("Chaise en bois");
        productDTO1.setDescription("Chaise élégante en bois clair");
        productDTO1.setAvailable(true);
        productDTO1.setPrice(29.99);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO1)))
                .andExpect(status().isOk());

        // Étape 2 : Créer et enregistrer un deuxième produit
        ProductDTO productDTO2 = new ProductDTO();
        productDTO2.setCode("CODE_002");
        productDTO2.setName("Table en bois");
        productDTO2.setDescription("Table assortie à la chaise");
        productDTO2.setAvailable(true);
        productDTO2.setPrice(79.99);

       String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO2)))
                .andExpect(status().isOk()).andReturn()
               .getResponse()
               .getContentAsString();

        // Étape 3 : Mettre à jour le deuxième produit avec un code déjà existant
        productDTO2.setCode("CODE_001");
        //récupérer l'id du produit
        int idSecondProduct = objectMapper.readValue(response, ProductDTO.class).getId();

        mockMvc.perform(put("/products/{id}", idSecondProduct) // Remplacez 2 par l'ID approprié si nécessaire
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO2)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("code already exist")));
    }

    @Test void testUpdateProduct_WithSameArtCode_OK() throws Exception {
        // Créer un produit
        ProductDTO productDTO = new ProductDTO();
        productDTO.setCode("ART_095");
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
        //modif info
        ProductDTO updatedProductDTO = new ProductDTO();
        updatedProductDTO.setCode("ART_095"); //on renvoie le même code qui ne doit pas changer ça ne doit pas faire d'erreur
        updatedProductDTO.setName("Chaise en plastique");
        updatedProductDTO.setDescription("Chaise en plastique beige");
        updatedProductDTO.setAvailable(false);
        updatedProductDTO.setPrice(15.99);

        //Effectuer une requête PUT pour mettre à jour le produit
        mockMvc.perform(put("/products/" + idProduct)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDTO)))
                .andExpect(status().isOk());


        //requête get pr verif les infos
        mockMvc.perform(get("/products/" + idProduct)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idProduct)) // Vérifie que l'ID n'a pas changé
                .andExpect(jsonPath("$.name").value(updatedProductDTO.getName()))
                .andExpect(jsonPath("$.available").value(updatedProductDTO.getAvailable()))
                .andExpect(jsonPath("$.description").value(updatedProductDTO.getDescription()))
                .andExpect(jsonPath("$.code").value(updatedProductDTO.getCode()))
                .andExpect(jsonPath("$.price").value(updatedProductDTO.getPrice()));
    }

    @Test void testUpdateProduct_WithNOTSameArtCode_OK() throws Exception {
        // Créer un produit
        ProductDTO productDTO = new ProductDTO();
        productDTO.setCode("ART_095");
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
        //modif info
        ProductDTO updatedProductDTO = new ProductDTO();
        updatedProductDTO.setCode("ART_094"); //on renvoie le même code qui ne doit pas changer ça ne doit pas faire d'erreur
        updatedProductDTO.setName("Chaise en plastique");
        updatedProductDTO.setDescription("Chaise en plastique beige");
        updatedProductDTO.setAvailable(false);
        updatedProductDTO.setPrice(15.99);

        //Effectuer une requête PUT pour mettre à jour le produit
        mockMvc.perform(put("/products/" + idProduct)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDTO)))
                .andExpect(status().isOk());


        //requête get pr verif les infos
        mockMvc.perform(get("/products/" + idProduct)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idProduct)) // Vérifie que l'ID n'a pas changé
                .andExpect(jsonPath("$.name").value(updatedProductDTO.getName()))
                .andExpect(jsonPath("$.available").value(updatedProductDTO.getAvailable()))
                .andExpect(jsonPath("$.description").value(updatedProductDTO.getDescription()))
                .andExpect(jsonPath("$.code").value(updatedProductDTO.getCode()))
                .andExpect(jsonPath("$.price").value(updatedProductDTO.getPrice()));
    }



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
