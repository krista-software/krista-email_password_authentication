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

package com.krista.extensions.authentication.email_password_authentication.app.dto;

import app.krista.ksdk.authentication.SessionManager;

@SuppressWarnings("deprecation")
public final class AttemptLoginIn {

    private final String emailId;
    private final String password;
    private final String clientSessionId;
    private final String loginCode;
    private final SessionManager.SessionType sessionType;

    public AttemptLoginIn(String emailId, String password, String clientSessionId,
            SessionManager.SessionType sessionType, String loginCode) {
        this.emailId = emailId;
        this.password = password;
        this.clientSessionId = clientSessionId;
        this.sessionType = sessionType;
        this.loginCode = loginCode;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getPassword() {
        return password;
    }

    public String getClientSessionId() {
        return clientSessionId;
    }

    public String getLoginCode() {
        return loginCode;
    }

    public SessionManager.SessionType getSessionType() {
        return sessionType;
    }

}
