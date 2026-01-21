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

import javax.ws.rs.core.Response;

public class KnownError extends RuntimeException {

    private final int status;

    public KnownError(String message) {
        this(message, Response.Status.BAD_REQUEST.getStatusCode());
    }

    public KnownError(String message, int status) {
        super(message);
        this.status = status;
    }

    public KnownError(String message, Throwable cause) {
        this(message, cause, Response.Status.BAD_REQUEST.getStatusCode());
    }

    public KnownError(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
