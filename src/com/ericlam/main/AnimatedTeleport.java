package com.ericlam.main;

import com.ericlam.listener.OnTeleport;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class AnimatedTeleport extends JavaPlugin {
    public static Plugin plugin;

    public static Sound sound;
    private static FileConfiguration config;

    public static String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', config.getString("messages.".concat(path)));
    }

    @Override
    public void onEnable() {
        plugin = this;
        addNewFile("config.yml");
        addNewFile("test-location.yml");
        getLogger().info("GTAStyleTeleport enabled");
        this.getCommand("ar-reload").setExecutor((commandSender, command, s, strings) -> {
            if (!commandSender.hasPermission("ar.reload")) {
                commandSender.sendMessage(getMessage("no-perm"));
                return true;
            }
            this.reloadConfig();
            commandSender.sendMessage(getMessage("reload-success"));
            return true;
        });
        this.getServer().getPluginManager().registerEvents(new OnTeleport(), this);
        config = this.getConfig();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        sound = Sound.valueOf(config.getString("teleport-sound"));
    }

    @Override
    public void onDisable() {
        Map map = Map.getInstance();
        for (Player player : Bukkit.getOnlinePlayers()) {
            map.handlePlayerQuit(player);
        }
        getLogger().info("GTAStyleTeleport disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("you are not player!");
            return false;
        }
        if (!sender.hasPermission("ar.tester")) {
            sender.sendMessage(getMessage("no-perm"));
            return false;
        }
        File testlocfile = new File(this.getDataFolder(), "test-location.yml");
        FileConfiguration testloc = YamlConfiguration.loadConfiguration(testlocfile);
        Player player = (Player) sender;
        if (command.getName().equals("setloc")) {
            Location loc = player.getLocation();
            testloc.createSection("", loc.serialize());
            try {
                testloc.save(testlocfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.sendMessage(getMessage("set-success"));
        }
        if(command.getName().equals("loc")){
            if (!testloc.contains("world")) {
                player.sendMessage(getMessage("no-loc"));
                return false;
            }
            World world = Bukkit.getWorld(testloc.getString("world"));
            if (world == null){
                player.sendMessage(getMessage("invalid-loc"));
                return false;
            }
            player.teleport(Location.deserialize(testloc.getValues(false)));
        }
        return true;
    }

    private void addNewFile(String yaml){
        File file = new File(this.getDataFolder(),yaml);
        if (!file.exists()) this.saveResource(yaml,false);
        YamlConfiguration.loadConfiguration(file);
    }
}
