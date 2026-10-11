package com.prathyusha.chubb.policy.service;

import com.prathyusha.chubb.policy.api.dto.FlagPoliciesResponse;
import com.prathyusha.chubb.policy.api.dto.PolicyPageResponse;
import com.prathyusha.chubb.policy.api.dto.PolicyResponse;
import com.prathyusha.chubb.policy.api.dto.PolicySummaryResponse;
import com.prathyusha.chubb.policy.application.exception.PolicyNotFoundException;
import com.prathyusha.chubb.policy.application.service.PolicyService;
import com.prathyusha.chubb.policy.domain.model.LineOfBusiness;
import com.prathyusha.chubb.policy.domain.model.Policy;
import com.prathyusha.chubb.policy.domain.model.PolicyStatus;
import com.prathyusha.chubb.policy.domain.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyService policyService;

    private Policy policy;
    private UUID policyId;

    @BeforeEach
    void setUp() {

        policyId = UUID.randomUUID();

        policy = new Policy();

        policy.setId(policyId);
        policy.setPolicyNumber("POL-100001");
        policy.setPolicyholderName("Acme Corporation");
        policy.setLineOfBusiness(LineOfBusiness.PROPERTY);
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setPremiumAmount(new BigDecimal("125000.00"));
        policy.setCurrency("SGD");
        policy.setEffectiveDate(LocalDate.of(2026, 1, 1));
        policy.setExpiryDate(LocalDate.of(2026, 12, 31));
        policy.setRegion("Singapore");
        policy.setUnderwriter("James Wilson");
        policy.setFlaggedForReview(false);
        policy.setCreatedAt(Instant.now());
        policy.setUpdatedAt(Instant.now());
    }

    @Test
    void shouldFindPoliciesSuccessfully() {

        PageImpl<Policy> page = new PageImpl<>(
                List.of(policy),
                PageRequest.of(0, 20, Sort.by("policyNumber").ascending()),
                1
        );

        when(policyRepository.findAll(any(), any()))
                .thenReturn(page);

        PolicyPageResponse response = policyService.findPolicies(
                0,
                20,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals("POL-100001", response.content().get(0).policyNumber());
        assertEquals(0, response.page());
        assertEquals(20, response.size());
        assertEquals(1, response.totalElements());
        assertEquals(1, response.totalPages());

        verify(policyRepository).findAll(any(), any());
    }

    @Test
    void shouldRejectNegativePage() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> policyService.findPolicies(
                                -1,
                                20,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                );

        assertEquals("Page must be greater than or equal to 0", exception.getMessage());

        verifyNoInteractions(policyRepository);
    }

    @Test
    void shouldRejectInvalidSize() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> policyService.findPolicies(
                                0,
                                101,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                );

        assertEquals("Size must be between 1 and 100", exception.getMessage());

        verifyNoInteractions(policyRepository);
    }

    @Test
    void shouldRejectInvalidSortField() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> policyService.findPolicies(
                                0,
                                20,
                                "invalidField,asc",
                                null,
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                );

        assertEquals(
                "Invalid sort field: invalidField",
                exception.getMessage()
        );

        verifyNoInteractions(policyRepository);
    }
    
    @Test
    void shouldGetPolicyByIdSuccessfully() {

        when(policyRepository.findById(policyId))
                .thenReturn(Optional.of(policy));

        PolicyResponse response =
                policyService.getPolicyById(policyId);

        assertNotNull(response);
        assertEquals(policyId, response.id());
        assertEquals("POL-100001", response.policyNumber());
        assertEquals("Acme Corporation", response.policyholderName());
        assertEquals("Active", response.status());
        assertEquals("Property", response.lineOfBusiness());

        verify(policyRepository).findById(policyId);
    }

    @Test
    void shouldThrowExceptionWhenPolicyDoesNotExist() {

        when(policyRepository.findById(policyId))
                .thenReturn(Optional.empty());

        assertThrows(
                PolicyNotFoundException.class,
                () -> policyService.getPolicyById(policyId)
        );

        verify(policyRepository).findById(policyId);
    }

    @Test
    void shouldFlagPoliciesSuccessfully() {

        UUID secondPolicyId = UUID.randomUUID();

        Policy secondPolicy = new Policy();
        secondPolicy.setId(secondPolicyId);
        secondPolicy.setPolicyNumber("POL-100002");
        secondPolicy.setFlaggedForReview(false);

        when(policyRepository.findAllByIds(
                List.of(policyId, secondPolicyId)))
                .thenReturn(List.of(policy, secondPolicy));

        FlagPoliciesResponse response =
                policyService.flagPolicies(
                        List.of(policyId, secondPolicyId)
                );

        assertNotNull(response);
        assertEquals(2, response.flaggedCount());
        assertEquals(
                List.of(policyId, secondPolicyId),
                response.flaggedPolicyIds()
        );

        assertTrue(policy.isFlaggedForReview());
        assertTrue(secondPolicy.isFlaggedForReview());

        verify(policyRepository, times(2))
                .save(any(Policy.class));
    }

    @Test
    void shouldThrowExceptionWhenPolicyIsMissingDuringBulkFlagging() {

        UUID missingId = UUID.randomUUID();

        when(policyRepository.findAllByIds(
                List.of(policyId, missingId)))
                .thenReturn(List.of(policy));

        assertThrows(
                PolicyNotFoundException.class,
                () -> policyService.flagPolicies(
                        List.of(policyId, missingId)
                )
        );

        verify(policyRepository, never())
                .save(any(Policy.class));
    }

    @Test
    void shouldReturnPolicySummary() {

        Map<PolicyStatus, Long> statusCounts =
                Map.of(
                        PolicyStatus.ACTIVE, 5L,
                        PolicyStatus.EXPIRED, 2L,
                        PolicyStatus.PENDING, 2L,
                        PolicyStatus.CANCELLED, 1L
                );

        Map<LineOfBusiness, BigDecimal> premiumTotals =
                Map.of(
                        LineOfBusiness.PROPERTY,
                        new BigDecimal("500000.00"),

                        LineOfBusiness.CASUALTY,
                        new BigDecimal("300000.00"),

                        LineOfBusiness.A_AND_H,
                        new BigDecimal("200000.00"),

                        LineOfBusiness.MARINE,
                        new BigDecimal("150000.00")
                );

        when(policyRepository.countByStatus())
                .thenReturn(statusCounts);

        when(policyRepository.totalPremiumByLineOfBusiness())
                .thenReturn(premiumTotals);

        when(policyRepository.countExpiringSoon(
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(3L);

        PolicySummaryResponse response =
                policyService.getPolicySummary();

        assertNotNull(response);

        assertEquals(
                5L,
                response.countByStatus().get("Active")
        );

        assertEquals(
                new BigDecimal("500000.00"),
                response.totalPremiumByLineOfBusiness()
                        .get("Property")
        );

        assertEquals(
                3L,
                response.expiringSoonCount()
        );

        verify(policyRepository).countByStatus();
        verify(policyRepository).totalPremiumByLineOfBusiness();
        verify(policyRepository)
                .countExpiringSoon(any(), any());
    }

    @Test
    void shouldFindPoliciesWithCustomSortDesc() {

        PageImpl<Policy> page = new PageImpl<>(
                List.of(policy),
                PageRequest.of(0, 20, Sort.by("policyNumber").descending()),
                1
        );

        when(policyRepository.findAll(any(), any()))
                .thenReturn(page);

        PolicyPageResponse response = policyService.findPolicies(
                0,
                20,
                "policyNumber,desc",
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertNotNull(response);
        assertEquals(1, response.content().size());
        verify(policyRepository).findAll(any(), any());
    }

    @Test
    void shouldFindPoliciesWithSortByPremiumAmount() {
        PageImpl<Policy> page = new PageImpl<>(
                List.of(policy),
                PageRequest.of(0, 20, Sort.by("premiumAmount").ascending()),
                1
        );

        when(policyRepository.findAll(any(), any())).thenReturn(page);

        PolicyPageResponse response = policyService.findPolicies(
                0,
                20,
                "premiumAmount,asc",
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertNotNull(response);
        assertEquals(1, response.content().size());
    }

    @Test
    void shouldRejectPageSizeZero() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> policyService.findPolicies(0, 0, null, null, null, null, null, null, null)
        );

        assertEquals("Size must be between 1 and 100", exception.getMessage());
        verifyNoInteractions(policyRepository);
    }

    @Test
    void shouldAcceptMaxSize100() {
        PageImpl<Policy> page = new PageImpl<>(
                List.of(policy),
                PageRequest.of(0, 100, Sort.by("policyNumber").ascending()),
                1
        );

        when(policyRepository.findAll(any(), any())).thenReturn(page);

        PolicyPageResponse response = policyService.findPolicies(0, 100, null, null, null, null, null, null, null);

        assertNotNull(response);
        assertEquals(100, response.size());
    }

    @Test
    void shouldHandleSearchTerm() {
        PageImpl<Policy> page = new PageImpl<>(
                List.of(policy),
                PageRequest.of(0, 20, Sort.by("policyNumber").ascending()),
                1
        );

        when(policyRepository.findAll(any(), any())).thenReturn(page);

        PolicyPageResponse response = policyService.findPolicies(0, 20, null, null, null, null, null, null, "Acme");

        assertNotNull(response);
        assertEquals(1, response.content().size());
    }

    @Test
    void shouldFilterByLineOfBusiness() {
        PageImpl<Policy> page = new PageImpl<>(
                List.of(policy),
                PageRequest.of(0, 20, Sort.by("policyNumber").ascending()),
                1
        );

        when(policyRepository.findAll(any(), any())).thenReturn(page);

        PolicyPageResponse response = policyService.findPolicies(0, 20, null, null, LineOfBusiness.PROPERTY, null, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.content().size());
    }

    @Test
    void shouldFilterByRegion() {
        PageImpl<Policy> page = new PageImpl<>(
                List.of(policy),
                PageRequest.of(0, 20, Sort.by("policyNumber").ascending()),
                1
        );

        when(policyRepository.findAll(any(), any())).thenReturn(page);

        PolicyPageResponse response = policyService.findPolicies(0, 20, null, null, null, "Singapore", null, null, null);

        assertNotNull(response);
        assertEquals(1, response.content().size());
    }

    @Test
    void shouldFilterByEffectiveDateRange() {
        PageImpl<Policy> page = new PageImpl<>(
                List.of(policy),
                PageRequest.of(0, 20, Sort.by("policyNumber").ascending()),
                1
        );

        when(policyRepository.findAll(any(), any())).thenReturn(page);

        PolicyPageResponse response = policyService.findPolicies(0, 20, null, null, null, null, LocalDate.of(2026,1,1), LocalDate.of(2026,12,31), null);

        assertNotNull(response);
        assertEquals(1, response.content().size());
    }

    @Test
    void shouldFlagSinglePolicy() {
        when(policyRepository.findAllByIds(List.of(policyId))).thenReturn(List.of(policy));

        FlagPoliciesResponse response = policyService.flagPolicies(List.of(policyId));

        assertNotNull(response);
        assertEquals(1, response.flaggedCount());
        assertEquals(List.of(policyId), response.flaggedPolicyIds());
        verify(policyRepository).save(any(Policy.class));
    }

    @Test
    void shouldFlagEmptyList() {
        when(policyRepository.findAllByIds(List.of())).thenReturn(List.of());

        FlagPoliciesResponse response = policyService.flagPolicies(List.of());

        assertNotNull(response);
        assertEquals(0, response.flaggedCount());
        assertEquals(List.of(), response.flaggedPolicyIds());
        verify(policyRepository, never()).save(any());
    }

    @Test
    void shouldFlagWithDuplicateIds() {
        when(policyRepository.findAllByIds(any())).thenReturn(List.of(policy));

        FlagPoliciesResponse response = policyService.flagPolicies(List.of(policyId, policyId));

        assertNotNull(response);
        assertEquals(1, response.flaggedCount());
        assertEquals(List.of(policyId), response.flaggedPolicyIds());
        verify(policyRepository).save(any(Policy.class));
    }

    @Test
    void shouldThrowWhenSaveFails() {
        when(policyRepository.findAllByIds(List.of(policyId))).thenReturn(List.of(policy));
        doThrow(new RuntimeException("DB error")).when(policyRepository).save(any(Policy.class));

        assertThrows(RuntimeException.class, () -> policyService.flagPolicies(List.of(policyId)));
    }

    @Test
    void shouldSummaryWhenNoData() {
        when(policyRepository.countByStatus()).thenReturn(Map.of());
        when(policyRepository.totalPremiumByLineOfBusiness()).thenReturn(Map.of());
        when(policyRepository.countExpiringSoon(any(), any())).thenReturn(0L);

        PolicySummaryResponse response = policyService.getPolicySummary();

        assertNotNull(response);
        assertTrue(response.countByStatus().isEmpty());
        assertTrue(response.totalPremiumByLineOfBusiness().isEmpty());
        assertEquals(0L, response.expiringSoonCount());
    }

    @Test
    void shouldCountExpiringSoonUsesDates() {
        when(policyRepository.countByStatus()).thenReturn(Map.of());
        when(policyRepository.totalPremiumByLineOfBusiness()).thenReturn(Map.of());
        when(policyRepository.countExpiringSoon(any(), any())).thenReturn(0L);

        policyService.getPolicySummary();

        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);
        verify(policyRepository).countExpiringSoon(captor.capture(), captor.capture());

        LocalDate start = captor.getAllValues().get(0);
        LocalDate end = captor.getAllValues().get(1);

        assertEquals(start.plusDays(30), end);
    }

    @Test
    void shouldValidateSortFieldCreatedAtDesc() {
        PageImpl<Policy> page = new PageImpl<>(List.of(policy), PageRequest.of(0,20, Sort.by("createdAt").descending()),1);
        when(policyRepository.findAll(any(), any())).thenReturn(page);

        PolicyPageResponse response = policyService.findPolicies(0,20, "createdAt,desc", null, null, null, null, null, null);
        assertNotNull(response);
    }

    @Test
    void shouldRejectBlankSort() {
        PageImpl<Policy> page = new PageImpl<>(List.of(policy), PageRequest.of(0,20, Sort.by("policyNumber")),1);
        when(policyRepository.findAll(any(), any())).thenReturn(page);

        PolicyPageResponse response = policyService.findPolicies(0,20, "   ", null, null, null, null, null, null);
        assertNotNull(response);
        verify(policyRepository).findAll(any(), any());
    }

    @Test
    void shouldFindPoliciesWithMultipleSortPartsExtraSpaces() {
        PageImpl<Policy> page = new PageImpl<>(List.of(policy), PageRequest.of(0,20, Sort.by("policyNumber").descending()),1);
        when(policyRepository.findAll(any(), any())).thenReturn(page);

        PolicyPageResponse response = policyService.findPolicies(0,20, " policyNumber , desc ", null, null, null, null, null, null);
        assertNotNull(response);
    }

    @Test
    void shouldFlagPoliciesPreserveOrder() {
        UUID id1 = policyId;
        UUID id2 = UUID.randomUUID();

        Policy p1 = policy;
        Policy p2 = new Policy();
        p2.setId(id2);
        p2.setPolicyNumber("POL-200");

        when(policyRepository.findAllByIds(List.of(id1, id2))).thenReturn(List.of(p1, p2));

        FlagPoliciesResponse response = policyService.flagPolicies(List.of(id1, id2));

        assertEquals(List.of(id1, id2), response.flaggedPolicyIds());
    }

    @Test
    void shouldFindPoliciesPageSizeOne() {
        PageImpl<Policy> page = new PageImpl<>(List.of(policy), PageRequest.of(0,1, Sort.by("policyNumber")),1);
        when(policyRepository.findAll(any(), any())).thenReturn(page);

        PolicyPageResponse response = policyService.findPolicies(0,1, null, null, null, null, null, null, null);
        assertEquals(1, response.size());
    }

    @Test
    void shouldFindPoliciesLargePageNumberReturnsEmpty() {
        PageImpl<Policy> page = new PageImpl<>(List.of(), PageRequest.of(10,20, Sort.by("policyNumber")), 0);
        when(policyRepository.findAll(any(), any())).thenReturn(page);

        PolicyPageResponse response = policyService.findPolicies(10,20, null, null, null, null, null, null, null);
        assertEquals(0, response.content().size());
    }

    @Test
    void shouldRejectNullInputForFlagging() {
        assertThrows(NullPointerException.class, () -> policyService.flagPolicies(null));
    }
}