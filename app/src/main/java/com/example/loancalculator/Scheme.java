package com.example.loancalculator;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "schemes")
public class Scheme {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String ltvType;
    private int price;
    private float minLimit;
    private float maxLimit;
    private float interest;

    public Scheme() {
    }

    public Scheme(String name, String ltvType, int price, float minLimit, float maxLimit, float interest) {
        this.name = name;
        this.ltvType = ltvType;
        this.price = price;
        this.minLimit = minLimit;
        this.maxLimit = maxLimit;
        this.interest = interest;
    }

    // Getters and setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLtvType() { return ltvType; }
    public void setLtvType(String ltvType) { this.ltvType = ltvType; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public float getMinLimit() { return minLimit; }
    public void setMinLimit(float minLimit) { this.minLimit = minLimit; }

    public float getMaxLimit() { return maxLimit; }
    public void setMaxLimit(float maxLimit) { this.maxLimit = maxLimit; }

    public float getInterest() { return interest; }
    public void setInterest(float interest) { this.interest = interest; }

}
