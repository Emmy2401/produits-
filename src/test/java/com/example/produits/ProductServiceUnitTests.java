package com.example.produits;
import com.example.produits.DTO.ProductDTO;
import com.example.produits.Entity.Product;
import com.example.produits.Repository.ProductRepository;
import com.example.produits.Service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class ProductServiceUnitTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    public ProductServiceUnitTests() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetProductById_Exist() {
        // Préparer les données
        Product product = new Product();
        product.setId(1);
        product.setCode("ART_096");
        product.setName("Chaise en bois");
        product.setDescription("Chaise en bois beige avec assise en mousse");
        product.setAvailable(true);
        product.setPrice(24.99);

        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        // Appeler la méthode
        ProductDTO result = productService.getProductById(1);

        // Vérifier les résultats
        assertNotNull(result);
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getCode(), result.getCode());
        assertEquals(product.getDescription(), result.getDescription());
        assertEquals(product.getPrice(), result.getPrice());
        assertEquals(product.getAvailable(), result.getAvailable());

        verify(productRepository, times(1)).findById(1);
    }

    @Test
    public void testGetProductById_NotFound() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.getProductById(999));

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository, times(1)).findById(999);
    }

    @Test
    public void testDeleteProductById_NotFound() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.deleteProductById(999));

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository, times(1)).findById(999);
        verify(productRepository, never()).deleteById(999);
    }

    @Test
    public void testDeleteProductById() {
        Product product = new Product();
        product.setId(1);
        product.setCode("ART_096");

        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        productService.deleteProductById(1);

        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).deleteById(1);
    }

    @Test
    public void testCreateProduct_CodeAlreadyExist() {
        Product existingProduct = new Product();
        existingProduct.setCode("ART_096");

        when(productRepository.findByCode("ART_096")).thenReturn(Optional.of(existingProduct));

        ProductDTO newProduct = new ProductDTO();
        newProduct.setCode("ART_096");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.createProduct(newProduct));

        assertEquals("code already exist", exception.getMessage());
        verify(productRepository, times(1)).findByCode("ART_096");
        verify(productRepository, never()).save(any(Product.class));
    }
    @Test
    public void testCreateProduct_OK() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setCode("ART_097");
        productDTO.setName("Table en bois");
        productDTO.setDescription("Table en bois massif");
        productDTO.setAvailable(true);
        productDTO.setPrice(199.99);

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(1);
            return savedProduct;
        });

        ProductDTO result = productService.createProduct(productDTO);

        assertNotNull(result);
        assertEquals("ART_097", result.getCode());
        assertEquals("Table en bois", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testUpdateProduct_SAMECODE_OK() {
        Product existingProduct = new Product();
        existingProduct.setId(1);
        existingProduct.setCode("ART_097");
        existingProduct.setName("Table en bois");
        existingProduct.setDescription("Table en bois massif");
        existingProduct.setAvailable(true);
        existingProduct.setPrice(199.99);

        ProductDTO updatedDTO = new ProductDTO();
        updatedDTO.setCode("ART_097");
        updatedDTO.setName("Table en chêne");
        updatedDTO.setDescription("Table en bois de chêne");
        updatedDTO.setAvailable(false);
        updatedDTO.setPrice(299.99);

        when(productRepository.findById(1)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductDTO result = productService.updateProduct(1, updatedDTO);

        assertNotNull(result);
        assertEquals("ART_097", result.getCode());
        assertEquals("Table en chêne", result.getName());
        assertEquals("Table en bois de chêne", result.getDescription());
        assertFalse(result.getAvailable());
        assertEquals(299.99, result.getPrice());

        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testUpdateProduct_NOTSAMECODE_OK() {
        Product existingProduct = new Product();
        existingProduct.setId(1);
        existingProduct.setCode("ART_097");
        existingProduct.setName("Table en bois");
        existingProduct.setDescription("Table en bois massif");
        existingProduct.setAvailable(true);
        existingProduct.setPrice(199.99);

        ProductDTO updatedDTO = new ProductDTO();
        updatedDTO.setCode("ART_050");
        updatedDTO.setName("Table en chêne");
        updatedDTO.setDescription("Table en bois de chêne");
        updatedDTO.setAvailable(false);
        updatedDTO.setPrice(299.99);

        when(productRepository.findById(1)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductDTO result = productService.updateProduct(1, updatedDTO);

        assertNotNull(result);
        assertEquals("ART_050", result.getCode());
        assertEquals("Table en chêne", result.getName());
        assertEquals("Table en bois de chêne", result.getDescription());
        assertFalse(result.getAvailable());
        assertEquals(299.99, result.getPrice());

        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(any(Product.class));
    }
    @Test
    public void testUpdateProduct_CodeAlreadyExist() {
        Product existingProduct = new Product();
        existingProduct.setId(1);
        existingProduct.setCode("ART_096");

        Product conflictingProduct = new Product();
        conflictingProduct.setId(2);
        conflictingProduct.setCode("ART_096");

        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setId(1);
        updateDTO.setCode("ART_096");

        when(productRepository.findById(1)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findByCode("ART_096")).thenReturn(Optional.of(conflictingProduct));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.updateProduct(1, updateDTO));

        assertEquals("code already exist", exception.getMessage());
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).findByCode("ART_096");
        verify(productRepository, never()).save(any(Product.class));
    }


}

