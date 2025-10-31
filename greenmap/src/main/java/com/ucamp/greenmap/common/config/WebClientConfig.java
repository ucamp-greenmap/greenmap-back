package com.ucamp.greenmap.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;


@Configuration
public class WebClientConfig {

    @Value("${naver.api.base-url}")
    private String naverUrl;

    @Bean
    public WebClient naverWebClient() throws SSLException {

        return WebClient.builder()
                .baseUrl(naverUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    WebClient kepcoClient(
            @Value("${kepco.base-url}") String baseUrl,
            @Value("${kepco.timeout-sec}") long timeoutSec
    ) {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)
                )
                .build();

        return WebClient.builder()
                .baseUrl(baseUrl)
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofSeconds(timeoutSec))
                ))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
                .build();
    }
}
