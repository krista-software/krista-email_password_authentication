/*
 * Email Password Authentication Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>. 
 */

package com.krista.extensions.authentication.email_password_authentication.app;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

import app.krista.extension.authorization.Authenticated;
import app.krista.extension.executor.Invoker;
import app.krista.ksdk.accounts.Account;
import app.krista.ksdk.accounts.AccountProvider;
import app.krista.ksdk.authentication.PasswordDatabase;
import app.krista.ksdk.authentication.SessionManager;
import app.krista.ksdk.context.AuthorizationContext;
import app.krista.ksdk.workspace.Workspace;
import com.google.gson.JsonObject;
import com.krista.extensions.authentication.email_password_authentication.app.dto.*;
import com.krista.extensions.authentication.email_password_authentication.email.EmailConfiguration;
import com.krista.extensions.authentication.email_password_authentication.email.EmailSender;
import com.krista.extensions.authentication.email_password_authentication.service.CodeService;
import com.krista.extensions.authentication.email_password_authentication.utils.CodeInfo;
import com.krista.extensions.authentication.email_password_authentication.utils.EmailPasswordAuthenticationConstants;
import com.krista.extensions.authentication.email_password_authentication.utils.KnownError;
import com.krista.extensions.authentication.email_password_authentication.utils.ResetReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.krista.extensions.authentication.email_password_authentication.utils.Constants.*;
import static com.krista.extensions.authentication.email_password_authentication.utils.EmailPasswordAuthenticationConstants.*;
import static javax.ws.rs.core.HttpHeaders.*;

