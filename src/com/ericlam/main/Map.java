package com.ericlam.main;

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

    public HashMap<Player, Integer> getCount() {
        return count;
    }

    public HashSet<Player> getFreeze() {
        return freeze;
    }
}
