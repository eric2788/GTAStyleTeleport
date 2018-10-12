package com.ericlam.main;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;

public class Map {
    private static Map freezen;

    public static Map getInstance() {
        if (freezen == null) freezen = new Map();
        return freezen;
    }

    private HashSet<Player> freeze = new HashSet<>();
    private HashMap<Player, Integer> count = new HashMap<>();
    private HashMap<Player, Location> loc = new HashMap<>();
    private HashMap<Player, GameMode> gamemode = new HashMap<>();

    public HashMap<Player, Location> getLoc() {
        return loc;
    }

    public HashMap<Player, GameMode> getGamemode() {
        return gamemode;
    }

    public HashMap<Player, Integer> getCount() {
        return count;
    }

    public HashSet<Player> getFreeze() {
        return freeze;
    }

    public void handlePlayerQuit(Player player){
        if(!Map.getInstance().getCount().containsKey(player)) return;
            player.setAllowFlight(false);
            player.setFlySpeed(0.25F);
            player.setGameMode(gamemode.get(player));
            player.teleport(loc.get(player));
            count.remove(player);
            freeze.remove(player);
            gamemode.remove(player);
            loc.remove(player);
    }
}