// FIXME:- ensure account is not blocked in all APIs
@SuppressWarnings("deprecation")
@Path(API)
public class ApiResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiResource.class);

    private final AccountProvider accountProvider;
    private final SessionManager sessionManager;
    private final PasswordDatabase passwordDatabase;
    private final Workspace workspace;
    private final CodeService codeService;
    private final Invoker invoker;
    private final AuthorizationContext authorizationContext;

    @Inject
    public ApiResource(AccountProvider accountProvider, SessionManager sessionManager,
                       PasswordDatabase passwordDatabase, Workspace workspace, CodeService codeService, Invoker invoker,
                       AuthorizationContext authorizationContext) {
        this.accountProvider = accountProvider;
        this.sessionManager = sessionManager;
        this.passwordDatabase = passwordDatabase;
        this.workspace = workspace;
        this.codeService = codeService;
        this.invoker = invoker;
        this.authorizationContext = authorizationContext;
    }

    @GET
    @Path(WORKSPACE_INFO)
    public WorkspaceInfo getWorkspaceInfo() {
        return new WorkspaceInfo(workspace.getWorkspaceId(), workspace.getWorkspaceName());
    }

    @POST
    @Path(IS_USER_BLOCKED)
    public UserBlocked.Out isUserBlocked(UserBlocked.In input) {
        if (input.getEmailId() == null || input.getEmailId().isEmpty()) {
            throw new KnownError(EmailPasswordAuthenticationConstants.EMAIL_IS_REQUIRED);
        }
        Account account = accountProvider.lookupAccount(input.getEmailId());
        if (account == null) {
            LOGGER.info("Account not found for email:{}", input.getEmailId());
            return new UserBlocked.Out(false);
        }
        boolean isBlocked = passwordDatabase.authenticationAttemptsLeft(account) == 0;
        LOGGER.debug("{} is actually blocked.", account.getAccountId());
        return new UserBlocked.Out(isBlocked);
    }

    @POST
    @Path(ATTEMPT_LOGIN)
    public Response attemptLogin(AttemptLoginIn attemptLoginIn, @CookieParam(X_KRISTA_CONTEXT) Cookie xKristaContext) {
        Account userAccount = accountProvider.lookupAccount(attemptLoginIn.getEmailId());
        if (userAccount == null) {
            return Response.ok(AttemptLoginOut.failure(new AttemptLoginOut.FailureResponse(4))).build();
        }
        int attemptsLeft = passwordDatabase.authenticationAttemptsLeft(userAccount);
        if (attemptsLeft == 0) {
            return Response.ok(AttemptLoginOut.blocked()).build();
        }
        boolean validPassword = passwordDatabase.isValidPassword(userAccount, attemptLoginIn.getPassword(), true);
        if (validPassword) {
            passwordDatabase.resetAuthenticationAttempts(userAccount);
            String loginCode = attemptLoginIn.getLoginCode();
            String sessionId = sessionManager.createSession(userAccount.getAccountId(), attemptLoginIn.getSessionType(),
                    getClientSessionId(xKristaContext), loginCode);

            String encodedClientSessionId = getEncodedClientSessionId(sessionId);
            Date expdate = new Date();
            expdate.setTime(expdate.getTime() + 365L * 24 * 60 * 60 * 1000);
            String cookieExpire = "expires=" + expdate.toGMTString();
            return Response.ok(AttemptLoginOut.success(new AttemptLoginOut.SuccessResponse(sessionId, loginCode)))
                    .header(SET_COOKIE, "X-Krista-Context=" + encodedClientSessionId + "; Path=/; Secure; HttpOnly; " + cookieExpire)
                    .build();
        } else if (attemptsLeft - 1 == 0) {
            return Response.ok(AttemptLoginOut.blocked()).build();
        } else {
            return Response.ok(AttemptLoginOut.failure(new AttemptLoginOut.FailureResponse(attemptsLeft - 1))).build();
        }
    }

    private String getEncodedClientSessionId(String sessionId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("clientSessionId", sessionId);
        return URLEncoder.encode(jsonObject.toString(), StandardCharsets.UTF_8);
    }

    private String getClientSessionId(Cookie kristaContext) {
        if (kristaContext == null) {
            return null;
        }
        String xKristaContext = kristaContext.getValue();
        if (Objects.isNull(xKristaContext)) {
            return null;
        }
        return ClientSessionIdHelper.decodeClientSessionId(xKristaContext);
    }

    @POST
    @Path(SEND_FORGOT_LINK)
    public Response sendForgotLink(SendForgotLinkIn input) {
        try {
            Account userAccount = accountProvider.lookupAccount(input.getEmailId());
            if (userAccount == null) {
                return Response.ok().build();
            }
            EmailConfiguration emailConfiguration = getEmailConfiguration();
            EmailSender sender = EmailSender.create(emailConfiguration);
            String forgotPasswordEmailLink = codeService.generateNewLink(input.getEmailId(), input.getRedirectUrl(),
                    ResetReason.FORGET_PASSWORD);
            sender.sendForgotPasswordLinkEmail(emailConfiguration, input.getEmailId(), forgotPasswordEmailLink);
            return Response.ok().build();
        } catch (MessagingException | IOException cause) {
            throw new RuntimeException(cause);
        }
    }

    @POST
    @Path(SEND_BLOCKED_LINK)
    public Response sendBlockedLink(SendBlockedIn input) {
        try {
            EmailConfiguration emailConfiguration = getEmailConfiguration();
            EmailSender sender = EmailSender.create(emailConfiguration);
            String link =
                    codeService.generateNewLink(input.getEmailId(), input.getRedirectUrl(), ResetReason.MAX_ATTEMPTS);
            sender.sendResetPasswordLinkEmail(emailConfiguration, input.getEmailId(), link);
            return Response.ok().build();
        } catch (MessagingException | IOException cause) {
            throw new RuntimeException(cause);
        }
    }

    @POST
    @Path(RESET_PASSWORD)
    public ResetPasswordOut resetPassword(ResetPasswordIn resetPasswordIn) {
        if (resetPasswordIn.getNewPassword().isEmpty()) {
            // Validated from the FE
            throw new KnownError("Password cannot be empty");
        }
        CodeInfo codeInfo = codeService.getCodeInfo(resetPasswordIn.getCode());
        if (codeInfo == null) {
            throw new KnownError("Link is expired");
        }
        Account userAccount = accountProvider.lookupAccount(codeInfo.getEmailId());
        if (userAccount == null) {
            throw new KnownError("Failed to change the password");
        }
        boolean success = passwordDatabase.changePassword(userAccount, resetPasswordIn.getNewPassword());
        if (!success) {
            LOGGER.error("Failed to change password of account: {}-{}", userAccount.getAccountId(),
                    resetPasswordIn.getEmailId());
            throw new KnownError("Failed to change the password");
        }
        passwordDatabase.resetAuthenticationAttempts(userAccount);
        codeService.deleteCode(codeInfo.getCode());
        return new ResetPasswordOut(codeInfo.getEmailId(), codeInfo.getRedirectUrl());
    }

    @Authenticated
    @POST
    @Path(CHANGE_PASSWORD)
    public Response changePassword(ChangePasswordIn changePasswordIn) {
        Account authorizedAccount = authorizationContext.getAuthorizedAccount();
        if (changePasswordIn.getNewPassword().isEmpty()) {
            throw new KnownError(PASSWORD_IS_EMPTY);
        }
        boolean validPassword =
                passwordDatabase.isValidPassword(authorizedAccount, changePasswordIn.getOldPassword(), false);
        if (!validPassword) {
            // Generic Message
            throw new KnownError(AUTH_GENERIC_ERROR);
        }
        boolean success = passwordDatabase.changePassword(authorizedAccount, changePasswordIn.getNewPassword());
        if (!success) {
            throw new KnownError(OPERATION_FAILED);
        }
        passwordDatabase.resetAuthenticationAttempts(authorizedAccount);
        return Response.ok().build();
    }

    private EmailConfiguration getEmailConfiguration() {
        EmailConfiguration emailConfiguration;
        Map<String, Object> attributes = this.invoker.getAttributes();
        if (attributes != null) {
            boolean useCustomMailServer =
                    (boolean) Objects.requireNonNullElse(attributes.get(USE_CUSTOM_SMTP_SETTINGS), false);
            if (!useCustomMailServer) {
                emailConfiguration = EmailConfiguration.getDefaultConfiguration();
            } else {
                emailConfiguration = EmailConfiguration.parse(attributes);
            }
        } else {
            emailConfiguration = EmailConfiguration.getDefaultConfiguration();
        }
        return emailConfiguration;
    }

}
