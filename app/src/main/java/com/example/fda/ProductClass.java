package com.example.fda;

import java.io.Serializable;

public class ProductClass implements Serializable {
    private String name;
    private String nutrients;
    private String origin;
    private String calories;
    private String function;
    private String chemical_props;
    private String id;

    public ProductClass() {
    }

    public ProductClass(String name, String nutrients, String origin, String calories, String function, String chemical_props, String id) {
        this.name = name;
        this.nutrients = nutrients;
        this.origin = origin;
        this.calories = calories;
        this.function = function;
        this.chemical_props = chemical_props;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNutrients() {
        return nutrients;
    }

    public void setNutrients(String nutrients) {
        this.nutrients = nutrients;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getChemical_props() {
        return chemical_props;
    }

    public void setChemical_props(String chemical_props) {
        this.chemical_props = chemical_props;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
