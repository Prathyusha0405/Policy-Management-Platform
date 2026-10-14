package com.prathyusha.chubb.policy.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prathyusha.chubb.policy.api.dto.FlagPoliciesResponse;
import com.prathyusha.chubb.policy.api.dto.PolicyPageResponse;
import com.prathyusha.chubb.policy.api.dto.PolicyResponse;
import com.prathyusha.chubb.policy.api.dto.PolicyResponseMapper;
import com.prathyusha.chubb.policy.api.dto.PolicySummaryResponse;
import com.prathyusha.chubb.policy.application.dto.PolicySearchCriteria;
import com.prathyusha.chubb.policy.application.exception.PolicyNotFoundException;
import com.prathyusha.chubb.policy.domain.model.LineOfBusiness;
import com.prathyusha.chubb.policy.domain.model.Policy;
import com.prathyusha.chubb.policy.domain.model.PolicyStatus;
import com.prathyusha.chubb.policy.domain.repository.PolicyRepository;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public PolicyPageResponse findPolicies(
            int page,
            int size,
            String sort,
            PolicyStatus status,
            LineOfBusiness lineOfBusiness,
            String region,
            LocalDate effectiveDateFrom,
            LocalDate effectiveDateTo,
            String search
    ) {

        validatePageParameters(page, size);

        Sort sortObject = buildSort(sort);

        Pageable pageable =
                PageRequest.of(page, size, sortObject);

        PolicySearchCriteria criteria =
                new PolicySearchCriteria(
                        status,
                        lineOfBusiness,
                        region,
                        effectiveDateFrom,
                        effectiveDateTo,
                        search
                );

        Page<com.prathyusha.chubb.policy.domain.model.Policy> result =
                policyRepository.findAll(
                        criteria,
                        pageable
                );

        return new PolicyPageResponse(
                result.getContent()
                        .stream()
                        .map(PolicyResponseMapper::toResponse)
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    private void validatePageParameters(
            int page,
            int size
    ) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page must be greater than or equal to 0"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Size must be between 1 and 100"
            );
        }
    }

    private Sort buildSort(String sort) {

        if (sort == null || sort.isBlank()) {
            return Sort.by(
                    Sort.Direction.ASC,
                    "policyNumber"
            );
        }

        String[] parts = sort.split(",");

        String field = parts[0].trim();

        Sort.Direction direction =
                parts.length > 1
                        ? parseDirection(parts[1].trim())
                        : Sort.Direction.ASC;

        validateSortField(field);

        return Sort.by(direction, field);
    }

    private Sort.Direction parseDirection(String direction) {

        if ("asc".equalsIgnoreCase(direction)) {
            return Sort.Direction.ASC;
        }

        if ("desc".equalsIgnoreCase(direction)) {
            return Sort.Direction.DESC;
        }

        throw new IllegalArgumentException(
                "Sort direction must be 'asc' or 'desc'"
        );
    }

    private void validateSortField(String field) {

        switch (field) {

            case "premiumAmount",
                 "effectiveDate",
                 "expiryDate",
                 "policyNumber",
                 "policyholderName",
                 "createdAt" -> {
                // Valid
            }

            default -> throw new IllegalArgumentException(
                    "Invalid sort field: " + field
            );
        }
    }
    public PolicyResponse getPolicyById(UUID id) {

        Policy policy = policyRepository.findById(id)
                .orElseThrow(() ->
                        new PolicyNotFoundException(id)
                );

        return PolicyResponseMapper.toResponse(policy);
    }
    @Transactional
    public FlagPoliciesResponse flagPolicies(List<UUID> policyIds) {

        List<Policy> policies = policyRepository.findAllByIds(policyIds);

        Set<UUID> foundIds = policies.stream()
                .map(Policy::getId)
                .collect(Collectors.toSet());

        List<UUID> missingIds = policyIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new PolicyNotFoundException(missingIds.get(0));
        }

        policies.forEach(policy -> policy.setFlaggedForReview(true));

        policies.forEach(policyRepository::save);

        return new FlagPoliciesResponse(
                policies.stream()
                        .map(Policy::getId)
                        .toList(),
                policies.size()
        );
    }
    
    public PolicySummaryResponse getPolicySummary() {

        Map<PolicyStatus, Long> statusCounts =
                policyRepository.countByStatus();

        Map<LineOfBusiness, BigDecimal> premiumByLob =
                policyRepository.totalPremiumByLineOfBusiness();

        LocalDate today = LocalDate.now();
        LocalDate expiringSoonDate = today.plusDays(30);

        long expiringSoonCount =
                policyRepository.countExpiringSoon(
                        today,
                        expiringSoonDate
                );

        Map<String, Long> countByStatus = statusCounts.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getValue(),
                        entry -> entry.getValue()
                ));

        Map<String, BigDecimal> totalPremiumByLob =
                premiumByLob.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey().getValue(),
                                entry -> entry.getValue()
                        ));

        return new PolicySummaryResponse(
                countByStatus,
                totalPremiumByLob,
                expiringSoonCount
        );
    }
    
}