package com.example.kamil.cukrowag.food;

import java.io.Serializable;

/**
 * Created by kamil on 11.06.17.
 */

public class IngredientCategory extends HasIdName implements Serializable {
    public IngredientCategory(int id, String name) {
        super(id, name);
    }
}
