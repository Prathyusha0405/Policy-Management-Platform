package com.prathyusha.chubb.policy.api.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record FlagPoliciesRequest(

        @NotEmpty(message = "policyIds must not be empty")
        List<UUID> policyIds

) {
}