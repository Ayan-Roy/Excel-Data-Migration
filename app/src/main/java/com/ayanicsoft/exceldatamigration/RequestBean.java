package com.ayanicsoft.exceldatamigration;

import androidx.annotation.Keep;

@Keep
public class RequestBean {

    String sku;
    String location;

    public RequestBean(String sku, String location) {
        this.sku = sku;
        this.location = location;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
