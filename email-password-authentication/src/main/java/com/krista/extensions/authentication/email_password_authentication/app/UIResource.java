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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("ui")
public class UIResource {

    @GET
    @Path("")
    public Response getIndex() throws IOException {
        return getUIAssets("");
    }

    @GET
    @Path("{subpath: .*}")
    public Response getUIAssets(@PathParam("subpath") String subPath) throws IOException {
        String cacheControl = "private, max-age=604800, must-revalidate";
        if (subPath.trim().isEmpty() || subPath.equals("index.html")) {
            subPath = "index.html";
            cacheControl = "no-cache";
        }
        java.nio.file.Path path = new File(subPath).toPath();
        String mimeType = Files.probeContentType(path);
        InputStream inputStream = getClass().getResourceAsStream("/ui/" + subPath);
        if (inputStream == null) {
            cacheControl = "no-cache";
            mimeType = "text/html";
            inputStream = getClass().getResourceAsStream("/ui/" + "index.html");
        }
        if (inputStream == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        // Read the entire stream and close it immediately
        byte[] content;
        try (InputStream is = inputStream) {
            content = is.readAllBytes();
        }
        return Response.ok(content).header("Content-type", mimeType)
                .header("Cache-Control", cacheControl).build();
    }

}
