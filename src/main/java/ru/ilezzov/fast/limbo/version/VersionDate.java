package ru.ilezzov.fast.limbo.version;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionDate {
    public LatestSection latest;
    public CompatibilitySection compatibility;
    public List<ChangeSection> history;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class LatestSection {
        public String version;
        public String releaseDate;
        public String downloadUrl;
        public List<String> changes;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class CompatibilitySection {
        public String minRequiredVersion;
        public List<String> blacklistedVersions;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ChangeSection {
        public String version;
        public List<String> changes;
    }
}
