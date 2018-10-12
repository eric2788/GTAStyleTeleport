package com.ericlam.listener;

import com.ericlam.main.MineStrike;
import com.ericlam.timer.Countdown;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

public class OnTeleport implements Listener {
    private Plugin plugin = MineStrike.plugin;

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e){
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN || e.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {
            e.setCancelled(true);
            Location from = e.getFrom();
            Location to = e.getTo();
            GameMode gm = e.getPlayer().getGameMode();
            Countdown count = new Countdown();
            count.startCountdown(e.getPlayer(), from, to, gm);
        }
    }
}
