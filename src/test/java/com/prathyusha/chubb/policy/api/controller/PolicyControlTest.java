package com.prathyusha.chubb.policy.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.prathyusha.chubb.policy.api.dto.FlagPoliciesResponse;
import com.prathyusha.chubb.policy.api.dto.PolicyPageResponse;
import com.prathyusha.chubb.policy.api.dto.PolicySummaryResponse;
import com.prathyusha.chubb.policy.api.exception.GlobalExceptionHandler;
import com.prathyusha.chubb.policy.application.exception.PolicyNotFoundException;
import com.prathyusha.chubb.policy.application.service.PolicyService;
import com.prathyusha.chubb.policy.domain.model.LineOfBusiness;
import com.prathyusha.chubb.policy.domain.model.PolicyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PolicyController.class)
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PolicyService policyService;

    @Test
    void shouldReturnPolicyPageWithDefaults() throws Exception {

        when(policyService.findPolicies(
                0, 20, null, null, null, null, null, null, null))
                .thenReturn(new PolicyPageResponse(List.of(), 0, 20, 0L, 0));

        mockMvc.perform(get("/api/v1/policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void shouldBindQueryParametersToService() throws Exception {

        when(policyService.findPolicies(
                1, 5, "premiumAmount,desc",
                PolicyStatus.ACTIVE, LineOfBusiness.PROPERTY, "Singapore",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), "acme"))
                .thenReturn(new PolicyPageResponse(List.of(), 1, 5, 0L, 0));

        mockMvc.perform(get("/api/v1/policies")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "premiumAmount,desc")
                        .param("status", "ACTIVE")
                        .param("lineOfBusiness", "PROPERTY")
                        .param("region", "Singapore")
                        .param("effectiveDateFrom", "2026-01-01")
                        .param("effectiveDateTo", "2026-12-31")
                        .param("search", "acme"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5));
    }

    @Test
    void shouldReturnBadRequestForMalformedDate() throws Exception {

        mockMvc.perform(get("/api/v1/policies")
                        .param("effectiveDateFrom", "not-a-date"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(policyService);
    }

    @Test
    void shouldReturnBadRequestForMalformedPolicyId() throws Exception {

        mockMvc.perform(get("/api/v1/policies/not-a-uuid"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(policyService);
    }

    @Test
    void shouldReturnNotFoundWhenPolicyDoesNotExist() throws Exception {

        UUID id = UUID.randomUUID();
        when(policyService.getPolicyById(id))
                .thenThrow(new PolicyNotFoundException(id));

        mockMvc.perform(get("/api/v1/policies/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Policy not found: " + id));
    }

    @Test
    void shouldFlagPolicies() throws Exception {

        UUID id = UUID.randomUUID();
        when(policyService.flagPolicies(List.of(id)))
                .thenReturn(new FlagPoliciesResponse(List.of(id), 1));

        mockMvc.perform(patch("/api/v1/policies/flag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"policyIds\":[\"" + id + "\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flaggedCount").value(1))
                .andExpect(jsonPath("$.flaggedPolicyIds[0]").value(id.toString()));

        verify(policyService).flagPolicies(List.of(id));
    }

    @Test
    void shouldRejectFlagRequestWithEmptyIds() throws Exception {

        mockMvc.perform(patch("/api/v1/policies/flag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"policyIds\":[]}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(policyService);
    }

    @Test
    void shouldRejectFlagRequestWithoutBody() throws Exception {

        mockMvc.perform(patch("/api/v1/policies/flag")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(policyService);
    }

    @Test
    void shouldReturnNotFoundWhenFlaggingMissingPolicy() throws Exception {

        UUID id = UUID.randomUUID();
        when(policyService.flagPolicies(any()))
                .thenThrow(new PolicyNotFoundException(id));

        mockMvc.perform(patch("/api/v1/policies/flag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"policyIds\":[\"" + id + "\"]}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Policy not found: " + id));
    }

    @Test
    void shouldReturnSummary() throws Exception {

        when(policyService.getPolicySummary())
                .thenReturn(new PolicySummaryResponse(
                        Map.of("Active", 5L),
                        Map.of("Property", new BigDecimal("500000.00")),
                        3L));

        mockMvc.perform(get("/api/v1/policies/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countByStatus.Active").value(5))
                .andExpect(jsonPath("$.totalPremiumByLineOfBusiness.Property")
                        .value(500000.00))
                .andExpect(jsonPath("$.expiringSoonCount").value(3));
    }
}
 