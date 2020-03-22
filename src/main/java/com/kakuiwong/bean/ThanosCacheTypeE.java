package com.kakuiwong.bean;

/**
 * @author: gaoyang
 * @Description:
 */
public enum ThanosCacheTypeE {
    L1("L1"), L2("L2"), L1L2("L1L2");

    private String type;

    ThanosCacheTypeE(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
