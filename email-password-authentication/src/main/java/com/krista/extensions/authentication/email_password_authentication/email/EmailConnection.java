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


import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

public class EmailConnection {

    private final EmailConfiguration authConfig;
    private final int retryCount;
    private final Properties properties;
    private Session session;

    public EmailConnection(EmailConfiguration authConfig, int retryCount) {
        this.authConfig = authConfig;
        this.properties = authConfig.toProperties();
        this.retryCount = retryCount;
        this.getSession();
    }

    public EmailConfiguration getAuthConfig() {
        return authConfig;
    }

    public Properties getProperties() {
        return properties;
    }

    public int getRetryCount() {
        return Math.max(retryCount, 1);
    }

    public synchronized Session getSession() {
        if (session == null) {
            session = Session.getInstance(properties,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(properties.get("user").toString(),
                                    properties.get("password").toString());
                        }
                    });
        }
        return session;
    }

}
