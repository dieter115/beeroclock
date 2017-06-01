package be.flashapps.beeroclock.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dietervaesen on 30/05/17.
 */

public class Beer extends RealmObject {
    @PrimaryKey
    private String id;
    private String barCode;
    private String name;
    private String type;
    private String country;
    private String alcoholPercentage;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAlcoholPercentage() {
        return alcoholPercentage;
    }

    public void setAlcoholPercentage(String alcoholPercentage) {
        this.alcoholPercentage = alcoholPercentage;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
