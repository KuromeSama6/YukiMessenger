package moe.protasis.yukimessenger.message;

import moe.protasis.yukicommons.util.EnvironmentType;
import moe.protasis.yukicommons.util.Util;

public enum MessageDestination {
    SERVER,
    CLIENT,
    BOTH;

    public boolean Matches() {
        EnvironmentType env = Util.GetEnvironment();
        return this == BOTH || env == EnvironmentType.PROXY && this == SERVER || env == EnvironmentType.SPIGOT && this == CLIENT;
    }
}
