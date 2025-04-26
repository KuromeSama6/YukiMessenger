package moe.protasis.yukimessenger.spigot;

import lombok.Getter;
import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukicommons.util.JsonUtil;
import moe.protasis.yukimessenger.annotation.EndpointHandler;
import moe.protasis.yukimessenger.annotation.MessageHandler;
import moe.protasis.yukimessenger.api.IYukiMessengerApi;
import moe.protasis.yukimessenger.api.YukiMessengerAPI;
import moe.protasis.yukimessenger.message.InboundMessage;
import moe.protasis.yukimessenger.message.OutboundMessage;
import moe.protasis.yukimessenger.message.ServerBoundMessage;
import moe.protasis.yukimessenger.util.JsonObjectBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class YukiMessenger extends JavaPlugin implements EndpointHandler {
    @Getter
    private static YukiMessenger instance;
    private static Logger logger;
    public static JsonWrapper config;
    private IYukiMessengerApi api;
    private MessengerClient client;

    @Override
    public void onEnable() {
        instance = this;
        logger = super.getLogger();

        logger.info("Staring YukiMessenger");
        config = JsonUtil.UpdateAndWrite(
                new File(getDataFolder() + "/config.json"),
                getResource("spigot/config.json")
        );

        client = new MessengerClient();
        api = IYukiMessengerApi.Get();
        api.Subscribe(this);
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
        api.SendAsync(OutboundMessage.ToProxy("yukimessenger.ping", new JsonWrapper()
                .Set("content", content)), res -> {

            sender.sendMessage(String.format("pong [processed=%s] %s", res.isProcessed(), res.getData().GetString("content")));
        });

        return true;
    }

    @MessageHandler("yukimessenger.ping")
    private void OnPing(InboundMessage msg) {
        msg.Respond(new JsonWrapper()
                .Set("content", msg.getData().GetString("content")));
    }

    @Deprecated
    public YukiMessengerAPI GetApi() {
        return new YukiMessengerAPI(false);
    }

    public static Logger GetLogger() {
        return getInstance().getServer().getLogger();
    }
}
