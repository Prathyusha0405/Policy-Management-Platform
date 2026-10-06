package com.prathyusha.chubb.policy.domain.model;

public enum LineOfBusiness {

    PROPERTY("Property"),
    CASUALTY("Casualty"),
    A_AND_H("A&H"),
    MARINE("Marine");

    private final String value;

    LineOfBusiness(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}