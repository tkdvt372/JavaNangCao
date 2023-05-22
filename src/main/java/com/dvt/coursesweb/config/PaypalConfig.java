package com.dvt.coursesweb.config;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaypalConfig {

    private String clientId = "AV1JBuC_ieCcJngGGw-NesLm2cskd5U1d9ffDogJ74UTbzyrJwDhikTWGAg-wsao4cZlPR-JvY-ZemUm";
    private String clientSecret = "EGHuXfqr3WpXoRLrC7GfbwoiEU2pCxJmvV5T2fmjqUtteHeQ9-dQS_0r2iSh6tj54uq6pXJAvQssCK_m";
    private String mode = "sandbox";

    @Bean
    public Map<String, String> paypalSdkConfig() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", mode);
        return configMap;
    }

    @Bean
    public OAuthTokenCredential oAuthTokenCredential() {
        return new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig());
    }

    @Bean
    public APIContext apiContext() throws PayPalRESTException {
        APIContext context = new APIContext(oAuthTokenCredential().getAccessToken());
        context.setConfigurationMap(paypalSdkConfig());
        return context;
    }
}
