package moe.protasis.yukimessenger.bungee.command;

import moe.protasis.yukicommons.json.JsonWrapper;
import moe.protasis.yukimessenger.api.IYukiMessengerApi;
import moe.protasis.yukimessenger.bungee.YukiMessenger;
import moe.protasis.yukimessenger.bungee.service.SpigotServer;
import moe.protasis.yukimessenger.message.OutboundMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class PingCommand extends Command {
    public PingCommand() {
        super("ymping");
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String msg = String.join(" ", strings);
        IYukiMessengerApi api = IYukiMessengerApi.Get();
        SpigotServer server = api.GetServer(strings[0]);
        sender.sendMessage(new TextComponent("ping"));
        api.SendAsync(new OutboundMessage(server, "yukimessenger.ping", new JsonWrapper()
                .Set("content", msg)), res -> {

            sender.sendMessage(new TextComponent(String.format("pong [processed=%s] %s", res.isProcessed(), res.getData().GetString("content"))));
        });

    }
}
