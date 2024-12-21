package com.example.produits.Service;

import com.example.produits.DTO.ProductDTO;
import com.example.produits.Entity.Product;
import com.example.produits.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    private ProductDTO convertToDto(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setAvailable(product.getAvailable());
        return productDTO;
    }

    private Product convertToProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setAvailable(productDTO.getAvailable());
        return product;
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public ProductDTO getProductById(int id) {
        return productRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(()-> new RuntimeException("Product not found"));
    }
    public ProductDTO getProductByCode(String code) {
        return productRepository.findByCode(code)
                .map(this::convertToDto)
                .orElseThrow(()-> new RuntimeException("Product not found"));
    }

    public void deleteProductById(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Product not found"));
        productRepository.deleteById(product.getId());
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        // Vérifier si un produit avec le même code existe déjà
        Optional<Product> existingProduct = productRepository.findByCode(productDTO.getCode());
        if (existingProduct.isPresent()) {
            throw new IllegalArgumentException("Product with code'" + productDTO.getCode() + "' already exist");
        }
        // Convertir le DTO en entité
        Product product = convertToProduct(productDTO);
        // Sauvegarder l'entité
        Product productSave = productRepository.save(product);
        // Retourner le DTO correspondant
        return convertToDto(productSave);
        }

        public ProductDTO updateProduct(int id ,ProductDTO productDTO) {
            // Étape 1 : Récupération du produit existant
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Étape 2 : Vérification de l'unicité du code (autre produit avec le même code)
            Optional<Product> productWithSameCode = productRepository.findByCode(productDTO.getCode());
            if (productWithSameCode.isPresent() && !productWithSameCode.get().getId().equals(existingProduct.getId())) {
                throw new IllegalArgumentException(
                        "Product with code '" + productDTO.getCode() + "' already exists");
            }

            // Étape 3 : Mise à jour des champs du produit existant
            existingProduct.setName(productDTO.getName());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setCode(productDTO.getCode());
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setAvailable(productDTO.getAvailable());

            // Étape 4 : Sauvegarde du produit mis à jour
            Product updatedProduct = productRepository.save(existingProduct);

            // Étape 5 : Conversion de l'entité mise à jour en DTO et retour
            return convertToDto(updatedProduct);

        }

    }


