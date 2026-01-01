package ru.ilezzov.fast.limbo.utils;

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

import ru.ilezzov.fast.limbo.model.Response;

public class Utils {
    public static Response<Integer> equalsVersion(final String first, final String second) {
        final String[] firstSplit = first.split("\\.");
        final String[] secondSplit = second.split("\\.");

        final int maxVersionLength = Math.max(firstSplit.length, secondSplit.length);

        for (int i = 0; i < maxVersionLength; i++) {
            try {
                final int num1 = i < firstSplit.length ? Integer.parseInt(firstSplit[i]) : 0;
                final int num2 = i < secondSplit.length ? Integer.parseInt(secondSplit[i]) : 0;

                if (num1 > num2) {
                    return Response.ok(-1);
                }

                if (num1 < num2) {
                    return Response.ok(1);
                }

            } catch (final NumberFormatException e) {
                return Response.error("Invalid config version format: a number was expected (e.g., 1.0.1), but a string was found", e);
            }
        }
        return Response.ok(0);
    }
}
