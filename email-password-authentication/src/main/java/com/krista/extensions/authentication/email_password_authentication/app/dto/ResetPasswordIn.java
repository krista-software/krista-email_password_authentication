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

public class ResetPasswordIn {

    private final String emailId;
    private final String code;
    private final String newPassword;

    public ResetPasswordIn(String emailId, String code, String newPassword) {
        this.emailId = emailId;
        this.code = code;
        this.newPassword = newPassword;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getCode() {
        return code;
    }

    public String getNewPassword() {
        return newPassword;
    }

}
