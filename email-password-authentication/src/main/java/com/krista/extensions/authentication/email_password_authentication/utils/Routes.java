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

public final class Routes {

    private Routes() {
    }

    public static final String password = "password";
    public static final String forgotPassword = "forgot-password";
    public static final String accountBlocked = "account-blocked";
    public static final String resetPassword = "reset-password";
    public static final String changePassword = "change-password";
    public static final String notFound = "not-found";
    public static final String emailSend = "email-send";
}
