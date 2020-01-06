package com.ericlam.timer;

import com.ericlam.main.AnimatedTeleport;
import com.ericlam.main.Map;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.HashMap;

public class Countdown {
    private Plugin plugin = AnimatedTeleport.plugin;
    private FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
    private double time = config.getInt("time");
    private final double origintime = time;
    private HashMap<Player, Integer> count = Map.getInstance().getCount();

    public void startCountdown(Player player, Location from, Location to, GameMode beforegammemode){
        if (count.containsKey(player)) return;
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        int intervalY = config.getInt("Interval-Y");
        final Location originTo = to;
        int task = scheduler.scheduleSyncRepeatingTask(plugin, ()->{
            double y = from.getY();
            double y2 = to.getY();
            if (time == Math.round(origintime*0.8) || time == Math.round(origintime*1)){
                if (time == Math.round(origintime*1)){
                    player.setAllowFlight(true);
                    player.setFlySpeed(0);
                    player.setFlying(true);
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendTitle(AnimatedTeleport.getMessage("tp-title"), "", 10, (int) origintime * 20, 10);
                    Map.getInstance().getFreeze().add(player);
                }
                from.setPitch(90);
                from.setYaw(90);
                y += intervalY;
                from.setY(y);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0));
                player.playSound(from, AnimatedTeleport.sound, 1, 1);
                player.teleport(from);
                //player.sendMessage("DEBUG: y is now "+y);
            }

            if(time == Math.round(origintime*0.6)){
                to.setPitch(90);
                to.setYaw(90);
                y2 += intervalY*3;
                to.setY(y2);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0));
                player.playSound(to, AnimatedTeleport.sound, 1, 1);
                player.teleport(to);
                //player.sendMessage("DEBUG: y is now "+y2);
            }

            if (time == Math.round(origintime*0.4) || time == Math.round(origintime*0.2) || time == Math.round(origintime*0)) {
                to.setPitch(90);
                to.setYaw(90);
                y2 -= intervalY;
                player.setAllowFlight(true);
                player.setFlying(true);
                player.setFlySpeed(0.0f);
                to.setY(y2);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0));
                player.playSound(to, AnimatedTeleport.sound, 1, 1);
                //player.sendMessage("DEBUG: y is now "+y2);
                if (time == Math.round(origintime * 0)) {
                    Map map = Map.getInstance();
                    player.teleport(originTo);
                    player.setGameMode(beforegammemode);
                    player.setFlying(false);
                    player.setFlySpeed(0.1F);
                    player.setGravity(true);
                    if (beforegammemode != GameMode.CREATIVE) player.setAllowFlight(false);
                    map.getFreeze().remove(player);
                    map.getLoc().remove(player);
                    map.getGamemode().remove(player);
                    stopCountdown(player);
                    return;
                }
                player.teleport(to);
            }


            //player.sendMessage("DEBUG: timer: "+time);
            time -= 1;
        },0L,20L);
        count.put(player,task);
    }

    private void stopCountdown(Player player){
        if (!count.containsKey(player)) return;
        Bukkit.getScheduler().cancelTask(count.get(player));
        count.remove(player);
    }

}
