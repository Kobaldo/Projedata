package com.estoque.projedata;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private RawMaterialsRepository rawMaterialsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetProductionSuccessfully() throws Exception {
        Product chair = new Product();
        chair.setName("CHAIR");
        HashMap<String, Integer> ingredients = new HashMap<>();
        ingredients.put("WOOD", 10);
        chair.setIngredients(ingredients);

        RawMaterials wood = new RawMaterials();
        wood.setName("WOOD");
        wood.setQuantity(50);

        when(productRepository.findAll(eq(Sort.by("price").descending()))).thenReturn(List.of(chair));
        when(rawMaterialsRepository.findAll()).thenReturn(List.of(wood));

        mockMvc.perform(get("/api/products/production"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.CHAIR").value(5));
    }

    @Test
    public void testCreateProduct() throws Exception {
        Product product = new Product();
        product.setName("chair");
        HashMap<String, Integer> ingredients = new HashMap<>();
        ingredients.put("wood", 10);
        product.setIngredients(ingredients);
        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("CHAIR");
        HashMap<String, Integer> savedIngredients = new HashMap<>();
        savedIngredients.put("WOOD", 10);
        savedProduct.setIngredients(savedIngredients);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("CHAIR"))
                .andExpect(jsonPath("$.ingredients.WOOD").value(10));
    }

    @Test
    public void testListAllProducts() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("TABLE");

        when(productRepository.findAll()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("TABLE"));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("OLD NAME");

        Product updatedDetails = new Product();
        updatedDetails.setName("New Name");
        updatedDetails.setPrice(100.0);
        updatedDetails.setIngredients(new HashMap<>());

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NEW NAME"));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        when(productRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteProductNotFound() throws Exception {
        when(productRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNotFound());
    }
}