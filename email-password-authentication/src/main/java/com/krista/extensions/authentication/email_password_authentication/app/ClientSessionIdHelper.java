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

import com.google.gson.Gson;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ClientSessionIdHelper {

    static String decodeClientSessionId(String encodedRequestContext) {
        String requestContextString = URLDecoder.decode(encodedRequestContext, StandardCharsets.UTF_8);
        return new Gson().fromJson(requestContextString, ClientSession.class).getClientSessionId();
    }
}
