package com.ericlam.main;

import com.ericlam.listener.OnTeleport;
import com.ericlam.timer.Countdown;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class GTAStyleTP extends JavaPlugin {
    public static Plugin plugin;
    @Override
    public void onEnable() {
        plugin = this;
        addNewFile("config.yml");
        addNewFile("test-location.yml");
        getLogger().info("GTAStyleTeleport enabled");
        this.getServer().getPluginManager().registerEvents(new OnTeleport(),this);
    }

    @Override
    public void onDisable() {
        getLogger().info("GTAStyleTeleport disable");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("you are not player!");
            return false;
        }
        if (!sender.hasPermission("gtateleport.tester")) {
            sender.sendMessage("§c沒有權限");
            return false;
        }
        File testlocfile = new File(this.getDataFolder(), "test-location.yml");
        FileConfiguration testloc = YamlConfiguration.loadConfiguration(testlocfile);
        Player player = (Player) sender;
        if (command.getName().equals("setloc")){
            Location loc = player.getLocation();
            double pitch = loc.getPitch();
            double yaw = loc.getYaw();
            testloc.set("world",loc.getWorld().getName());
            testloc.set("x",loc.getX());
            testloc.set("y",loc.getY());
            testloc.set("z",loc.getZ());
            testloc.set("pitch",pitch);
            testloc.set("yaw",yaw);
            try {
                testloc.save(testlocfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.sendMessage("§a測試位置設置成功! 可輸入 /loc 來測試GTA傳送");
        }
        if(command.getName().equals("loc")){
            if (!testloc.contains("world")) {
                player.sendMessage("§c尚未設置測試位置!");
                return false;
            }
            World world = Bukkit.getWorld(testloc.getString("world"));
            if (world == null){
                player.sendMessage("§c位置無效!");
                return false;
            }
            double x = testloc.getDouble("x");
            double y = testloc.getDouble("y");
            double z = testloc.getDouble("z");
            double pitch = testloc.getDouble("pitch");
            double yaw = testloc.getDouble("yaw");
            player.teleport(new Location(world,x,y,z,(float)yaw,(float)pitch));
        }
        if(command.getName().equals("return")){
            player.setAllowFlight(false);
            player.setGravity(true);
            player.setWalkSpeed(0.25F);
            player.sendMessage("§a Restored.");
        }
        return true;
    }

    private void addNewFile(String yaml){
        File file = new File(this.getDataFolder(),yaml);
        if (!file.exists()) this.saveResource(yaml,false);
        YamlConfiguration.loadConfiguration(file);
    }
}
