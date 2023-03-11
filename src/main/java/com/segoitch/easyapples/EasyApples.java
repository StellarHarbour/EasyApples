package com.segoitch.easyapples;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;

public final class EasyApples extends JavaPlugin  implements Listener{
    Random random = new Random();

    ItemStack apple = new ItemStack(Material.APPLE,1 );
    ItemStack stick = new ItemStack(Material.STICK, random.nextInt(1,2));
    double appleProcChance = 0.05;
    double stickProcChance = 0.20;
    int currentTries = 0;
    int maxTries = 6;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onLeavesRightClick(PlayerInteractEvent event) {
        // Check to fire event only one time (because basically it fires once for each hand)
        if(Objects.requireNonNull(event.getHand()).equals(EquipmentSlot.HAND)
                && event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR)
                && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            //event.getPlayer().sendMessage("Hand is free"); //Debug
            if (Objects.requireNonNull(event.getClickedBlock()).getType() == Material.AZALEA_LEAVES
                    || Objects.requireNonNull(event.getClickedBlock()).getType() == Material.ACACIA_LEAVES
                    || Objects.requireNonNull(event.getClickedBlock()).getType() == Material.BIRCH_LEAVES
                    || Objects.requireNonNull(event.getClickedBlock()).getType() == Material.DARK_OAK_LEAVES
                    || Objects.requireNonNull(event.getClickedBlock()).getType() == Material.JUNGLE_LEAVES
                    || Objects.requireNonNull(event.getClickedBlock()).getType() == Material.MANGROVE_LEAVES
                    || Objects.requireNonNull(event.getClickedBlock()).getType() == Material.OAK_LEAVES
                    || Objects.requireNonNull(event.getClickedBlock()).getType() == Material.FLOWERING_AZALEA_LEAVES
                    || Objects.requireNonNull(event.getClickedBlock()).getType() == Material.SPRUCE_LEAVES) {
                //event.getPlayer().sendMessage("Leaves found"); //Debug
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_AZALEA_LEAVES_HIT, 0.10F, 1.0F);
                currentTries++;
                event.getPlayer().sendMessage("Tries: " + currentTries); //Debug
                if(Math.random() <= stickProcChance && currentTries < maxTries) {
                    //event.getPlayer().sendMessage("Chance proc"); //Debug
                    //event.getClickedBlock().breakNaturally(); //if you want drop block
                    event.getClickedBlock().setType(Material.AIR);
                    currentTries = 0;
                    event.getPlayer().getInventory().addItem(stick);
                    if(Math.random() <= appleProcChance) {
                        //event.getPlayer().sendMessage("Chance proc"); //Debug
                        //event.getClickedBlock().breakNaturally(); //if you want drop block
                        event.getPlayer().getInventory().addItem(apple);
                    }
                }
                if (currentTries >= maxTries) {
                    event.getClickedBlock().setType(Material.AIR);
                    currentTries = 0;
                    //chance failed, nothing gained
                }
            }
        }
    }
}