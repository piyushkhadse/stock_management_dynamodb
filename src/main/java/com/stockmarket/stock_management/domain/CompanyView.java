package com.stockmarket.stock_management.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
public class CompanyView {
    private String companyName;
    private String companyCode;
    private String companyCEO;
    private Double companyTurnover;
    private String companyWebsite;
    private String stockExchange;
    private Instant createdDate;
    private String createdBy;
    private Instant modifiedDate;
    private String modifiedBy;
}
