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

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import app.krista.extension.authorization.MustAuthenticateException;
import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extension.authorization.RequestAuthenticator;
import app.krista.extension.request.ProtoRequest;
import app.krista.extension.request.ProtoResponse;
import app.krista.extension.request.protos.http.HttpRequest;
import app.krista.ksdk.authentication.SessionManager;
import app.krista.ksdk.context.RuntimeContext;
import app.krista.model.field.NamedField;
import com.krista.extensions.authentication.email_password_authentication.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class ExtensionRequestAuthenticator implements RequestAuthenticator {

    private final RuntimeContext runtimeContext;
    private final SessionManager sessionManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionRequestAuthenticator.class);

    @Inject
    public ExtensionRequestAuthenticator(RuntimeContext runtimeContext, SessionManager sessionManager) {
        this.runtimeContext = runtimeContext;
        this.sessionManager = sessionManager;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public Set<String> getSupportedProtocols() {
        return null;
    }

    @Override
    public String getAuthenticatedAccountId(ProtoRequest protoRequest) {
        if (protoRequest instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) protoRequest;
            String path = httpRequest.getUri().getPath();
            if (path.equals(Constants.getFullApiPath(Constants.CHANGE_PASSWORD))) {
                String cookie = httpRequest.getHeader("Cookie");
                String xKristaContext = getCookie(cookie, Constants.X_KRISTA_CONTEXT);
                if (Objects.isNull(xKristaContext)) {
                    LOGGER.error("X-Krista-Context is null for the request");
                }
                String clientSessionId = ClientSessionIdHelper.decodeClientSessionId(xKristaContext);
                if (clientSessionId == null) {
                    LOGGER.error("ClientSessionId is null for the request: {}", Constants.CLIENT_SESSION_ID);
                    // Not authenticated
                    return null;
                }
                String accountId = sessionManager.lookupAccountId(clientSessionId);
                LOGGER.info("Received accountId: {}", accountId);
                return accountId;
            } else if (Constants.KRISTA_ADMIN_PATHS.contains(path)) {
                return runtimeContext.getKristaAccount().getAccountId();
            }
        }
        return null;
    }

    private String getCookie(String cookie, String key) {
        String[] cookies = cookie.split(";");
        for (String c : cookies) {
            if (c.trim().startsWith(key)) {
                return c.split("=")[1];
            }
        }
        return null;
    }

    @Override
    public boolean setServiceAuthorization(String s) {
        return false;
    }

    @Override
    public Map<String, NamedField> getAttributeFields() {
        return null;
    }

    @Override
    public ProtoResponse getMustAuthenticateResponse(MustAuthenticateException e, ProtoRequest protoRequest) {
        return null;
    }

    @Override
    public AuthorizationResponse getMustAuthenticateResponse(MustAuthenticateException e) {
        return null;
    }

    @Override
    public ProtoResponse getMustAuthorizeResponse(MustAuthorizeException e, ProtoRequest protoRequest) {
        return null;
    }

    @Override
    public AuthorizationResponse getMustAuthorizeResponse(MustAuthorizeException e) {
        return null;
    }

}