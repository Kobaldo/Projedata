package com.estoque.projedata;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="rawMaterialsByProducts")
@Getter
@Setter
public class RawMaterialsByProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rawMaterialId")
    private RawMaterials rawMaterials;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;
}
