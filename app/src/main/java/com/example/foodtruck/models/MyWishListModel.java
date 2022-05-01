package com.example.foodtruck.models;

public class MyWishListModel {
    private  String productID;
    private String productImage;
    private long offersNumber;
    private String averageRating;
    private Long totalRating;
    private String productName;
    private String productOriginalPrice;
    private String productDiscountedPrice;
//    private boolean inStock;

    public MyWishListModel(String productID, String productImage,  String averageRating, Long totalRating, String productName, String productOriginalPrice, String productDiscountedPrice) {
        this.productID = productID;
        this.productImage = productImage;
//        this.offersNumber = offersNumber;
        this.averageRating = averageRating;
        this.totalRating = totalRating;
        this.productName = productName;
        this.productOriginalPrice = productOriginalPrice;
        this.productDiscountedPrice = productDiscountedPrice;
//        this.inStock = inStock;
    }


    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public long getOffersNumber() {
        return offersNumber;
    }

    public void setOffersNumber(long offersNumber) {
        this.offersNumber = offersNumber;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public Long getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(Long totalRating) {
        this.totalRating = totalRating;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductOriginalPrice() {
        return productOriginalPrice;
    }

    public void setProductOriginalPrice(String productOriginalPrice) {
        this.productOriginalPrice = productOriginalPrice;
    }

    public String getProductDiscountedPrice() {
        return productDiscountedPrice;
    }

    public void setProductDiscountedPrice(String productDiscountedPrice) {
        this.productDiscountedPrice = productDiscountedPrice;
    }
}
