package com.prathyusha.chubb.policy.api.dto;

import java.math.BigDecimal;
import java.util.Map;

public record PolicySummaryResponse(
        Map<String, Long> countByStatus,
        Map<String, BigDecimal> totalPremiumByLineOfBusiness,
        long expiringSoonCount
) {
}