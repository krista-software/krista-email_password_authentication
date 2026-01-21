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

package com.krista.extensions.authentication.email_password_authentication.email;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import javax.mail.AuthenticationFailedException;
import com.krista.extensions.authentication.email_password_authentication.utils.EmailPasswordAuthenticationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.krista.extensions.authentication.email_password_authentication.utils.EmailPasswordAuthenticationConstants.*;

public class EmailConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailConfiguration.class);

    public boolean isUseCustomMailServer() {
        return useCustomMailServer;
    }

    private final boolean useCustomMailServer;
    private final String host;
    private final String port;
    private final String user;
    private final String password;
    private final String from;
    private final Boolean ssl;
    private final Map<String, Object> properties;

    public EmailConfiguration(boolean useCustomMailServer, String host, String port, String user, String password,
            String from, Boolean ssl,
            Map<String, Object> properties) {
        this.useCustomMailServer = useCustomMailServer;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.from = from;
        this.ssl = ssl;
        this.properties = properties;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getFrom() {
        return from;
    }

    public Boolean getSsl() {
        return ssl;
    }

    public boolean getOrDefaultSsl() {
        return Objects.requireNonNullElse(ssl, true);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Properties toProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", getHost());
        properties.put("mail.smtp.port", getPort());
        properties.put("user", getUser());
        properties.put("password", getPassword());

        properties.put("mail.smtp.auth", "true");
        if (getOrDefaultSsl()) {
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.starttls.required", "true");
        }

        properties.put("mail.smtp.connectiontimeout", "10000");
        properties.put("mail.smtp.timeout", "30000");
        properties.put("mail.smtp.writetimeout", "30000");

        Map<String, Object> extraProperties = Objects.requireNonNullElse(getProperties(), Map.of());
        properties.putAll(extraProperties);
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmailConfiguration that = (EmailConfiguration) o;
        return Objects.equals(host, that.host) && Objects.equals(port, that.port) &&
                Objects.equals(user, that.user) && Objects.equals(password, that.password) &&
                Objects.equals(from, that.from) && Objects.equals(ssl, that.ssl) &&
                Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, user, password, from, ssl, properties);
    }

    public static EmailConfiguration getDefaultConfiguration() {
        return new EmailConfiguration(false, "smtp.gmail.com", "587",
                "noreply@krista.app", "H5JcPD4Yr9rWtgLtP5s7", null,
                null, null);
    }

    public static EmailConfiguration parse(Map<String, Object> attributes) {
        Objects.requireNonNull(attributes);
        boolean useCustomMailServer =
                (boolean) Objects.requireNonNullElse(attributes.get(USE_CUSTOM_SMTP_SETTINGS), false);
        String senderEmailAddress = (String) attributes.get(SMTP_FROM_ADDRESS);
        String smtpAccount = (String) attributes.get(SMTP_USERNAME);
        if (smtpAccount == null || smtpAccount.isBlank()) {
            smtpAccount = senderEmailAddress;
        }
        String smtpPassword = (String) attributes.get(SMTP_PASSWORD);
        String smtpHost = (String) attributes.get(SMTP_HOST);
        int smtpPort = Integer.parseInt((String) attributes.get(SMTP_PORT));
        boolean useSSL =
                (boolean) Objects.requireNonNullElse(attributes.get(USE_SSL), false);
        return new EmailConfiguration(useCustomMailServer, smtpHost, String.valueOf(smtpPort), smtpAccount,
                smtpPassword, senderEmailAddress, useSSL, null);
    }

    public static boolean isAttributesValid(Map<String, Object> attributes) {
        if (attributes == null) {
            return false;
        }
        getStringAttribute(attributes, SMTP_HOST, ERROR_HOST_REQUIRED);
        String port =
                getStringAttribute(attributes, SMTP_PORT, ERROR_PORT_REQUIRED);
        validatePort(port);
        getStringAttribute(attributes, SMTP_USERNAME, ERROR_USERNAME_REQUIRED);
        getStringAttribute(attributes, SMTP_PASSWORD, ERROR_PASSWORD_REQUIRED);
        return true;
    }

    private static String getStringAttribute(Map<String, Object> attributes, String attributeName,
            String errorMessage) {
        Object attributeValue = attributes.get(attributeName);
        if (attributeValue == null || attributeValue.toString().isBlank()) {
            throw new RuntimeException(errorMessage);
        }
        return attributeValue.toString();
    }

    private static void validatePort(String port) {
        try {
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new RuntimeException(ERROR_PORT_NOT_VALID);
        }
    }

    public static void handleSmtpChange(EmailConfiguration smtpConfiguration) {
        String toEmail = "noreply@krista.app";
        try {
            EmailConfiguration authConfig =
                    new EmailConfiguration(smtpConfiguration.useCustomMailServer, smtpConfiguration.host,
                            smtpConfiguration.port, smtpConfiguration.user, smtpConfiguration.password,
                            smtpConfiguration.from, smtpConfiguration.ssl, smtpConfiguration.properties);
            EmailSender sender = EmailSender.create(authConfig);
            sender.testEmailConfiguration(authConfig, toEmail,
                    "SMTP settings configured successfully for your Krista workspace");
        } catch (AuthenticationFailedException cause) {
            String errorMessage =
                    String.format("%s or %s is incorrect", EmailPasswordAuthenticationConstants.SMTP_USERNAME,
                            EmailPasswordAuthenticationConstants.SMTP_PASSWORD);
            LOGGER.error(errorMessage, cause);
            throw new RuntimeException(errorMessage);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

}
