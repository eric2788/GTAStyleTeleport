package com.ericlam.listener;

import com.ericlam.main.Map;
import com.ericlam.main.GTAStyleTP;
import com.ericlam.timer.Countdown;
import com.ericlam.timer.CountdownOnJoin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class OnTeleport implements Listener {
    private Plugin plugin = GTAStyleTP.plugin;
    private HashSet<Player> allow = new HashSet<>();
    private Map map = Map.getInstance();
    private FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(),"config.yml"));

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e){
        Player player = e.getPlayer();
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN && allow.contains(e.getPlayer())) {
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
        String[] label = e.getMessage().split(" ");
        if (map.getCount().containsKey(player)){
            e.setCancelled(true);
            player.sendMessage("§c傳送中，無法執行其他指令!");
            return;
        }
        if (allow.contains(player)) return;
        List<String> commandlist = config.getStringList("allow-command");
        for (String commands : commandlist) {
            if (label[0].equalsIgnoreCase(commands)) {
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        if (!config.getBoolean("enable-on-join") || !player.hasPermission("gtateleport.join")) return;
        Location to = player.getLocation();
        GameMode gm = player.getGameMode();
        map.getGamemode().put(player,gm);
        map.getLoc().put(player,to);
        CountdownOnJoin jointp = new CountdownOnJoin();
        Bukkit.getScheduler().runTaskLater(plugin,()-> jointp.startCountdown(player,to,gm),10L);

    }
}
