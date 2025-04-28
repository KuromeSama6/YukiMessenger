package moe.protasis.yukimessenger.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukicommons.api.plugin.IAbstractPlugin;
import moe.protasis.yukicommons.api.scheduler.IAbstractScheduler;
import moe.protasis.yukicommons.util.JsonUtil;
import moe.protasis.yukicommons.velocity.impl.scheduler.VelocityScheduler;
import moe.protasis.yukimessenger.api.IYukiMessengerAPI;
import moe.protasis.yukimessenger.api.YukiMessengerAPI;
import moe.protasis.yukimessenger.api.annotation.EndpointHandler;
import moe.protasis.yukimessenger.api.annotation.MessageHandler;
import moe.protasis.yukimessenger.api.message.InboundMessage;
import moe.protasis.yukimessenger.velocity.impl.VelocityMessenger;
import moe.protasis.yukimessenger.velocity.impl.MessengerApiVelocity;
import moe.protasis.yukimessenger.velocity.command.PingCommand;
import moe.protasis.yukimessenger.server.Messenger;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(
        id = "yukimessenger",
        name = "YukiMessenger",
        version = "1.1.2",
        authors = "KuromeSama6",
        dependencies = {
                @Dependency(id = "yukicommons")
        }
)
public class YukiMessenger implements EndpointHandler, IAbstractPlugin {
    @Getter
    private static YukiMessenger instance;
    @Getter
    private static JsonWrapper config;
    private final Logger logger;
    @Getter
    private IYukiMessengerAPI api;
    @Getter
    private Messenger server;
    private VelocityScheduler scheduler;
    @Getter
    private final ProxyServer proxyServer;
    @Getter
    private final File dataFolder;

    @Inject
    public YukiMessenger(ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
        instance = this;
        proxyServer = server;
        this.logger = logger;
        this.dataFolder = dataFolder.toFile();
    }

    @Subscribe
    private void OnPluginInit(ProxyInitializeEvent e) {
        logger.info("Initializing YukiMessenger");
        api = new MessengerApiVelocity();
        YukiMessengerAPI.SetCurrent(api);
        scheduler = new VelocityScheduler(this);

        config = JsonUtil.UpdateAndWrite(
                new File(dataFolder + "/config.json"),
                getClass().getResourceAsStream("/config.json")
        );

        server = new VelocityMessenger(this, config.GetString("ip"), config.GetInt("port"));

        api.Subscribe(this);

        proxyServer.getCommandManager().register(
                proxyServer.getCommandManager().metaBuilder("ping").plugin(this).build(),
                new PingCommand()
        );
    }

    @Subscribe
    private void OnShutdown(ProxyShutdownEvent e) {
        logger.info("Stopping websocket server");
        server.Close();
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

    public Logger GetLogger() {
        return logger;
    }

    @Override
    public ClassLoader GetClassLoader() {
        return ClassLoader.getSystemClassLoader();
    }
}
