package com.prathyusha.chubb.policy.api.dto;

import java.util.List;
import java.util.UUID;

public record FlagPoliciesResponse(
        List<UUID> flaggedPolicyIds,
        int flaggedCount
) {
}