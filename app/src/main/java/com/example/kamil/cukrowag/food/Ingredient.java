package com.example.kamil.cukrowag.food;

import java.io.Serializable;

/**
 * Created by kamil on 10.06.17.
 */

public class Ingredient extends HasIdName implements Serializable {
    static final long serialVersionUID = 42L;
    public IngredientCategory category = null;
    public double calories = 0; // kalorie
    public double protein = 0; // białka
    public double fat = 0; // tlkuszcze
    public double carbs = 0; // węglowodany
    public double fiber = 0; // błonnik

    public Ingredient() {
        super();
    }
    public Ingredient(int id, IngredientCategory category, String name, double calories, double protein, double fat, double carbs, double fiber) {
        super(id, name);
        this.category = category;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.fiber = fiber;
    }
    Ingredient add(Ingredient i, double howmuch) {
        setId(-1);
        category = null;
        this.name = "SUM of ingredients = meal, this field shouldn't be used!";
        calories += i.calories/100*howmuch;
        protein += i.protein/100*howmuch;
        fat += i.fat/100*howmuch;
        carbs += i.carbs/100*howmuch;
        fiber += i.fiber/100*howmuch;
        return this;
    }
    static double fatToKcal(double fat) { return fat*9; }
    static double proteinToKcal(double protein) { return protein*4; }
    static double carbsToKcal(double carbs) { return carbs*4; }
    public double getWBT() {
        return ( Ingredient.fatToKcal(fat) + Ingredient.proteinToKcal(protein) ) / 100;
    }
    public double getWW() {
        return Ingredient.carbsToKcal( carbs - fiber) / 10;
    }
    public String toString() {
        return String.format("%s\n", name) + toStringNoName();
    }
    public String toStringNoName() {
        return String.format("Kalorie: %.1fkcal; Białka: %.1fg; Tłuszcze: %.1fg;\n", calories, protein, fat)+
                String.format("Węglowodany: %.1fg; Błonnik: %.1fg; \nWBT: %.2f; WW: %.2f\n", carbs, fiber, getWBT(), getWW());
    }
}
