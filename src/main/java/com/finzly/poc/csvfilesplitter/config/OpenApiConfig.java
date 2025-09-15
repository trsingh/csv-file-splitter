package com.finzly.poc.csvfilesplitter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI csvFileSplitterOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CSV File Splitter API")
                        .description("Professional API for splitting large CSV files into smaller chunks")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Finzly POC Team")
                                .email("support@finzly.com")));
    }
}
