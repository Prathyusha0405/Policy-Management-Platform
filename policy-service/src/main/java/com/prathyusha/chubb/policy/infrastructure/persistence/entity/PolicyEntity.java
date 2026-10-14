package com.prathyusha.chubb.policy.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.prathyusha.chubb.policy.domain.model.LineOfBusiness;
import com.prathyusha.chubb.policy.domain.model.PolicyStatus;

@Entity
@Table(
    name = "policies",
    indexes = {
        @Index(name = "idx_policy_status", columnList = "status"),
        @Index(name = "idx_policy_lob", columnList = "line_of_business"),
        @Index(name = "idx_policy_region", columnList = "region"),
        @Index(name = "idx_policy_effective_date", columnList = "effective_date"),
        @Index(name = "idx_policy_expiry_date", columnList = "expiry_date")
    }
)
public class PolicyEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(
        name = "policy_number",
        nullable = false,
        unique = true,
        length = 20
    )
    private String policyNumber;

    @Column(
        name = "policyholder_name",
        nullable = false,
        length = 255
    )
    private String policyholderName;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "line_of_business",
        nullable = false,
        length = 30
    )
    private LineOfBusiness lineOfBusiness;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    private PolicyStatus status;

    @Column(
        name = "premium_amount",
        nullable = false,
        precision = 19,
        scale = 2
    )
    private BigDecimal premiumAmount;

    @Column(
        name = "currency",
        nullable = false,
        length = 3
    )
    private String currency;

    @Column(
        name = "effective_date",
        nullable = false
    )
    private LocalDate effectiveDate;

    @Column(
        name = "expiry_date",
        nullable = false
    )
    private LocalDate expiryDate;

    @Column(
        name = "region",
        nullable = false,
        length = 50
    )
    private String region;

    @Column(
        name = "underwriter",
        nullable = false,
        length = 255
    )
    private String underwriter;

    @Column(
        name = "flagged_for_review",
        nullable = false
    )
    private boolean flaggedForReview;

    @Column(
        name = "created_at",
        nullable = false
    )
    private Instant createdAt;

    @Column(
        name = "updated_at",
        nullable = false
    )
    private Instant updatedAt;

    public PolicyEntity() {
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