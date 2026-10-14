package com.prathyusha.chubb.policy.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prathyusha.chubb.policy.infrastructure.persistence.entity.PolicyEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
public interface PolicyJpaRepository
        extends JpaRepository<PolicyEntity, UUID>,
                JpaSpecificationExecutor<PolicyEntity> {
	@Query("""
		    SELECT p.status, COUNT(p)
		    FROM PolicyEntity p
		    GROUP BY p.status
		""")
		List<Object[]> countPoliciesByStatus();
		
		@Query("""
			    SELECT p.lineOfBusiness, SUM(p.premiumAmount)
			    FROM PolicyEntity p
			    GROUP BY p.lineOfBusiness
			""")
			List<Object[]> totalPremiumByLineOfBusiness();
			
			@Query("""
				    SELECT COUNT(p)
				    FROM PolicyEntity p
				    WHERE p.expiryDate BETWEEN :from AND :to
				""")
				long countExpiringSoon(
				        @Param("from") LocalDate from,
				        @Param("to") LocalDate to
				);
			
}