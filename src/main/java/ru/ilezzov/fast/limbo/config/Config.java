package ru.ilezzov.fast.limbo.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {
    public boolean checkUpdates;
    public String bind;
    public int maxPlayers;

    public PingSection ping;
    public Dimension dimension;

    public String worldName;

    public SpawnSection spawn;
    public Weather weather;
    public GameMode gamemode;

    public PlayerListSection playerList;
    public TabListSection tabList;
    public ServerNameSection serverName;
    public PlayerJoinSection playerJoin;
    public BossBarSection bossBar;
    public ActionBarSection actionBar;

    public InfoForwardingSection infoForwarding;
    public NettySection netty;

    public double configVersion;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PingSection {
        public String motd;
        public String version;
        public List<String> supportVersion;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SpawnSection {
        public double x;
        public double y;
        public double z;
        public float yaw;
        public float pitch;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerListSection {
        public boolean enable;
        public String username;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TabListSection {
        public boolean enable;
        public String header;
        public String footer;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ServerNameSection {
        public boolean enable;
        public String content;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerJoinSection {
        public ChatMessageSection chatMessage;
        public TitleSection title;
        public SoundSection sound;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public PlayerJoinSection() {
            this.chatMessage = new ChatMessageSection();
            this.title = new TitleSection();
            this.sound = new SoundSection();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ChatMessageSection {
            public boolean enable;
            public String text;

        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class TitleSection {
            public boolean enable;
            public int fadeIn;
            public int fadeOut;
            public int stay;

        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class SoundSection {
            public boolean enable;
            public String sound;

        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BossBarSection {
        public String text;
        public double progress;
        public BossBarColor color;
        public BossBarOverlay overlay;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ActionBarSection {
        public String text;
        public boolean lock;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InfoForwardingSection {
        public ForwardingType type;
        public String secret;
        public List<String> tokens;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NettySection {
        public ThreadsSection threads;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ThreadsSection {
            public int bossGroup;
            public int workerGroup;

        }
    }

    public enum Dimension {
        OVERWORLD, NETHER, THE_END
    }

    public enum Weather {
        CLEAR, RAIN, THUNDER
    }

    public enum GameMode {
        SURVIVAL, CREATIVE, SPECTATOR, ADVENTURE
    }

    public enum BossBarColor {
        PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE
    }

    public enum BossBarOverlay {
        PROGRESS, NOTCHED_6, NOTCHED_10, NOTCHED_12, NOTCHED_20
    }

    public enum ForwardingType {
        NONE, LEGACY, MODERN, BUNGEE_GUARD
    }

    public Config() {}
}