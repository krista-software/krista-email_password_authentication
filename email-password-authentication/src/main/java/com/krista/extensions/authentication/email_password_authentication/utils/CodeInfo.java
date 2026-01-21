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

package com.krista.extensions.authentication.email_password_authentication.utils;

public class CodeInfo {

    private final String code;
    private final long creationTime;
    private final String emailId;
    private final String redirectUrl;
    private final ResetReason resetReason;

    public CodeInfo(String code, long creationTime, String emailId, String redirectUrl, ResetReason resetReason) {
        this.code = code;
        this.creationTime = creationTime;
        this.emailId = emailId;
        this.redirectUrl = redirectUrl;
        this.resetReason = resetReason;
    }

    public String getCode() {
        return code;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public ResetReason getResetReason() {
        return resetReason;
    }

    public String getCodeKey() {
        return getCodeKey(code);
    }

    public String getEmailKey() {
        return getEmailKey(emailId);
    }

    public static String getCodeKey(String code) {
        return String.format("code/id/%s", code);
    }

    public static String getEmailKey(String emailId) {
        return String.format("code/email/%s", emailId);
    }
}
