package com.example.altuser.dominionstats.models;

import java.io.Serializable;

public class Expansion implements Serializable {
    private int id;
    private String expansionName;

    public Expansion() {}

    public Expansion(int id, String expansionName ) {
        this.id = id;
        this.expansionName = expansionName;
    }

    public Expansion(String expansionName) {
        this.expansionName = expansionName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExpansionName() {
        return expansionName;
    }

    public void setExpansionName(String expansionName) {
        this.expansionName = expansionName;
    }

    @Override
    public String toString() {
        return expansionName;
    }

//    @Override //FULL TO STRING METHOD
//    public String toString() {
//        return id + ":" + expansionName;
//    }
}
