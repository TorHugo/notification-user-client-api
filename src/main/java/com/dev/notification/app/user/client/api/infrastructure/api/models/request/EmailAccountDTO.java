package com.dev.notification.app.user.client.api.infrastructure.api.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmailAccountDTO(
        @JsonProperty("email") @NotBlank String email
) {
}
