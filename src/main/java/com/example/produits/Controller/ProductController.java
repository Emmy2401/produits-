package com.example.produits.Controller;

import com.example.produits.DTO.ProductDTO;
import com.example.produits.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ProductDTO getProductById(@PathVariable int id) {
        return productService.getProductById(id);
    }
//    @RequestMapping(method = RequestMethod.GET)
//    public ProductDTO getProductById(@RequestParam(value="code") String code) {
//        return productService.getProductByCode(code);
//    }

    @PostMapping
    public ProductDTO createProduct(@RequestBody ProductDTO productDTO) {
        return productService.createProduct(productDTO);
    }

    @PutMapping ("/{id}")
    public ProductDTO updateProduct(@PathVariable int id,@RequestBody ProductDTO productDTO)
    {
        return productService.updateProduct(id,productDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable int id) {
        productService.deleteProductById(id);
    }
}
