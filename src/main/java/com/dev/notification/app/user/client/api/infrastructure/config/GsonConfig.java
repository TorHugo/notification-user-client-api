package com.dev.notification.app.user.client.api.infrastructure.config;

import com.dev.notification.app.user.client.api.domain.value.object.Parameter;
import com.dev.notification.app.user.client.api.infrastructure.adapter.LocalDateTimeAdapter;
import com.dev.notification.app.user.client.api.infrastructure.adapter.ParameterTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class GsonConfig {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Parameter.class, new ParameterTypeAdapter())
                .create();
    }
}