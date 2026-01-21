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

package com.krista.extensions.authentication.email_password_authentication.service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import app.krista.extension.executor.Invoker;
import app.krista.extension.request.RoutingInfo;
import app.krista.extension.request.protos.http.HttpProtocol;
import app.krista.extensions.util.KeyValueStore;
import com.google.gson.Gson;
import com.krista.extensions.authentication.email_password_authentication.utils.CodeInfo;
import com.krista.extensions.authentication.email_password_authentication.utils.ResetReason;
import com.krista.extensions.authentication.email_password_authentication.utils.Routes;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CodeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeService.class);

    private final KeyValueStore keyValueStore;
    private final Gson gson;
    private final Invoker invoker;

    @Inject
    public CodeService(KeyValueStore keyValueStore, Gson gson, Invoker invoker) {
        this.keyValueStore = keyValueStore;
        this.gson = gson;
        this.invoker = invoker;
    }

    public String generateNewLink(String email, String serverAddress, ResetReason resetReason) {
        String code = generateNewCode(email, serverAddress, resetReason);
        String routingURL = invoker.getRoutingInfo().getRoutingURL(HttpProtocol.PROTOCOL_NAME,
                isStudioUrl(serverAddress) ? RoutingInfo.Type.STUDIO : RoutingInfo.Type.CLIENT);
        return String.format("%s/rest/ui/%s?%s=%s", routingURL, Routes.resetPassword, "code", code);
    }

    private boolean isStudioUrl(String url) {
        return url.contains("studio");
    }

    private String generateNewCode(String emailId, String redirectUrl, ResetReason resetReason) {
        Objects.requireNonNull(emailId);
        Objects.requireNonNull(redirectUrl);
        String oldCode = getExistingCodeOfEmail(emailId);
        if (oldCode != null) {
            deleteCode(oldCode);
        }
        String newCode = createNonce();
        CodeInfo confirmationCodeDto =
                new CodeInfo(newCode, System.currentTimeMillis(), emailId, redirectUrl, resetReason);
        keyValueStore.put(confirmationCodeDto.getCodeKey(), gson.toJson(confirmationCodeDto));
        keyValueStore.put(confirmationCodeDto.getEmailKey(), newCode);
        return newCode;
    }

    public CodeInfo getCodeInfo(String code) {
        CodeInfo codeInfo = getCodeInfoWithOutValidation(code);
        if (codeInfo == null) {
            return null;
        }
        if (System.currentTimeMillis() - codeInfo.getCreationTime() > TimeUnit.MINUTES.toMillis(30)) {
            LOGGER.info("Code expired: {}. Removing it from the database", code);
            deleteCode(code);
            return null;
        }
        return codeInfo;
    }

    public void deleteCode(String code) {
        CodeInfo codeInfo = getCodeInfoWithOutValidation(code);
        if (codeInfo == null) {
            return;
        }
        keyValueStore.remove(codeInfo.getCodeKey());
        keyValueStore.remove(codeInfo.getEmailKey());
    }

    private CodeInfo getCodeInfoWithOutValidation(String code) {
        Objects.requireNonNull(code);
        String codeKey = CodeInfo.getCodeKey(code);
        String codeInfoString = (String) keyValueStore.get(codeKey);
        if (codeInfoString == null) {
            LOGGER.warn("code not found: {}", code);
            return null;
        }
        return gson.fromJson(codeInfoString, CodeInfo.class);
    }

    private String getExistingCodeOfEmail(String emailId) {
        String s = CodeInfo.getEmailKey(emailId);
        return (String) keyValueStore.get(s);
    }

    public static String createNonce() {
        String nonce = "";

        try {
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            String randomNum = String.valueOf(prng.nextLong());

            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] result = sha.digest(randomNum.getBytes());
            nonce = hexEncode(result);
        } catch (Exception ignored) {
        }
        return UUID.randomUUID() + "-" + nonce;
    }

    public static String hexEncode(byte[] aInput) {
        StringBuilder result = new StringBuilder();

        char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};

        for (byte b : aInput) {
            result.append(digits[(b & 0xf0) >> 4]);
            result.append(digits[b & 0x0f]);
        }

        return result.toString();
    }

}
