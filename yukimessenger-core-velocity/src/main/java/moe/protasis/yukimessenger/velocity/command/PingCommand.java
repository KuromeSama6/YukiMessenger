package moe.protasis.yukimessenger.velocity.command;

import com.velocitypowered.api.command.RawCommand;
import moe.protasis.yukicommons.api.json.JsonWrapper;
import moe.protasis.yukimessenger.api.IYukiMessengerAPI;
import moe.protasis.yukimessenger.api.message.OutboundMessage;
import moe.protasis.yukimessenger.velocity.impl.VelocityDownstream;
import net.kyori.adventure.text.Component;

public class PingCommand implements RawCommand {

    @Override
    public void execute(Invocation invocation) {
        var strings = invocation.arguments().split(" ");
        var sender = invocation.source();

        String msg = String.join(" ", strings);
        IYukiMessengerAPI api = IYukiMessengerAPI.Get();
        VelocityDownstream server = api.GetServer(strings[0]);
        sender.sendMessage(Component.text("ping"));
        api.SendAsync(OutboundMessage.ToClient(server, "yukimessenger.ping", new JsonWrapper()
                .Set("content", msg)))
                .thenAccept(res -> sender.sendMessage(Component.text(String.format("pong [processed=%s] %s", res.isProcessed(), res.getData().GetString("content")))));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("yukimessenger.ping");
    }
}
