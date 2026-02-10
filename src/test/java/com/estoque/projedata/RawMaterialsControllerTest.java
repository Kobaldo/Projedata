package com.estoque.projedata;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(RawMaterialsController.class)
public class RawMaterialsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RawMaterialsRepository rawMaterialRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateRawMaterial() throws Exception {
        RawMaterials inputMaterial = new RawMaterials();
        inputMaterial.setName("wood");
        inputMaterial.setQuantity(100);

        RawMaterials savedMaterial = new RawMaterials();
        savedMaterial.setId(1L);
        savedMaterial.setName("WOOD");
        savedMaterial.setQuantity(100);

        when(rawMaterialRepository.save(any(RawMaterials.class))).thenReturn(savedMaterial);

        mockMvc.perform(post("/api/raw-materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputMaterial)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("WOOD"))
                .andExpect(jsonPath("$.quantity").value(100.0));
    }

    @Test
    public void testListAllRawMaterials() throws Exception {
        RawMaterials material = new RawMaterials();
        material.setId(1L);
        material.setName("IRON");
        material.setQuantity(50);

        when(rawMaterialRepository.findAll()).thenReturn(List.of(material));

        mockMvc.perform(get("/api/raw-materials"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("IRON"));
    }

    @Test
    public void testUpdateRawMaterial() throws Exception {
        RawMaterials existingMaterial = new RawMaterials();
        existingMaterial.setId(1L);
        existingMaterial.setName("OLD MATERIAL");
        existingMaterial.setQuantity(10);

        RawMaterials updatedDetails = new RawMaterials();
        updatedDetails.setName("New Material");
        updatedDetails.setQuantity(20);

        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(existingMaterial));
        when(rawMaterialRepository.save(any(RawMaterials.class))).thenReturn(existingMaterial);

        mockMvc.perform(put("/api/raw-materials/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NEW MATERIAL"))
                .andExpect(jsonPath("$.quantity").value(20.0));
    }

    @Test
    public void testDeleteRawMaterial() throws Exception {
        when(rawMaterialRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/raw-materials/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteRawMaterialNotFound() throws Exception {
        when(rawMaterialRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/raw-materials/1"))
                .andExpect(status().isNotFound());
    }
}