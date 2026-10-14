package com.prathyusha.chubb.policy.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Policy {

    private UUID id;

    private String policyNumber;

    private String policyholderName;

    private LineOfBusiness lineOfBusiness;

    private PolicyStatus status;

    private BigDecimal premiumAmount;

    private String currency;

    private LocalDate effectiveDate;

    private LocalDate expiryDate;

    private String region;

    private String underwriter;

    private boolean flaggedForReview;

    private Instant createdAt;

    private Instant updatedAt;

    public Policy() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getPolicyholderName() {
        return policyholderName;
    }

    public void setPolicyholderName(String policyholderName) {
        this.policyholderName = policyholderName;
    }

    public LineOfBusiness getLineOfBusiness() {
        return lineOfBusiness;
    }

    public void setLineOfBusiness(LineOfBusiness lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUnderwriter() {
        return underwriter;
    }

    public void setUnderwriter(String underwriter) {
        this.underwriter = underwriter;
    }

    public boolean isFlaggedForReview() {
        return flaggedForReview;
    }

    public void setFlaggedForReview(boolean flaggedForReview) {
        this.flaggedForReview = flaggedForReview;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}