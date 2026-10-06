package com.prathyusha.chubb.policy.api.dto;

import com.prathyusha.chubb.policy.domain.model.Policy;

public final class PolicyResponseMapper {

    private PolicyResponseMapper() {
    }


public static PolicyResponse toResponse(Policy policy) {

    return new PolicyResponse(
            policy.getId(),
            policy.getPolicyNumber(),
            policy.getPolicyholderName(),
            policy.getLineOfBusiness().getValue(),
            policy.getStatus().getValue(),
            policy.getPremiumAmount(),
            policy.getCurrency(),
            policy.getEffectiveDate(),
            policy.getExpiryDate(),
            policy.getRegion(),
            policy.getUnderwriter(),
            policy.isFlaggedForReview(),
            policy.getCreatedAt(),
            policy.getUpdatedAt()
    );

    }
}