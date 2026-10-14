package com.prathyusha.chubb.policy.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.prathyusha.chubb.policy.application.dto.PolicySearchCriteria;
import com.prathyusha.chubb.policy.domain.model.LineOfBusiness;
import com.prathyusha.chubb.policy.domain.model.Policy;
import com.prathyusha.chubb.policy.domain.model.PolicyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PolicyRepository {

    Page<Policy> findAll(
            PolicySearchCriteria criteria,
            Pageable pageable
    );

    Optional<Policy> findById(UUID id);

    List<Policy> findAllByIds(List<UUID> ids);
    Policy save(Policy policy);
    Map<PolicyStatus, Long> countByStatus();

    Map<LineOfBusiness, BigDecimal> totalPremiumByLineOfBusiness();

    
    long countExpiringSoon(LocalDate from, LocalDate to);
}