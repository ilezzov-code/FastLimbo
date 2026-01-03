package ru.ilezzov.fast.limbo.version;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ilezzov.fast.limbo.model.Response;
import ru.ilezzov.fast.limbo.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static ru.ilezzov.fast.limbo.FastLimbo.*;
import static ru.ilezzov.fast.limbo.logging.Lang.*;

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

public class VersionManager {
    private static final Logger logger = LoggerFactory.getLogger(VersionManager.class);
    private static final String VERSION_FILE_URL = getProperties().getVersionFileUrl();
    private static final String CURRENT_VERSION = getProperties().getCurrentVersion();

    private static final ObjectMapper mapper = new JsonMapper();

    private VersionDate versionDate;

    public VersionManager() {
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        final Response<Void> loadResponse = load();

        if (!loadResponse.success()) {
            final Exception e = loadResponse.error();

            if (e == null) {
                logger.error(loadResponse.message());
            } else {
                logger.error(loadResponse.message(), loadResponse.error());
            }
            return;
        }

        logger.info(VERSION_LOADED);
    }

    public Response<VersionType> check() {
        if (versionDate == null) {
            return Response.ok(VersionType.UNREACHABLE);
        }

        final String latestVersion = versionDate.latest.version;
        final String minRequiredVersion = versionDate.compatibility.minRequiredVersion;
        final List<String> blacklistVersions = versionDate.compatibility.blacklistedVersions;

        if (blacklistVersions.contains(CURRENT_VERSION)) {
            return Response.ok(VersionType.BLACKLIST);
        }

        final Response<Integer> equalsMinRequiredAndCurrent = Utils.equalsVersion(minRequiredVersion, CURRENT_VERSION);

        if (!equalsMinRequiredAndCurrent.success()) {
            return Response.error("Failed to equals versions", equalsMinRequiredAndCurrent.error());
        }

        int equalsStatus = equalsMinRequiredAndCurrent.data();

        if (equalsStatus == -1) {
            return Response.ok(VersionType.OUTDATED);
        }

        final Response<Integer> equalsLatestAndCurrent = Utils.equalsVersion(latestVersion, CURRENT_VERSION);

        if (!equalsLatestAndCurrent.success()) {
            return Response.error("Failed to equals versions", equalsLatestAndCurrent.error());
        }

        equalsStatus = equalsLatestAndCurrent.data();

        if (equalsStatus == -1) {
            return Response.ok(VersionType.SUPPORTED);
        }

        return Response.ok(VersionType.LATEST);
    }

    public VersionDate getVersionDate() {
        return versionDate;
    }

    private Response<Void> load() {
        try {
            final URI uri = URI.create(VERSION_FILE_URL);
            final URL url = uri.toURL();

            try (final InputStream in = url.openStream()) {
                byte[] bytes = in.readAllBytes();
                String content = new String(bytes, StandardCharsets.UTF_8);

                if (!content.trim().startsWith("{")) {
                    return Response.error(SYNTAX_ERROR.formatted("github returned non-json"));
                }

                this.versionDate = mapper.readValue(content, VersionDate.class);
                return Response.ok();
            }
        } catch (final UnknownHostException e) {
            return Response.error(NO_NETWORK_CONNECT_ERROR, e);
        } catch (final ConnectException e) {
            return Response.error(CONNECT_REJECTED_ERROR, e);
        } catch (JsonMappingException | JsonParseException e) {
            return Response.error(STRUCTURE_ERROR, e);
        } catch (IOException e) {
            if (e.getMessage().contains("404")) {
                return Response.error(NOT_FOUND_ERROR, e);
            }
            return Response.error(IO_ERROR, e);
        } catch (Exception e) {
            return Response.error(CRITICAL_REQUEST_ERROR.formatted(e.getMessage()), e);
        }
    }
}
