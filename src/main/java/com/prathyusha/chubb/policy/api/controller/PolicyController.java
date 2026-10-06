package com.prathyusha.chubb.policy.api.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.prathyusha.chubb.policy.api.dto.FlagPoliciesRequest;
import com.prathyusha.chubb.policy.api.dto.FlagPoliciesResponse;
import com.prathyusha.chubb.policy.api.dto.PolicyPageResponse;
import com.prathyusha.chubb.policy.api.dto.PolicyResponse;
import com.prathyusha.chubb.policy.api.dto.PolicySummaryResponse;
import com.prathyusha.chubb.policy.application.service.PolicyService;
import com.prathyusha.chubb.policy.domain.model.LineOfBusiness;
import com.prathyusha.chubb.policy.domain.model.PolicyStatus;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping
    public PolicyPageResponse getPolicies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) PolicyStatus status,
            @RequestParam(required = false) LineOfBusiness lineOfBusiness,
            @RequestParam(required = false) String region,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate effectiveDateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate effectiveDateTo,
            @RequestParam(required = false) String search
    ) {
        return policyService.findPolicies(
                page,
                size,
                sort,
                status,
                lineOfBusiness,
                region,
                effectiveDateFrom,
                effectiveDateTo,
                search
        );
    }

    @GetMapping("/{id}")
    public PolicyResponse getPolicyById(@PathVariable UUID id) {
        return policyService.getPolicyById(id);
    }
    @PatchMapping("/flag")
    public FlagPoliciesResponse flagPolicies(
            @Valid @RequestBody FlagPoliciesRequest request) {

        return policyService.flagPolicies(request.policyIds());
    }
    @GetMapping("/summary")
    public PolicySummaryResponse getPolicySummary() {
        return policyService.getPolicySummary();
    }
}