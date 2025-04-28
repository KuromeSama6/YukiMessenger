package moe.protasis.yukimessenger.api.message;

import moe.protasis.yukicommons.util.EnvironmentType;
import moe.protasis.yukicommons.util.Util;

public enum MessageDestination {
    SERVER,
    CLIENT,
    BOTH;

    public boolean Matches() {
        EnvironmentType env = Util.GetEnvironment();
        return this == BOTH
                || env == EnvironmentType.BUNGEECORD && this == SERVER
                || env == EnvironmentType.VELOCITY && this == SERVER
                || env == EnvironmentType.BUKKIT && this == CLIENT;
    }
}
