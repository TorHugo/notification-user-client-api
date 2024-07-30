package com.dev.notification.app.user.client.api.infrastructure.service;

import com.dev.notification.app.user.client.api.application.FindAccountUseCase;
import com.dev.notification.app.user.client.api.application.SaveAccountUseCase;
import com.dev.notification.app.user.client.api.application.SendNotificationUseCase;
import com.dev.notification.app.user.client.api.domain.entity.Account;
import com.dev.notification.app.user.client.api.domain.exception.template.GatewayException;
import com.dev.notification.app.user.client.api.domain.service.AccountService;
import com.dev.notification.app.user.client.api.domain.service.EncryptionService;
import com.dev.notification.app.user.client.api.domain.service.PublishingService;
import com.dev.notification.app.user.client.api.domain.service.models.EventCreateAccountPublishing;
import com.dev.notification.app.user.client.api.domain.utils.DateUtils;
import com.dev.notification.app.user.client.api.domain.utils.HashCodeUtils;
import com.dev.notification.app.user.client.api.domain.value.object.Parameter;
import com.dev.notification.app.user.client.api.infrastructure.api.models.request.CreateAccountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final FindAccountUseCase findAccountUseCase;
    private final SaveAccountUseCase saveAccountUseCase;
    private final SendNotificationUseCase sendNotificationUseCase;
    private final EncryptionService encryptionService;
    private final PublishingService<EventCreateAccountPublishing> publishingService;

    @Value("${spring.integration.email-token.hash-digits}")
    private Integer digits;

    @Value("${spring.integration.email-token.expiration-date}")
    private Integer milliseconds;

    @Override
    public Account create(final CreateAccountDTO dto) {
        final var existingAccount = findAccountUseCase.execute(dto.email());
        if (Objects.nonNull(existingAccount)) throw new GatewayException("This account already exists! With email:", dto.email());
        final var account = Account.create(dto.firstName(), dto.lastName(), dto.email(), encryptionService.encryption(dto.password()), false);
        sendNotificationUseCase.execute(account.getEmail(), "hashcode-confirmed-account", Arrays.asList(new Parameter("hashcode", HashCodeUtils.create(digits)), new Parameter("process", "false"),new Parameter("expiration-date", DateUtils.fromMillis(milliseconds).toString())));
        publishingService.publish(EventCreateAccountPublishing.builder().account(account).build());
        return saveAccountUseCase.execute(account);
    }
}