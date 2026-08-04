package com.example.inventory_service.config;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiConfig {

    private static final String GATEWAY_PREFIX = "/api/v1/inventory";

    @Bean
    public OpenApiCustomizer prefixPathsWithGatewayRoute() {
        return openApi -> {
            Paths originalPaths = openApi.getPaths();
            Paths prefixedPaths = new Paths();

            for (Map.Entry<String, PathItem> entry : originalPaths.entrySet()) {
                prefixedPaths.addPathItem(GATEWAY_PREFIX + entry.getKey(), entry.getValue());
            }

            openApi.setPaths(prefixedPaths);
        };
    }
}