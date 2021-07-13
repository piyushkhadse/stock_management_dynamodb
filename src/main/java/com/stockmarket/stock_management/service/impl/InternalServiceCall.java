package com.stockmarket.stock_management.service.impl;

import com.stockmarket.core_d.domain.Error;
import com.stockmarket.core_d.exception.ApplicationException;
import com.stockmarket.core_d.logger.StockMarketApplicationLogger;
import com.stockmarket.stock_management.domain.CompanyView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class InternalServiceCall {

    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${company.management.companyDetails.uri}")
    private String companyManagementGetCompanyDetailsUri;

    StockMarketApplicationLogger logger = StockMarketApplicationLogger.getLogger(this.getClass());

    /**
     * returns a company details by calling company management endpoint
     *
     * @param companyCode
     * @return
     */
    public CompanyView getCompanyDetails(String companyCode) {
        final String url = baseUrl + companyManagementGetCompanyDetailsUri + "/" + companyCode;
        try {
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI(url);
            return restTemplate.getForObject(uri, CompanyView.class);
        } catch (ApplicationException exception) {
            logger.error().log("Error while fetching company details url:{}", url, exception);
            throw exception;
        } catch (Exception e) {
            logger.error().log("Error while fetching company details url:{}", url, e);
            throw new ApplicationException(new Error("INTERNAL_SERVER_ERROR", "Internal Server Error"), 500);
        }
    }

}
