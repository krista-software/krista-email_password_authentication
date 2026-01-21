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

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import com.krista.extensions.authentication.email_password_authentication.app.dto.KnownErrorMessage;
import com.krista.extensions.authentication.email_password_authentication.utils.KnownError;

public class KnownErrorExceptionMapper implements ExceptionMapper<KnownError> {

    @Override
    public Response toResponse(KnownError exception) {
        KnownErrorMessage message = new KnownErrorMessage(exception.getMessage(), getStackTrace(exception));
        return Response.status(exception.getStatus()).entity(message).build();
    }

    private String getStackTrace(Exception exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }

}
