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

package com.krista.extensions.authentication.email_password_authentication.app.dto;

public final class AttemptLoginOut {

    private final Status status;

    private final Object info;

    private AttemptLoginOut(Status status, Object info) {
        this.status = status;
        this.info = info;
    }

    public static AttemptLoginOut success(AttemptLoginOut.SuccessResponse info) {
        return new AttemptLoginOut(Status.SUCCESS, info);
    }

    public static AttemptLoginOut failure(AttemptLoginOut.FailureResponse info) {
        return new AttemptLoginOut(Status.FAILURE, info);
    }

    public static AttemptLoginOut blocked() {
        return new AttemptLoginOut(Status.RECOVER_ACCOUNT, null);
    }

    public Status getStatus() {
        return status;
    }

    public Object getInfo() {
        return info;
    }

    public static class FailureResponse {

        private final int attemptsLeft;

        public FailureResponse(int attemptsLeft) {
            this.attemptsLeft = attemptsLeft;
        }

        public int getAttemptsLeft() {
            return attemptsLeft;
        }

    }

    public static class SuccessResponse {

        private final String clientSessionId;
        private final String loginCode;

        public SuccessResponse(String clientSessionId, String loginCode) {
            this.clientSessionId = clientSessionId;
            this.loginCode = loginCode;
        }

        public String getClientSessionId() {
            return clientSessionId;
        }

        public String getLoginCode() {
            return loginCode;
        }

    }

    public enum Status {
        RECOVER_ACCOUNT,
        SUCCESS,
        FAILURE

    }

}
