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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.mail.MessagingException;

public class EmailSender {

    private static final int MAX_RETRY_COUNT = 2;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final EmailConnection kristaEmailConnection;
    private final Map<EmailConfiguration, EmailConnection> allEmailConnections = new ConcurrentHashMap<>();

    private EmailSender(EmailConnection kristaEmailConnection) {
        this.kristaEmailConnection = kristaEmailConnection;
        this.allEmailConnections.put(kristaEmailConnection.getAuthConfig(), kristaEmailConnection);
    }

    public static EmailSender create(EmailConfiguration emailAuthConfig) {
        return new EmailSender(new EmailConnection(emailAuthConfig, MAX_RETRY_COUNT));
    }

    public void sendForgotPasswordLinkEmail(EmailConfiguration workspaceEmailConfig, String email, String link)
            throws IOException, MessagingException {
        String subject = "I think you forgot your password";
        String html = load("forgotPasswordTemplateNew.html").replace("link_template_here", link);
        sendEmail(workspaceEmailConfig, email, subject, html);
    }

    public void sendResetPasswordLinkEmail(EmailConfiguration workspaceEmailConfig, String email, String link)
            throws IOException, MessagingException {
        String subject = "Password Reset Request";
        String html = load("resetPasswordTemplate.html").replace("link_template_here", link);
        sendEmail(workspaceEmailConfig, email, subject, html);
    }

    private String load(String filename) throws IOException {
        try (InputStream is = EmailSender.class.getResourceAsStream(String.format("/%s", filename))) {
            Objects.requireNonNull(is);
            return new String(is.readAllBytes());
        }
    }

    public void testEmailConfiguration(EmailConfiguration authConfig, String to, String subject) throws Exception {
        EmailConnection emailConnection = new EmailConnection(authConfig, 1);
        EmailSendTask emailSendTask = new EmailSendTask(to, subject, "", List.of(emailConnection));
        emailSendTask.run();
        boolean success = emailSendTask.isSuccess();

        if (!success) {
            List<Exception> exceptions = emailSendTask.getExceptions();
            if (exceptions != null && !exceptions.isEmpty()) {
                throw exceptions.get(0);
            }
        }
    }
    public void sendEmail(EmailConfiguration workspaceEmailConfig,
            String to, String subject, String htmlContent) {
        List<EmailConnection> emailConnections;
        if (workspaceEmailConfig == null) {
            emailConnections = List.of(this.kristaEmailConnection);
        } else {
            EmailConnection emailConnection = allEmailConnections.computeIfAbsent(workspaceEmailConfig,
                    config -> new EmailConnection(config, MAX_RETRY_COUNT));
            emailConnections = List.of(emailConnection, this.kristaEmailConnection);
        }
        executorService.execute(new EmailSendTask(to, subject, htmlContent, emailConnections));
    }

}