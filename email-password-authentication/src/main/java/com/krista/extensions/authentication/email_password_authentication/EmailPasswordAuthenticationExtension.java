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

package com.krista.extensions.authentication.email_password_authentication;

import java.util.Map;
import javax.inject.Inject;
import app.krista.extension.authorization.RequestAuthenticator;
import app.krista.extension.impl.anno.*;
import com.krista.extensions.authentication.email_password_authentication.app.ExtensionRequestAuthenticator;
import com.krista.extensions.authentication.email_password_authentication.email.EmailConfiguration;
import com.krista.extensions.authentication.email_password_authentication.utils.EmailPasswordAuthenticationConstants;
import org.glassfish.hk2.api.ServiceLocator;

@Field(name = EmailPasswordAuthenticationConstants.USE_CUSTOM_SMTP_SETTINGS, type = "Switch", required = false)
@Field(name = EmailPasswordAuthenticationConstants.SMTP_HOST, type = "Text", required = false)
@Field(name = EmailPasswordAuthenticationConstants.SMTP_PORT, type = "Text", required = false)
@Field(name = EmailPasswordAuthenticationConstants.SMTP_USERNAME, type = "Text", required = false)
@Field(name = EmailPasswordAuthenticationConstants.SMTP_PASSWORD, type = "Text", required = false, attributes = @Attribute(name = "isSecured", value = "true"))
@Field(name = EmailPasswordAuthenticationConstants.SMTP_FROM_ADDRESS, type = "Text", required = false)
@Field(name = EmailPasswordAuthenticationConstants.USE_SSL, type = "Switch", required = false)
@Java(version = Java.Version.JAVA_21)
@StaticResource(path = "docs", file = "docs")
@Extension(version = "1.0.4", requireWorkspaceAdminRights = true, name = " Email password Authentication")
public class EmailPasswordAuthenticationExtension {

    private final ServiceLocator serviceLocator;

    @Inject
    public EmailPasswordAuthenticationExtension(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    @InvokerRequest(InvokerRequest.Type.CUSTOM_TABS)
    public Map<String, String> getCustomTabs() {
        return Map.of("Documentation", "static/docs");
    }

    @InvokerRequest(InvokerRequest.Type.VALIDATE_ATTRIBUTES)
    public void validateAttributes(Map<String, Object> attributes) {
        if (attributes != null) {
            boolean useCustomMailServer =
                    (boolean) attributes.getOrDefault(EmailPasswordAuthenticationConstants.USE_CUSTOM_SMTP_SETTINGS,
                            false);

            if (useCustomMailServer) {
                if (!EmailConfiguration.isAttributesValid(attributes)) {
                    throw new RuntimeException("Please enter valid Attributes");
                }

                EmailConfiguration emailConfiguration = EmailConfiguration.parse(attributes);
                EmailConfiguration.handleSmtpChange(emailConfiguration);
            }
        }
    }

    @InvokerRequest(InvokerRequest.Type.AUTHENTICATOR)
    public RequestAuthenticator getAuthenticatedAccountId() {
        return serviceLocator.create(ExtensionRequestAuthenticator.class);
    }

}
