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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.ReadOnlyFileSystemException;
import java.util.Map;

import static ru.ilezzov.fast.limbo.FastLimbo.*;
import static ru.ilezzov.fast.limbo.logging.Lang.*;
public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final String FILE_NAME = getProperties().getConfigurationFile();
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private final Path configPath;
    private Config config;

    public ConfigManager(final Path dataFolder) {
        this.configPath = dataFolder.resolve(FILE_NAME);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);

        final Response<ConfigStatus> response = load();

        if (!response.success()) {
            final Exception e = response.error();

            if (e == null) {
                logger.error(response.message());
            } else {
                logger.error(response.message(), response.error());
            }
            stop();
        }

        final ConfigStatus status = response.data();

        if (status == ConfigStatus.CREATED) {
            logger.info(FILE_CREATED.formatted(FILE_NAME));
        }

        if (status == ConfigStatus.UPDATED) {
            logger.info(FILE_UPDATED.formatted(FILE_NAME));
        }

        logger.info(FILE_LOADED.formatted(FILE_NAME));
    }

    private Response<ConfigStatus> load() {
        ConfigStatus status = ConfigStatus.LOADED;

        try (final InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (in == null) {
                return Response.error(RESOURCE_NOT_FOUD_ERROR.formatted(FILE_NAME));
            }

            final byte[] defaultConfigBytes = in.readAllBytes();

            if (Files.notExists(configPath)) {
                Files.write(configPath, defaultConfigBytes);
                status = ConfigStatus.CREATED;
            }

            final File currentFile = configPath.toFile();

            final JsonNode currentFileNode = mapper.readTree(currentFile);
            final JsonNode defaultFileNode = mapper.readTree(defaultConfigBytes);

            final String currentVersion = currentFileNode.path("config-version").asText();
            final String defaultVersion = defaultFileNode.path("config-version").asText();

            if (currentVersion == null || currentVersion.isBlank() || defaultVersion == null || defaultVersion.isBlank()) {
                return Response.error(REQUIRED_KEY_MISSING_ERROR.formatted("config-version"));
            }

            final Response<Integer> checkVersion = Utils.equalsVersion(currentVersion, defaultVersion);

            if (!checkVersion.success()) {
                return Response.error(COMPARING_VERSION_ERROR, checkVersion.error());
            }

            final int versionStatus = checkVersion.data();
            if (versionStatus == 1) {
                createDump(currentFile);

                if (currentFileNode.isObject()) {
                    final ObjectNode currentObjectNode = (ObjectNode) currentFileNode;
                    addMissingKeys(currentObjectNode, defaultFileNode);

                    mapper.writerWithDefaultPrettyPrinter().writeValue(currentFile, currentFileNode);
                    status = ConfigStatus.UPDATED;
                } else {
                    return Response.error(STRUCTURE_ERROR);
                }
            }

            this.config = mapper.readValue(currentFile, Config.class);
            return Response.ok(status);
        } catch (JsonParseException e) {
            return Response.error(SYNTAX_ERROR.formatted(e.getOriginalMessage()), e);
        } catch (JsonMappingException e) {
            return Response.error(STRUCTURE_ERROR, e);
        } catch (AccessDeniedException | ReadOnlyFileSystemException e) {
            return Response.error(ACCESS_DENIED_ERROR.formatted(configPath.toString()), e);
        } catch (IOException e) {
            return Response.error(IO_ERROR, e);
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

    private enum ConfigStatus {
        CREATED, UPDATED, LOADED
    }
}
