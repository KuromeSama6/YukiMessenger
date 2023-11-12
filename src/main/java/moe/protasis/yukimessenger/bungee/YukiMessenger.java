package moe.protasis.yukimessenger.bungee;

import lombok.Getter;
import moe.icegame.coreutils.GameUtil;
import moe.protasis.yukimessenger.YukiMessengerAPI;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;
import moe.protasis.yukimessenger.util.JsonObjectBuilder;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class YukiMessenger extends Plugin {
    @Getter private static YukiMessenger instance;
    @Getter private static Configuration config;
    private static Logger logger;

    private MessengerServer server;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        logger.info("Staring YukiMessenger (Bungee)");

        GameUtil.UpdateCofig(getDataFolder(), this, "/bungee/config.yml", "/config.yml");
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server = new MessengerServer();

        GetApi().SubscribeServerbound("yukimessenger.ping", res -> {
            res.Squawk(new JsonObjectBuilder()
                    .put("content", res.data.get("content").getAsString())
                    .finish());
        });
    }

    @Override
    public void onDisable() {
        for (SpigotServer server : MessengerServer.getInstance().getClients()) {
            server.conn.close(1012, "proxy restarting");
        }

        MessengerServer.getInstance().Close();
    }

    public YukiMessengerAPI GetApi() {
        return new YukiMessengerAPI(true);
    }

    public static Logger GetLogger() {
        return getInstance().getLogger();
    }
}
