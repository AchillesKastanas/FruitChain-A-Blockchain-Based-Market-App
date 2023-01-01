package org.bmarket;

public class Product {
    String productCode;
    String title;
    String timeOfCreation;
    String price;
    String description;
    String category;
    String prevRecord;

    public Product(String productCode, String title, String timeOfCreation, String price, String description, String category, String prevRecord) {
        this.productCode = productCode;
        this.title = title;
        this.timeOfCreation = timeOfCreation;
        this.price = price;
        this.description = description;
        this.category = category;
        this.prevRecord = prevRecord;
    }

    public String[] toArray(){
        return new String[]{this.productCode, this.title, this.timeOfCreation, this.price, this.description, this.category, this.prevRecord};
    }
}
