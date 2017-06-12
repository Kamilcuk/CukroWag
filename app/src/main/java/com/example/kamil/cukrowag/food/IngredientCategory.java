package com.example.kamil.cukrowag.food;

import java.io.Serializable;

/**
 * Created by kamil on 11.06.17.
 */

public class IngredientCategory extends HasId implements Serializable {
    String name = new String();
    public IngredientCategory(int id, String name) {
        setId(id);
        this.name = name;
    }
}
