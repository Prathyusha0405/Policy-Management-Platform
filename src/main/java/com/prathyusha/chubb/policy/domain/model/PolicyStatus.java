package com.prathyusha.chubb.policy.domain.model;

public enum PolicyStatus {

    ACTIVE("Active"),
    EXPIRED("Expired"),
    PENDING("Pending"),
    CANCELLED("Cancelled");

    private final String value;

    PolicyStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}