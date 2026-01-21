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

import java.util.List;

public final class Constants {

    private Constants() {
    }

    public static final String API = "api";

    public static final String WORKSPACE_INFO = "workspaceInfo";
    public static final String IS_USER_BLOCKED = "isUserBlocked";
    public static final String ATTEMPT_LOGIN = "attemptLogin";
    public static final String X_KRISTA_CONTEXT = "X-Krista-Context";
    public static final String SEND_BLOCKED_LINK = "sendBlockedLink";
    public static final String RESET_PASSWORD = "resetPassword";
    public static final String SEND_FORGOT_LINK = "sendForgotLink";
    public static final String CHANGE_PASSWORD = "changePassword";
    public static final String CLIENT_SESSION_ID = "clientSessionId";

    public static String getFullApiPath(String end) {
        return String.format("/%s/%s", API, end);
    }

    public static List<String> KRISTA_ADMIN_PATHS = List.of(
            getFullApiPath(WORKSPACE_INFO),
            getFullApiPath(IS_USER_BLOCKED),
            getFullApiPath(ATTEMPT_LOGIN),
            getFullApiPath(SEND_BLOCKED_LINK),
            getFullApiPath(RESET_PASSWORD),
            getFullApiPath(SEND_FORGOT_LINK));
}
