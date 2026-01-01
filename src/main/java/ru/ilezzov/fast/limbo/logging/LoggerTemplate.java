package ru.ilezzov.fast.limbo.logging;

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

import org.slf4j.Logger;

public class LoggerTemplate {
    public static void fileCreated(final Logger logger, final String file) {
        logger.info("File '{}' has been created successfully", file);
    }

    public static void fileLoaded(final Logger logger, final String file) {
        logger.info("File '{}' has been loaded successfully", file);
    }

    public static void fileErrorLoad(final Logger logger, final String file, final Exception e) {
        logger.error("An error occurred when loading a file '{}'", file, e);
    }

    public static void fileUpdated(final Logger logger, final String file) {
        logger.info("File '{}' has been updated and new parameters have been added", file);
    }
}
