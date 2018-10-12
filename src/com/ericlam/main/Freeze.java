package com.ericlam.main;

import org.bukkit.entity.Player;

import java.util.HashSet;

public class Freeze {
    private static Freeze freezen;

    public static Freeze getInstance() {
        if (freezen == null) freezen = new Freeze();
        return freezen;
    }

    private HashSet<Player> freeze = new HashSet<>();

    public HashSet<Player> getFreeze() {
        return freeze;
    }
}
