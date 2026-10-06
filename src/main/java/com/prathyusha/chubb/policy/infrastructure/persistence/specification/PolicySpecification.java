package com.prathyusha.chubb.policy.infrastructure.persistence.specification;

import org.springframework.data.jpa.domain.Specification;

import com.prathyusha.chubb.policy.domain.model.LineOfBusiness;
import com.prathyusha.chubb.policy.domain.model.PolicyStatus;
import com.prathyusha.chubb.policy.infrastructure.persistence.entity.PolicyEntity;

import java.time.LocalDate;

public final class PolicySpecification {

    private PolicySpecification() {
    }

    public static Specification<PolicyEntity> hasStatus(
            PolicyStatus status) {

        return (root, query, criteriaBuilder) ->
                status == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("status"),
                                status
                        );
    }

    public static Specification<PolicyEntity> hasLineOfBusiness(
            LineOfBusiness lineOfBusiness) {

        return (root, query, criteriaBuilder) ->
                lineOfBusiness == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("lineOfBusiness"),
                                lineOfBusiness
                        );
    }

    public static Specification<PolicyEntity> hasRegion(
            String region) {

        return (root, query, criteriaBuilder) ->
                region == null || region.isBlank()
                        ? null
                        : criteriaBuilder.equal(
                                criteriaBuilder.lower(
                                        root.get("region")
                                ),
                                region.toLowerCase()
                        );
    }

    public static Specification<PolicyEntity> effectiveDateFrom(
            LocalDate dateFrom) {

        return (root, query, criteriaBuilder) ->
                dateFrom == null
                        ? null
                        : criteriaBuilder.greaterThanOrEqualTo(
                                root.get("effectiveDate"),
                                dateFrom
                        );
    }

    public static Specification<PolicyEntity> effectiveDateTo(
            LocalDate dateTo) {

        return (root, query, criteriaBuilder) ->
                dateTo == null
                        ? null
                        : criteriaBuilder.lessThanOrEqualTo(
                                root.get("effectiveDate"),
                                dateTo
                        );
    }

    public static Specification<PolicyEntity> containsSearchText(
            String search) {

        return (root, query, criteriaBuilder) -> {

            if (search == null || search.isBlank()) {
                return null;
            }

            String searchPattern =
                    "%" + search.toLowerCase() + "%";

            return criteriaBuilder.or(

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("policyNumber")
                            ),
                            searchPattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("policyholderName")
                            ),
                            searchPattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("underwriter")
                            ),
                            searchPattern
                    )
            );
        };
    }
}