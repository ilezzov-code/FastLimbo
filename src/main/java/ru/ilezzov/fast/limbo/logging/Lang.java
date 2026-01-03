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
public class Lang {
    // Status Messages
    public static final String STARTING_LIMBO_SERVER = "Starting Limbo server";
    public static final String FILE_CREATED = "File '%s' has been created successfully";
    public static final String FILE_LOADED = "File '%s' has been loaded successfully";
    public static final String FILE_UPDATED = "File '%s' has been updated and new parameters have been added";
    public static final String VERSION_LOADED = "VersionDate has been loaded";


    // Error Messages
    public static final String RESOURCE_NOT_FOUD_ERROR = "Internal resource '%s' not found in the source JAR file";
    public static final String REQUIRED_KEY_MISSING_ERROR = "Required config key '%s' is missing";
    public static final String STRUCTURE_ERROR = "Structure error: The root configuration element must be an object";
    public static final String SYNTAX_ERROR = "Syntax error in JSON/YAML: %s";
    public static final String ACCESS_DENIED_ERROR = "Denied access when writing a file: %s";
    public static final String IO_ERROR = "Critical I/O error when working with configuration";
    public static final String COMPARING_VERSION_ERROR = "Error when comparing config versions";
    public static final String URI_SYNTAX_ERROR = "Syntax error in URI: %s";
    public static final String INVALID_FORMAT_VERSION_ERROR = "Invalid config version format: a number was expected (e.g., 1.0.1), but a string was found";
    public static final String NO_NETWORK_CONNECT_ERROR = "No network access: Couldn't connect to the update server";
    public static final String CONNECT_REJECTED_ERROR = "Connection rejected: check the firewall or proxy settings";
    public static final String NOT_FOUND_ERROR = "The version file could not be retrieved. The server returned a 404 error.";
    public static final String CRITICAL_REQUEST_ERROR = "Critical request initialization error: %s";


}
