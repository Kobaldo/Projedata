package com.estoque.projedata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RawMaterialsRepository rawMaterialsRepository;

    @PostMapping
    public Product create(@RequestBody Product product) {
        HashMap<String, Integer> ingredients = new HashMap<>();
        product.getIngredients().forEach((key, value) -> {
            ingredients.put(key.toUpperCase(), value);
        });
        product.setName(product.getName().toUpperCase());
        product.setIngredients(ingredients);
        return productRepository.save(product);
    }

    @GetMapping
    public List<Product> listAll() {
        return productRepository.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update( @PathVariable Long id, @RequestBody Product updatedProduct) {
        return productRepository.findById(id).map(oldProduct -> {
            if(updatedProduct.getName() != null) {
                oldProduct.setName(updatedProduct.getName().toUpperCase());
            }
            if(updatedProduct.getPrice() != null){
                oldProduct.setPrice(updatedProduct.getPrice());
            }
            if(!updatedProduct.getIngredients().isEmpty()) {
                oldProduct.setIngredients(updatedProduct.getIngredients());
            }
            Product saved = productRepository.save(oldProduct);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping ("/production")
    public HashMap<String, Integer> getProduction(){
        List<Product> products = productRepository.findAll(Sort.by("price").descending());
        List <RawMaterials> rawMaterials = rawMaterialsRepository.findAll();
        HashMap <String, Integer> materialList = new HashMap<>();
        for(RawMaterials material : rawMaterials){
            materialList.put(material.getName().toUpperCase(), material.getQuantity());
        }
        HashMap<String, Integer> production = new HashMap<>();
        for (Product product : products){
            Set<String> keys = product.getIngredients().keySet();
            List<Integer> numberOfItems = new ArrayList<>();
            keys.forEach((key) -> {if(materialList.containsKey(key.toUpperCase())){
                numberOfItems.add(materialList.getOrDefault(key.toUpperCase(),0)/product.getIngredients().get(key.toUpperCase()));
            }
            else {
                production.put(product.getName().toUpperCase(), 0);
            }});
            if(!numberOfItems.isEmpty()) {
                Collections.sort(numberOfItems);
                production.put(product.getName().toUpperCase(), numberOfItems.get(0));
                keys.forEach((key) -> {
                    if (materialList.containsKey(key.toUpperCase())) {
                        materialList.put(key.toUpperCase(), (materialList.get(key.toUpperCase()) - (numberOfItems.get(0) * product.getIngredients().get(key.toUpperCase()))));
                    }
                });
            }
        }
        return production;
        }
    }

