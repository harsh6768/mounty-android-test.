package com.technohack.mounty_exam_text.models;

public class Products {

    String imageUrl;
    String productTitle;
    String productDesc;
    double costPrice;
    double sellingPrice;

    public Products() {
    }

    public Products(String imageUrl, String productTitle, String productDesc, double costPrice, double sellingPrice) {
        this.imageUrl = imageUrl;
        this.productTitle = productTitle;
        this.productDesc = productDesc;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
}
