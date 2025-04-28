package moe.protasis.yukimessenger.bungeecord;

import lombok.Getter;
import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukicommons.api.plugin.IAbstractPlugin;
import moe.protasis.yukicommons.api.scheduler.IAbstractScheduler;
import moe.protasis.yukicommons.bungeecord.impl.scheduler.BungeecordScheduler;
import moe.protasis.yukicommons.util.JsonUtil;
import moe.protasis.yukimessenger.api.IYukiMessengerAPI;
import moe.protasis.yukimessenger.api.YukiMessengerAPI;
import moe.protasis.yukimessenger.api.annotation.EndpointHandler;
import moe.protasis.yukimessenger.api.annotation.MessageHandler;
import moe.protasis.yukimessenger.bungeecord.impl.BungeecordMessenger;
import moe.protasis.yukimessenger.bungeecord.impl.MessengerApiBungeecord;
import moe.protasis.yukimessenger.server.Messenger;
import moe.protasis.yukimessenger.bungeecord.command.PingCommand;
import moe.protasis.yukimessenger.api.message.InboundMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.logging.Logger;

public class YukiMessenger extends Plugin implements EndpointHandler, IAbstractPlugin {
    @Getter
    private static YukiMessenger instance;
    @Getter
    private static JsonWrapper config;
    private static Logger logger;
    @Getter
    private IYukiMessengerAPI api;
    @Getter
    private Messenger server;
    private BungeecordScheduler scheduler;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        scheduler = new BungeecordScheduler(this);
        api = new MessengerApiBungeecord();
        YukiMessengerAPI.SetCurrent(api);

        logger.info("Staring YukiMessenger (Bungee)");

        config = JsonUtil.UpdateAndWrite(
                new File(getDataFolder() + "/config.json"),
                getResourceAsStream("config.json")
        );

        server = new BungeecordMessenger(this, config.GetString("ip"), config.GetInt("port"));

        api.Subscribe(this);

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PingCommand());
    }

    @MessageHandler("yukimessenger.ping")
    private void OnPing(InboundMessage msg) {
        msg.Respond(new JsonWrapper()
                .Set("content", msg.getData().GetString("content")));
    }


    @Override
    public void onDisable() {
        logger.info("Stopping websocket server");
        server.Close();
    }

    @Override
    public IAbstractScheduler GetScheduler() {
        return scheduler;
    }

    public Logger GetLogger() {
        return logger;
    }

    @Override
    public ClassLoader GetClassLoader() {
        return ClassLoader.getSystemClassLoader();
    }
}
