package com.example.kamil.cukrowag.food;

import com.example.kamil.cukrowag.util.logger;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by kamil on 10.06.17.
 */

public class Meal extends HasIdName implements Serializable {
    static final long serialVersionUID = 42L;
    public Date creationDate = new Date();
    public ArrayList<IngredientPart> ingredients = new ArrayList<IngredientPart>();

    public Meal(Meal o) {
        this.setId(-1);
        this.name = o.name;
        this.creationDate = o.creationDate;
        this.ingredients = new ArrayList<IngredientPart>();
        for(IngredientPart ip : o.ingredients) {
            try {
                this.ingredients.add(new IngredientPart(ip.ingredient, ip.howmuch));
            } catch(Exception e) {
                logger.l(e.toString());
            }
        }
    }

    public Meal() {
        super();
    }

    public Meal add(Ingredient i, double howmuch) throws Exception {
        ingredients.add(new IngredientPart(i, howmuch));
        return this;
    }
    public Meal remove(IngredientPart i) {
        ingredients.remove(i);
        return this;
    }
    public Meal remove(Ingredient todelete) throws Exception {
        if ( todelete == null ) {
            return this;
        }
        Iterator<IngredientPart> it = ingredients.iterator();
        while(it.hasNext()) {
            IngredientPart i = it.next();
            if ( i.ingredient.equals(todelete) ) {
                it.remove();
                return this;
            }
        }
        throw new Exception("Ingredient not found!");
    }
    public Ingredient sum() {
        Ingredient ret = new Ingredient();
        for(IngredientPart i : ingredients) {
            ret.add(i.ingredient, i.howmuch);
        }
        return ret;
    }
    public String toString() {
        return String.format("%s utworzony: %s \n", name, new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss").format(creationDate));
    }
}
