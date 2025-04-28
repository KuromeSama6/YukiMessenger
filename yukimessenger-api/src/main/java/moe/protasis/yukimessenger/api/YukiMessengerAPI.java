package moe.protasis.yukimessenger.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class YukiMessengerAPI {
    private static IYukiMessengerAPI current;

    public static void SetCurrent(IYukiMessengerAPI api) {
        if (current != null) {
            throw new IllegalStateException("YukiMessengerAPI is already set to " + current);
        } else {
            current = api;
        }
    }

    public static IYukiMessengerAPI Get() {
        if (current == null) {
            throw new IllegalStateException("YukiMessengerAPI is not set");
        } else {
            return current;
        }
    }
}
