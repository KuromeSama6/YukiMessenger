package moe.protasis.yukimessenger.spigot;

import lombok.Getter;
import moe.icegame.coreutils.GameUtil;
import moe.protasis.yukimessenger.YukiMessengerAPI;
import moe.protasis.yukimessenger.spigot.service.ServerboundMessage;
import moe.protasis.yukimessenger.util.JsonObjectBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class YukiMessenger extends JavaPlugin {
    @Getter
    private static YukiMessenger instance;
    private static Logger logger;
    public static YamlConfiguration config;

    private MessengerClient client;

    @Override
    public void onEnable() {
        instance = this;
        logger = super.getLogger();

        logger.info("Staring YukiMessenger");
        config = GameUtil.UpdateConfigWithDefaults(getDataFolder(), this, "/spigot/config.yml", "/config.yml");

        client = new MessengerClient();


    }

    @Override
    public void onDisable() {
        logger.warning("Closing websocket");
        MessengerClient.getInstance().Close();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equals("ymping")) return true;
        String content = args.length > 0 ? args[0] : "";
        sender.sendMessage("ping");
        GetApi().SendAsync(new ServerboundMessage("yukimessenger.ping", new JsonObjectBuilder()
                .put("content", content)
                .finish()), res -> {

            sender.sendMessage(String.format("pong [processed=%s] %s", res.processed, res.data.get("content").getAsString()));
        });

        return true;
    }

    public YukiMessengerAPI GetApi() {
        return new YukiMessengerAPI(false);
    }

    public static Logger GetLogger() {
        return getInstance().getServer().getLogger();
    }
}
