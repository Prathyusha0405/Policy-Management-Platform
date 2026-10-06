package com.prathyusha.chubb.policy.application.dto;

import java.time.LocalDate;

import com.prathyusha.chubb.policy.domain.model.LineOfBusiness;
import com.prathyusha.chubb.policy.domain.model.PolicyStatus;

public record PolicySearchCriteria(

        PolicyStatus status,

        LineOfBusiness lineOfBusiness,

        String region,

        LocalDate effectiveDateFrom,

        LocalDate effectiveDateTo,

        String search
) {
}