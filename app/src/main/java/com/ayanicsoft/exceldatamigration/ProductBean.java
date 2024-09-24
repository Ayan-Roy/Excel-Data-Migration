package com.ayanicsoft.exceldatamigration;

import androidx.annotation.Keep;

@Keep
public class ProductBean {
    private String productId, location;
    private String location2, location3;
    private String insertDate;
    private String insertBy;
    private int isInStock;
    public ProductBean(){};


    public ProductBean(String productId, String location, String location2, String location3, String insertDate, String insertBy, int isInStock) {
        this.productId = productId;
        this.location = location;
        this.location2 = location2;
        this.location3 = location3;
        this.insertDate = insertDate;
        this.insertBy = insertBy;
        this.isInStock = isInStock;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation2() {
        return location2;
    }

    public void setLocation2(String location2) {
        this.location2 = location2;
    }

    public String getLocation3() {
        return location3;
    }

    public void setLocation3(String location3) {
        this.location3 = location3;
    }

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getInsertBy() {
        return insertBy;
    }

    public void setInsertBy(String insertBy) {
        this.insertBy = insertBy;
    }

    public int getIsInStock() {
        return isInStock;
    }

    public void setIsInStock(int isInStock) {
        this.isInStock = isInStock;
    }
}
