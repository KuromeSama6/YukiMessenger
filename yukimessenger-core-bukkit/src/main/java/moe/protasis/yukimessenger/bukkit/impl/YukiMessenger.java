package moe.protasis.yukimessenger.bukkit.impl;

import lombok.Getter;
import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukicommons.api.plugin.IAbstractPlugin;
import moe.protasis.yukicommons.api.scheduler.IAbstractScheduler;
import moe.protasis.yukicommons.bukkit.impl.scheduler.BukkitScheduler;
import moe.protasis.yukicommons.util.JsonUtil;
import moe.protasis.yukimessenger.api.YukiMessengerAPI;
import moe.protasis.yukimessenger.api.annotation.EndpointHandler;
import moe.protasis.yukimessenger.api.annotation.MessageHandler;
import moe.protasis.yukimessenger.api.IYukiMessengerAPI;
import moe.protasis.yukimessenger.api.message.InboundMessage;
import moe.protasis.yukimessenger.api.message.OutboundMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class YukiMessenger extends JavaPlugin implements EndpointHandler, IAbstractPlugin {
    @Getter
    private static YukiMessenger instance;
    private static Logger logger;
    public static JsonWrapper config;
    private IYukiMessengerAPI api;
    @Getter
    private BukkitMessenger client;
    private BukkitScheduler scheduler;

    @Override
    public void onEnable() {
        instance = this;
        logger = super.getLogger();
        api = new MessengerApiBukkit();
        scheduler = new BukkitScheduler(this);
        YukiMessengerAPI.SetCurrent(api);

        logger.info("Staring YukiMessenger");
        config = JsonUtil.UpdateAndWrite(
                new File(getDataFolder() + "/config.json"),
                getResource("config.json")
        );

        client = new BukkitMessenger();
        api.Subscribe(this);

    }

    @Override
    public void onDisable() {
        logger.warning("Closing websocket");
        client.Close();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equals("ymping")) return true;
        String content = args.length > 0 ? args[0] : "";
        sender.sendMessage("ping");
        api.SendAsync(OutboundMessage.ToProxy("yukimessenger.ping", new JsonWrapper()
                .Set("content", content)))
                .thenAccept(res -> sender.sendMessage(String.format("pong [processed=%s] %s", res.isProcessed(), res.getData().GetString("content"))));

        return true;
    }

    @MessageHandler("yukimessenger.ping")
    private void OnPing(InboundMessage msg) {
        msg.Respond(new JsonWrapper()
                .Set("content", msg.getData().GetString("content")));
    }

    @Override
    public IAbstractScheduler GetScheduler() {
        return scheduler;
    }

    @Override
    public Logger GetLogger() {
        return getInstance().getServer().getLogger();
    }

    @Override
    public ClassLoader GetClassLoader() {
        return getClassLoader();
    }
}
