package com.prathyusha.chubb.policy.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.prathyusha.chubb.policy.application.dto.PolicySearchCriteria;
import com.prathyusha.chubb.policy.domain.model.LineOfBusiness;
import com.prathyusha.chubb.policy.domain.model.Policy;
import com.prathyusha.chubb.policy.domain.model.PolicyStatus;
import com.prathyusha.chubb.policy.domain.repository.PolicyRepository;
import com.prathyusha.chubb.policy.infrastructure.persistence.entity.PolicyEntity;
import com.prathyusha.chubb.policy.infrastructure.persistence.mapper.PolicyMapper;
import com.prathyusha.chubb.policy.infrastructure.persistence.specification.PolicySpecification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PolicyRepositoryAdapter implements PolicyRepository {

    private final PolicyJpaRepository policyJpaRepository;

    public PolicyRepositoryAdapter(
            PolicyJpaRepository policyJpaRepository) {

        this.policyJpaRepository = policyJpaRepository;
    }

    @Override
    public Page<Policy> findAll(
            PolicySearchCriteria criteria,
            Pageable pageable) {

        Specification<PolicyEntity> specification =
                Specification
                        .where(
                                PolicySpecification.hasStatus(
                                        criteria.status()
                                )
                        )
                        .and(
                                PolicySpecification.hasLineOfBusiness(
                                        criteria.lineOfBusiness()
                                )
                        )
                        .and(
                                PolicySpecification.hasRegion(
                                        criteria.region()
                                )
                        )
                        .and(
                                PolicySpecification.effectiveDateFrom(
                                        criteria.effectiveDateFrom()
                                )
                        )
                        .and(
                                PolicySpecification.effectiveDateTo(
                                        criteria.effectiveDateTo()
                                )
                        )
                        .and(
                                PolicySpecification.containsSearchText(
                                        criteria.search()
                                )
                        );

        return policyJpaRepository
                .findAll(specification, pageable)
                .map(PolicyMapper::toDomain);
    }

    @Override
    public Optional<Policy> findById(UUID id) {

        return policyJpaRepository.findById(id)
                .map(PolicyMapper::toDomain);
    }
    @Override
    public List<Policy> findAllByIds(List<UUID> ids) {

        return policyJpaRepository.findAllById(ids)
                .stream()
                .map(PolicyMapper::toDomain)
                .toList();
    }
    @Override
    public Policy save(Policy policy) {

        PolicyEntity entity = PolicyMapper.toEntity(policy);

        PolicyEntity savedEntity = policyJpaRepository.save(entity);

        return PolicyMapper.toDomain(savedEntity);
    }
    @Override
    public Map<PolicyStatus, Long> countByStatus() {

        return policyJpaRepository.countPoliciesByStatus()
                .stream()
                .collect(Collectors.toMap(
                        row -> (PolicyStatus) row[0],
                        row -> (Long) row[1]
                ));
    }
    @Override
    public Map<LineOfBusiness, BigDecimal> totalPremiumByLineOfBusiness() {

        return policyJpaRepository.totalPremiumByLineOfBusiness()
                .stream()
                .collect(Collectors.toMap(
                        row -> (LineOfBusiness) row[0],
                        row -> (BigDecimal) row[1]
                ));
    }
    @Override
    public long countExpiringSoon(LocalDate from, LocalDate to) {

        return policyJpaRepository.countExpiringSoon(from, to);
    }
}