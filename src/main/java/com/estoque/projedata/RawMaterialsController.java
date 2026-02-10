package com.estoque.projedata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/raw-materials")
public class RawMaterialsController {

    @Autowired
    private RawMaterialsRepository rawMaterialRepository;

    @PostMapping
    public RawMaterials create(@RequestBody RawMaterials material) {
        material.setName(material.getName().toUpperCase());
        return rawMaterialRepository.save(material);
    }

    @GetMapping
    public List<RawMaterials> listAll() {
        return rawMaterialRepository.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RawMaterials> update( @PathVariable Long id, @RequestBody RawMaterials updatedRawMaterials) {
        return rawMaterialRepository.findById(id).map(oldMaterial -> {
            if(updatedRawMaterials.getName() != null) {
                oldMaterial.setName(updatedRawMaterials.getName().toUpperCase());
            }
            if(updatedRawMaterials.getQuantity() != null){
                oldMaterial.setQuantity(updatedRawMaterials.getQuantity());
            }
            RawMaterials saved = rawMaterialRepository.save(oldMaterial);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!rawMaterialRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        rawMaterialRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
