package com.segoitch.easyapples;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyApples extends JavaPlugin implements Listener {

    private Random random;
    private ItemStack apple;
    private ItemStack stick;
    private double appleProcChance;
    private double stickProcChance;
    private HashMap<Player, Integer> playerTries;
    private int maxTries;
    private List<Material> leavesList;

    @Override
    public void onEnable() {
        this.random = new Random();
        this.apple = new ItemStack(Material.APPLE, 1);
        this.stick = new ItemStack(Material.STICK, random.nextInt(1, 2));
        this.appleProcChance = 0.05;
        this.stickProcChance = 0.20;
        this.playerTries = new HashMap<>();
        this.maxTries = 6;
        this.leavesList = new ArrayList<>();
        Collections.addAll(this.leavesList, Material.AZALEA_LEAVES, Material.ACACIA_LEAVES, Material.BIRCH_LEAVES,
                Material.DARK_OAK_LEAVES, Material.JUNGLE_LEAVES, Material.MANGROVE_LEAVES, Material.OAK_LEAVES,
                Material.FLOWERING_AZALEA_LEAVES, Material.SPRUCE_LEAVES);

        Bukkit.getPluginManager().registerEvents(this, this);
        super.onEnable();
    }

    @EventHandler
    public void onLeavesRightClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        // Check to fire event only one time (because basically it fires once for each hand)
        if (!Objects.equals(event.getHand(), EquipmentSlot.HAND) || event.getAction() == Action.LEFT_CLICK_BLOCK
                || event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) return;
        if (!leavesList.contains(event.getClickedBlock().getType())) return;
        if (!playerTries.containsKey(player)) playerTries.put(player, 0);

        player.playSound(player.getLocation(), Sound.BLOCK_AZALEA_LEAVES_HIT, 0.10F, 1.0F);

        int currentPlayerValue = this.playerTries.get(player);
        this.playerTries.replace(player, currentPlayerValue++);

        if (Math.random() <= this.stickProcChance && this.playerTries.get(player) < this.maxTries) {
            event.getClickedBlock().setType(Material.AIR);
            this.playerTries.replace(player, 0);
            player.getInventory().addItem(this.stick);

            if (Math.random() <= this.appleProcChance) {
                player.getInventory().addItem(this.apple);

            }
            // event.getClickedBlock().breakNaturally(); // if you want to drop block
            return;
        }
        if (this.playerTries.get(player) >= this.maxTries) { // chance failed, nothing gained
            event.getClickedBlock().setType(Material.AIR);
            this.playerTries.replace(player, 0);
        }
    }
}
