package com.bilicraft.resourceswarning;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Container;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Set;

public final class ResourcesWarning extends JavaPlugin implements Listener {

    private String title;
    private String subtitle;
    private String chat;
    private List<String> world;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this,this);
        saveDefaultConfig();
        reloadConfig();
        parseColours(getConfig());
        this.title =parseColours(getConfig().getString("title"));
        this.subtitle =parseColours( getConfig().getString("subtitle"));
        StringBuilder chatBuilder = new StringBuilder();
        getConfig().getStringList("chat").forEach(str->{
            chatBuilder.append("\n").append(str);
        });
        this.chat = parseColours(chatBuilder.toString());
        this.world = getConfig().getStringList("world");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event){
        if(!world.contains(event.getBlockPlaced().getLocation().getWorld().getName())){
            return;
        }
       if(event.getBlockPlaced().getState() instanceof Container){
           if(event.getBlockPlaced().getType() != Material.FURNACE){
               Player player = event.getPlayer();
               player.sendTitle(title,subtitle,0,80,0);
               player.sendMessage(chat);
               player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE,1.0f,1.0f);
           }
       }
    }

    public void parseColours(Configuration config) {
        Set<String> keys = config.getKeys(true);
        for (String key : keys) {
            String filtered = config.getString(key);
            if (filtered == null) {
                continue;
            }
            if (filtered.startsWith("MemorySection")) {
                continue;
            }
            filtered = parseColours(filtered);
            config.set(key, filtered);
        }
    }
    public String parseColours(String text) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }
        text = ChatColor.translateAlternateColorCodes('&', text);
        return text;
    }
}
