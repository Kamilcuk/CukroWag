package com.example.kamil.cukrowag.food;

/**
 * Created by kamil on 11.06.17.
 */

public class HasId {
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    private int id = -1;

    public boolean equals(HasId o) {
        return this.getId() == o.getId();
    }
}
