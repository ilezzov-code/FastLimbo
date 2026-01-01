package ru.ilezzov.fast.limbo.properties;

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
import org.slf4j.LoggerFactory;
import ru.ilezzov.fast.limbo.FastLimbo;
import ru.ilezzov.fast.limbo.model.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static ru.ilezzov.fast.limbo.logging.LoggerTemplate.propertiesErrorLoad;
import static ru.ilezzov.fast.limbo.logging.LoggerTemplate.propertiesLoad;

public class MyProperties {
    private final Logger logger = LoggerFactory.getLogger(MyProperties.class);
    private final Properties properties = new Properties();

    private String currentVersion;
    private String versionFileUrl;
    private String configurationFile;

    public MyProperties() {
        final Response<Void> loadResponse = load();

        if (!loadResponse.success()) {
            propertiesErrorLoad(logger, loadResponse.error());
            FastLimbo.stop();
        }

        propertiesLoad(logger);
        loadValues();
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public String getVersionFileUrl() {
        return versionFileUrl;
    }

    private Response<Void> load() {
        try (final InputStream in = MyProperties.class.getClassLoader().getResourceAsStream(".properties")) {
            properties.load(in);

            return Response.ok();
        } catch (final IOException e) {
            return Response.error("Failed to load .properties", e);
        }
    }

    private void loadValues() {
        this.currentVersion = properties.getProperty("current-version");
        this.versionFileUrl = properties.getProperty("version-file-url");
        this.configurationFile = properties.getProperty("configuration-file");
    }

    public String getConfigurationFile() {
        return configurationFile;
    }
}
