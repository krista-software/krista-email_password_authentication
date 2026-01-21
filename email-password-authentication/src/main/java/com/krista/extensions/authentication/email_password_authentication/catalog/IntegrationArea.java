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

package com.krista.extensions.authentication.email_password_authentication.catalog;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import app.krista.extension.executor.Invoker;
import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;
import app.krista.extension.request.RoutingInfo;
import app.krista.extension.request.protos.http.HttpProtocol;
import com.krista.extensions.authentication.email_password_authentication.utils.Routes;

@Domain(id = "catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7",
        name = "Authentication",
        ecosystemId = "catEntryEcosystem_d3b05047-07b0-4b06-95a3-9fb8f7f608d9",
        ecosystemName = "Krista",
        ecosystemVersion = "a7cab895-1207-4ad9-a630-e92a4ac574a4")
public class IntegrationArea {

    private final Invoker invoker;

    @Inject
    public IntegrationArea(Invoker invoker) {
        this.invoker = invoker;
    }

    @CatalogRequest(
            id = "localDomainRequest_4707bb63-26f2-4b1e-8ca1-2a4e9fb99d46",
            name = "Get Authentication Actions",
            description = "Get Authentication Actions",
            area = "Integration",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Desc(name = "actions", type = "[ { name: Text, actionUrl: Text } ]", required = false)
    public List<Map<String, Object>> getAuthenticationActions(
            @Field.Text(name = "email", attributes = {
                    @Attribute(name = "visualWidth", value = "S")}) String email,
            @Field.Text(name = "redirectUrl", attributes = {
                    @Attribute(name = "visualWidth", value = "S")}) String redirectUrl,
            @Field.Text(name = "state", attributes = {
                    @Attribute(name = "visualWidth", value = "S")}) String state) {

        String routingURL = invoker.getRoutingInfo().getRoutingURL(HttpProtocol.PROTOCOL_NAME,
                isStudioUrl(redirectUrl) ? RoutingInfo.Type.STUDIO : RoutingInfo.Type.CLIENT);

        // Change password
        Map<String, Object> changePasswordAction = new HashMap<>();
        changePasswordAction.put("name", "changePassword");
        UriBuilder changePasswordUriBuilder = UriBuilder.fromUri(routingURL)
                .path("rest")
                .path("ui")
                .path(Routes.changePassword)
                .queryParam("email", URLEncoder.encode(email, StandardCharsets.UTF_8))
                .queryParam("redirectUrl", URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8));
        if (state != null) {
            changePasswordUriBuilder.queryParam("state", URLEncoder.encode(state, StandardCharsets.UTF_8));
        }
        String changePasswordUrl = changePasswordUriBuilder.build().toString();
        changePasswordAction.put("actionUrl", changePasswordUrl);

        // Login
        Map<String, Object> loginAction = new HashMap<>();
        loginAction.put("name", "login");
        UriBuilder loginUriBuilder = UriBuilder.fromUri(routingURL)
                .path("rest")
                .path("ui")
                .path(Routes.password)
                .queryParam("email", URLEncoder.encode(email, StandardCharsets.UTF_8))
                .queryParam("redirectUrl", URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8));
        if (state != null) {
            loginUriBuilder.queryParam("state", URLEncoder.encode(state, StandardCharsets.UTF_8));
        }
        String loginUri = loginUriBuilder.build().toString();
        loginAction.put("actionUrl", loginUri);

        List<Map<String, Object>> actions = new ArrayList<>();
        actions.add(changePasswordAction);
        actions.add(loginAction);
        return actions;
    }

    @CatalogRequest(
            id = "localDomainRequest_bc84de51-793c-40c2-913a-3b76c15b2e33",
            name = "Get Login URL",
            description = "provides URL to start login sequence.",
            area = "Integration",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Text(name = "loginUrl", required = false, attributes = {
            @Attribute(name = "visualWidth", value = "S")})
    public String getLoginURL(
            @Field.Text(name = "email", attributes = {
                    @Attribute(name = "visualWidth", value = "S")}) String email,
            @Field.Text(name = "redirectUrl", attributes = {
                    @Attribute(name = "visualWidth", value = "S")}) String redirectUrl) {
        return (String) getAuthenticationActions(email, redirectUrl, "")
                .stream()
                .filter(e -> "login".equals(e.get("name")))
                .findFirst()
                .map(e -> e.get("actionUrl"))
                .orElse(null);
    }

    private boolean isStudioUrl(String url) {
        return url.startsWith("https://studio");
    }

}