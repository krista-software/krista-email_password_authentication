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

import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.jvnet.hk2.annotations.ContractsProvided;
import org.jvnet.hk2.annotations.Service;

@Service
@ApplicationPath("/")
@ContractsProvided(Application.class)
public class EmailPasswordAuthenticationApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(ApiResource.class, UIResource.class);
    }

    @Override
    public Set<Object> getSingletons() {
        return Set.of(new KnownErrorExceptionMapper());
    }

}
