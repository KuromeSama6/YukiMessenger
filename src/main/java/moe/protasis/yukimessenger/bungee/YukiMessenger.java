package moe.protasis.yukimessenger.bungee;

import lombok.Getter;
import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukicommons.util.JsonUtil;
import moe.protasis.yukimessenger.api.IYukiMessengerApi;
import moe.protasis.yukimessenger.api.YukiMessengerAPI;
import moe.protasis.yukimessenger.annotation.EndpointHandler;
import moe.protasis.yukimessenger.annotation.MessageHandler;
import moe.protasis.yukimessenger.bungee.command.PingCommand;
import moe.protasis.yukimessenger.message.InboundMessage;
import moe.protasis.yukimessenger.message.ServerInboundMessage;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;
import moe.protasis.yukimessenger.util.JsonObjectBuilder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.logging.Logger;

public class YukiMessenger extends Plugin implements EndpointHandler {
    @Getter
    private static YukiMessenger instance;
    @Getter
    private static JsonWrapper config;
    private static Logger logger;
    @Getter
    private IYukiMessengerApi api;
    private MessengerServer server;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        logger.info("Staring YukiMessenger (Bungee)");

        config = JsonUtil.UpdateAndWrite(
                new File(getDataFolder() + "/config.json"),
                getResourceAsStream("bungee/config.json")
        );

        server = new MessengerServer();
        api = IYukiMessengerApi.Get();

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
        for (SpigotServer server : MessengerServer.getInstance().getClients()) {
            server.conn.close(1012, "proxy restarting");
        }

        logger.info("Stopping websocket server");
        MessengerServer.getInstance().Close();
    }

    @Deprecated
    public YukiMessengerAPI GetApi() {
        return new YukiMessengerAPI(true);
    }

    public static Logger GetLogger() {
        return getInstance().getLogger();
    }
}
