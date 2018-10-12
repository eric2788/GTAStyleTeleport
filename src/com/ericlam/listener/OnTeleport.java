package com.ericlam.listener;

import com.ericlam.main.Map;
import com.ericlam.main.GTAStyleTP;
import com.ericlam.timer.Countdown;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class OnTeleport implements Listener {
    private Plugin plugin = GTAStyleTP.plugin;
    private HashSet<Player> allow = new HashSet<>();
    private Map map = Map.getInstance();

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e){
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN && allow.contains(e.getPlayer())) {
            Player player = e.getPlayer();
            e.setCancelled(true);
            Location from = e.getFrom();
            Location to = e.getTo();
            GameMode gm = e.getPlayer().getGameMode();
            map.getGamemode().put(player,gm);
            Countdown count = new Countdown();
            count.startCountdown(e.getPlayer(), from, to, gm);
            allow.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        map.handlePlayerQuit(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if(!Map.getInstance().getFreeze().contains(e.getPlayer())) return;
        if (e.getPlayer().getLocation().getPitch() != e.getFrom().getPitch() || e.getPlayer().getLocation().getYaw() != e.getFrom().getYaw()) e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e){
        Player player = e.getPlayer();
        if (allow.contains(player)) return;
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(),"config.yml"));
        List<String> commandlist = config.getStringList("allow-command");
        for (String commands : commandlist) {
            if (e.getMessage().equalsIgnoreCase(commands)) {
                allow.add(player);
                map.getLoc().put(player,player.getLocation());
                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    if (!Map.getInstance().getCount().containsKey(player)){
                        allow.remove(player);
                        map.getLoc().remove(player);
                    }
                },20L);
            }
        }
    }
}
