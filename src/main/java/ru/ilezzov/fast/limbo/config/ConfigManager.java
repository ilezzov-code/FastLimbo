package ru.ilezzov.fast.limbo.config;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ilezzov.fast.limbo.model.Response;
import ru.ilezzov.fast.limbo.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.MissingResourceException;

import static ru.ilezzov.fast.limbo.FastLimbo.*;
import static ru.ilezzov.fast.limbo.logging.LoggerTemplate.*;

public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final String FILE_NAME = getProperties().getConfigurationFile();
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private final Path configPath;
    private Config config;

    public ConfigManager(final Path dataFolder) {
        this.configPath = dataFolder.resolve(FILE_NAME);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);

        final Response<Integer> response = load();

        if (!response.success()) {
            fileErrorLoad(logger, FILE_NAME, response.error());
            stop();
        }

        final int status = response.data();

        if (status > 0) {
            fileCreated(logger, FILE_NAME);
        }

        if (status > 1) {
            fileUpdated(logger, FILE_NAME);
        }

        fileLoaded(logger, FILE_NAME);
    }

    private Response<Integer> load() {
        int status = 0;

        try (final InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (in == null) {
                final String error = "file not found";
                return Response.error(error, new FileNotFoundException(error));
            }

            final byte[] defaultConfigBytes = in.readAllBytes();

            if (Files.notExists(configPath)) {
                Files.write(configPath, defaultConfigBytes);
                status = 1;
            }

            final File currentFile = configPath.toFile();
            final JsonNode currentFileNode = mapper.readTree(currentFile);
            final JsonNode defaultFileNode = mapper.readTree(defaultConfigBytes);

            final String currentVersion = currentFileNode.path("config-version").asText();
            final String defaultVersion = defaultFileNode.path("config-version").asText();

            logger.info("Current Version {} Default version {} ", currentVersion, defaultVersion);

            if (currentVersion == null || currentVersion.isBlank() || defaultVersion == null || defaultVersion.isBlank()) {
                return Response.error("Required config key 'config-version' is missing", new MissingResourceException("Required config key 'config-version' is missing", FILE_NAME, "config-version"));
            }

            final Response<Integer> checkVersion = Utils.equalsVersion(currentVersion, defaultVersion);

            if (!checkVersion.success()) {
                return Response.error("Couldn't read the configuration version", checkVersion.error());
            }

            final int versionStatus = checkVersion.data();

            if (versionStatus == 1) {
                createDump(currentFile);

                final ObjectNode currentObjectNode = (ObjectNode) currentFileNode;
                addMissingKeys(currentObjectNode, defaultFileNode);

                mapper.writerWithDefaultPrettyPrinter().writeValue(currentFile, currentFileNode);
                status = 2;
            }

            this.config = mapper.readValue(currentFile, Config.class);
            return Response.ok(status);
        } catch (final IOException e) {
            return Response.error("An error occurred while trying to load the configuration", e);
        }
    }

    private void createDump(final File currentFile) throws IOException {
        final File file = new File(currentFile.getPath().concat(".old"));
        Files.copy(currentFile.toPath(), file.toPath());
    }

    private void addMissingKeys(final ObjectNode currentFileNode, final JsonNode defaultFileNode) {
        for (final Map.Entry<String, JsonNode> entry : defaultFileNode.properties())  {
            final String key = entry.getKey();
            final JsonNode defaultValue = entry.getValue();

            if (!currentFileNode.has(key)) {
                currentFileNode.set(key, defaultValue);
                continue;
            }

            final JsonNode userValue = currentFileNode.get(key);
            if (defaultValue.isObject() && userValue.isObject()) {
                addMissingKeys((ObjectNode) userValue, defaultValue);
            }
        }

        currentFileNode.set("config-version", defaultFileNode.get("config-version"));

    }

    public Config getConfig() {
        return this.config;
    }
}
