package com.example.foodtruck.models;

public class CartItemModel {

    public static final int CART_ITEM_VIEW = 0;
    public static final int BALANCE_DETAILS_VIEW = 1;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    ////////////////cart item view
    private String productID;
    private String cartProductImage;
    private Long freeCouponNumber;
    private Long productQuantity;
    private String cartProductName;
    private String originalProductPrice;
    private String discountedProductPrice;
    private boolean inStock;

    public CartItemModel(int type, String productID, String cartProductImage, Long freeCouponNumber, Long productQuantity, String cartProductName, String originalProductPrice, String discountedProductPrice , boolean inStock) {
        this.type = type;
        this.productID = productID;
        this.cartProductImage = cartProductImage;
        this.freeCouponNumber = freeCouponNumber;
        this.productQuantity = productQuantity;
        this.cartProductName = cartProductName;
        this.originalProductPrice = originalProductPrice;
        this.discountedProductPrice = discountedProductPrice;
        this.inStock = inStock;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getCartProductImage() {
        return cartProductImage;
    }

    public void setCartProductImage(String cartProductImage) {
        this.cartProductImage = cartProductImage;
    }

    public Long getFreeCouponNumber() {
        return freeCouponNumber;
    }

    public void setFreeCouponNumber(Long freeCouponNumber) {
        this.freeCouponNumber = freeCouponNumber;
    }

    public Long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getCartProductName() {
        return cartProductName;
    }

    public void setCartProductName(String cartProductName) {
        this.cartProductName = cartProductName;
    }

    public String getOriginalProductPrice() {
        return originalProductPrice;
    }

    public void setOriginalProductPrice(String originalProductPrice) {
        this.originalProductPrice = originalProductPrice;
    }

    public String getDiscountedProductPrice() {
        return discountedProductPrice;
    }

    public void setDiscountedProductPrice(String discountedProductPrice) {
        this.discountedProductPrice = discountedProductPrice;
    }
    ////////////////cart item view


    ////////////////balance details view

    public CartItemModel(int type) {
        this.type = type;
    }

    ////////////////balance details view

}
