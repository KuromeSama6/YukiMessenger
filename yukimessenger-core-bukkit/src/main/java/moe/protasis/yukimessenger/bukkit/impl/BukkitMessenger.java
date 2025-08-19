package moe.protasis.yukimessenger.bukkit.impl;

import moe.protasis.yukimessenger.client.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class BukkitMessenger extends Messenger {
    public BukkitMessenger() {
        super(YukiMessenger.getInstance(), YukiMessenger.config.GetLong("reconnectInterval", 5000));
    }

    @Override
    protected Logger GetLogger() {
        return YukiMessenger.getInstance().GetLogger();
    }

    @Override
    protected void OnDisconnect() {
        // kick all players
        if (YukiMessenger.config.GetBool("kickOnDisconnect", true)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Bukkit.getScheduler().runTask(YukiMessenger.getInstance(), () -> {
                    player.kickPlayer("messenger connection dropped");
                });
            }
        }
    }

    @Override
    public String GetIdent() {
        String ret = YukiMessenger.config.GetString("ident");
        if (ret == null || ret.isEmpty()) {
            // use the folder name
            ret = YukiMessenger.getInstance()
                    .getDataFolder()
                    .getAbsoluteFile()
                    .getParentFile()
                    .getParentFile()
                    .getName();
        }

        return ret;
    }

    @Override
    protected String GetIP() {
        return YukiMessenger.config.GetString("ip", "127.0.0.1");
    }

    @Override
    protected String GetClientIP() {
        return Bukkit.getIp();
    }

    @Override
    protected int GetPort() {
        return YukiMessenger.config.GetInt("port", 8633);
    }

    @Override
    protected int GetClientPort() {
        return Bukkit.getPort();
    }

    @Override
    public String GetId() {
        return YukiMessenger.config.GetString("ident");
    }
}
