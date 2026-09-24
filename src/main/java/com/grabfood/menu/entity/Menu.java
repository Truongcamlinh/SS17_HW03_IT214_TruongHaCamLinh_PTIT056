package com.grabfood.menu.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "menus")
public class Menu implements Serializable {
    @Id @NotNull private Long id;
    @JsonProperty("dish_name")
    @NotBlank private String dishName;
    @NotNull @PositiveOrZero private BigDecimal price;

    protected Menu() {}
    public Menu(Long id, String dishName, BigDecimal price) {
        this.id = id; this.dishName = dishName; this.price = price;
    }
    public Long getId() { return id; }
    public String getDishName() { return dishName; }
    public BigDecimal getPrice() { return price; }
}
