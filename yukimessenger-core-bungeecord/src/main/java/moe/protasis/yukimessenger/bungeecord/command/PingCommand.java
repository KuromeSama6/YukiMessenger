package moe.protasis.yukimessenger.bungeecord.command;

import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukimessenger.api.IYukiMessengerAPI;
import moe.protasis.yukimessenger.bungeecord.impl.SpigotDownstream;
import moe.protasis.yukimessenger.api.message.OutboundMessage;
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
        IYukiMessengerAPI api = IYukiMessengerAPI.Get();
        SpigotDownstream server = api.GetServer(strings[0]);
        sender.sendMessage(new TextComponent("ping"));
        api.SendAsync(OutboundMessage.ToClient(server, "yukimessenger.ping", new JsonWrapper()
                .Set("content", msg)), res -> {

            sender.sendMessage(new TextComponent(String.format("pong [processed=%s] %s", res.isProcessed(), res.getData().GetString("content"))));
        });

    }
}
