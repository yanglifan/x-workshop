package com.github.yanglifan.workshop.spring.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * @author Yang Lifan
 */
@Component
public class PassportClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(PassportClient.class);

    private static final String PASSPORT_SERVICE_HOST = "http://localhost:8080/passport-service/";
    private static final String IS_INVALID_USER_URL = PASSPORT_SERVICE_HOST + "is-invalid-user?userToken={userToken}";

    private final AsyncRestTemplate asyncRestTemplate;
    private final RestTemplate restTemplate;

    public PassportClient(AsyncRestTemplate asyncRestTemplate, RestTemplate restTemplate) {
        this.asyncRestTemplate = asyncRestTemplate;
        this.restTemplate = restTemplate;
    }

    public ListenableFuture<ResponseEntity<Boolean>> isValidUserAsync(String userToken) {
        ListenableFuture<ResponseEntity<Boolean>> responseFuture =
                asyncRestTemplate.getForEntity(IS_INVALID_USER_URL, Boolean.class, userToken);
        responseFuture.addCallback(new ListenableFutureCallback<ResponseEntity<Boolean>>() {
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("Async query the user status by token {} failed", userToken, ex);
            }

            @Override
            public void onSuccess(ResponseEntity<Boolean> result) {
                if (result.getBody()) {
                    LOGGER.info("The user (token -> {}) is valid", userToken);
                } else {
                    LOGGER.info("The user (token -> {}) is invalid", userToken);
                }
            }
        });
        return responseFuture;
    }

    public Boolean isValidUser(String userToken) {
        LOGGER.info("Query the user status by the token {}", userToken);
        return restTemplate.getForObject(IS_INVALID_USER_URL, Boolean.class, userToken);
    }
}
