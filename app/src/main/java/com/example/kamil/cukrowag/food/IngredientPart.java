package com.example.kamil.cukrowag.food;

import java.io.Serializable;

/**
 * Created by kamil on 12.06.17.
 */

public class IngredientPart implements Serializable {
    public Ingredient ingredient;

    public IngredientPart(Ingredient ingredient, double howmuch) throws Exception {
        this.ingredient = ingredient;
        this.howmuch = howmuch;
        if ( howmuch < 0 ) {
            throw new Exception("howmuch < 0");
        }
    }

    public double howmuch; // in grams


    public String toString() {
        Ingredient i = ingredient;
        return String.format("%s Ilość: %.1f g\n", i.name, howmuch) +
                String.format("Kalorie:%.1fkcal Białka:%.1fg Tłuszcze:%.1fg\n", i.calories, i.protein, i.fat)+
                String.format("Węglowodany:%.1fg Błonnik:%.1fg WBT:%.2f WW:%.2f\n", i.carbs, i.fiber, i.getWBT(), i.getWW());
    }
}
