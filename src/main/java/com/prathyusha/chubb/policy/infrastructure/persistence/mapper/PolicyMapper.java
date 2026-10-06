package com.prathyusha.chubb.policy.infrastructure.persistence.mapper;

import com.prathyusha.chubb.policy.domain.model.Policy;
import com.prathyusha.chubb.policy.infrastructure.persistence.entity.PolicyEntity;

public final class PolicyMapper {

    private PolicyMapper() {
    }

    public static Policy toDomain(PolicyEntity entity) {

        Policy policy = new Policy();

        policy.setId(entity.getId());
        policy.setPolicyNumber(entity.getPolicyNumber());
        policy.setPolicyholderName(entity.getPolicyholderName());
        policy.setLineOfBusiness(entity.getLineOfBusiness());
        policy.setStatus(entity.getStatus());
        policy.setPremiumAmount(entity.getPremiumAmount());
        policy.setCurrency(entity.getCurrency());
        policy.setEffectiveDate(entity.getEffectiveDate());
        policy.setExpiryDate(entity.getExpiryDate());
        policy.setRegion(entity.getRegion());
        policy.setUnderwriter(entity.getUnderwriter());
        policy.setFlaggedForReview(entity.isFlaggedForReview());
        policy.setCreatedAt(entity.getCreatedAt());
        policy.setUpdatedAt(entity.getUpdatedAt());

        return policy;
    }

    public static PolicyEntity toEntity(Policy policy) {

        PolicyEntity entity = new PolicyEntity();

        entity.setId(policy.getId());
        entity.setPolicyNumber(policy.getPolicyNumber());
        entity.setPolicyholderName(policy.getPolicyholderName());
        entity.setLineOfBusiness(policy.getLineOfBusiness());
        entity.setStatus(policy.getStatus());
        entity.setPremiumAmount(policy.getPremiumAmount());
        entity.setCurrency(policy.getCurrency());
        entity.setEffectiveDate(policy.getEffectiveDate());
        entity.setExpiryDate(policy.getExpiryDate());
        entity.setRegion(policy.getRegion());
        entity.setUnderwriter(policy.getUnderwriter());
        entity.setFlaggedForReview(policy.isFlaggedForReview());
        entity.setCreatedAt(policy.getCreatedAt());
        entity.setUpdatedAt(policy.getUpdatedAt());

        return entity;
    }
}