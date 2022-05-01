package com.example.foodtruck.models;

public class MyAddressesModel {
    private String myAddressName;
    private String myFullAddress;
    private String myAddressMobNo;
    private Boolean selectedAddress;
    private String pinCode;

    public MyAddressesModel(String myAddressName, String myFullAddress, String myAddressMobNo, Boolean selectedAddress , String pinCode) {
        this.myAddressName = myAddressName;
        this.myFullAddress = myFullAddress;
        this.myAddressMobNo = myAddressMobNo;
        this.selectedAddress = selectedAddress;
        this.pinCode = pinCode;
    }

    public String getMyAddressName() {
        return myAddressName;
    }

    public void setMyAddressName(String myAddressName) {
        this.myAddressName = myAddressName;
    }

    public String getMyFullAddress() {
        return myFullAddress;
    }

    public void setMyFullAddress(String myFullAddress) {
        this.myFullAddress = myFullAddress;
    }

    public String getMyAddressMobNo() {
        return myAddressMobNo;
    }

    public void setMyAddressMobNo(String myAddressMobNo) {
        this.myAddressMobNo = myAddressMobNo;
    }

    public Boolean getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(Boolean selectedAddress) {
        this.selectedAddress = selectedAddress;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }
}
