package ru.ilezzov.fast.limbo.model;

/*
 * Copyright (C) 2024-2026 ILeZzoV
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

public record Response<T>(boolean success, String message, T data, Exception error) {
    public static <T> Response<T> ok() {
        return new Response<>(true, "OK", null, null);
    }

    public static <T> Response<T> ok(final T data) {
        return new Response<>(true, "OK", data, null);
    }

    public static <T> Response<T> ok(final String message, final T data) {
        return new Response<>(true, message, data, null);
    }

    public static <T> Response<T> ok(final String message) {
        return new Response<>(true, message, null, null);
    }

    public static <T> Response<T> error(final String message) {
        return new Response<>(false, message, null, null);
    }

    public static <T> Response<T> error(final String message, final T data) {
        return new Response<>(false, message, data, null);
    }

    public static <T> Response<T> error(final String message, final Exception e) {
        return new Response<>(false, message, null, e);
    }

    public boolean hasData() {
        return data != null;
    }
}
