package com.example.kamil.cukrowag.food;

/**
 * Created by kamil on 11.06.17.
 */

public class HasIdName {
    private int id;
    public String name;
    HasIdName() {
        this.id = -1;
        this.name = new String();
    }
    HasIdName(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public boolean equals(HasIdName o) {
        return this.getId() == o.getId();
    }
}
