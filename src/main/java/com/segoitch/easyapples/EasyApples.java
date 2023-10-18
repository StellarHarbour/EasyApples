package com.segoitch.easyapples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyApples extends JavaPlugin implements Listener {

    private boolean isWorldGuardExist;
    private ItemStack apple;
    private ItemStack stick;
    private double appleProcChance;
    private double stickProcChance;
    private HashMap<Player, Integer> playerTries;
    private int maxTries;
    private List<Material> leavesList;

    @Override
    public void onEnable() {
        this.isWorldGuardExist = this.isWorldGuardExist();
        this.apple = new ItemStack(Material.APPLE, 1);
        this.stick = new ItemStack(Material.STICK, 1);
        this.appleProcChance = 0.05;
        this.stickProcChance = 0.20;
        this.playerTries = new HashMap<>();
        this.maxTries = 6;
        this.leavesList = new ArrayList<>();
        Collections.addAll(this.leavesList, Material.AZALEA_LEAVES, Material.ACACIA_LEAVES, Material.BIRCH_LEAVES,
                Material.DARK_OAK_LEAVES, Material.JUNGLE_LEAVES, Material.MANGROVE_LEAVES, Material.OAK_LEAVES,
                Material.FLOWERING_AZALEA_LEAVES, Material.SPRUCE_LEAVES);

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        // Check to fire event only one time (because basically it fires once for each hand)
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() == Action.LEFT_CLICK_BLOCK
                || event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) return;
        if (!leavesList.contains(event.getClickedBlock().getType())) return;
        if(event.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) return;

        final Block block = event.getClickedBlock();
        if(this.wgCancelled(player, block)) return;

        if (!playerTries.containsKey(player)) playerTries.put(player, 0);

        player.playSound(event.getClickedBlock().getLocation(), Sound.BLOCK_AZALEA_LEAVES_HIT, 0.10F, 1.0F);

        int currentPlayerValue = this.playerTries.get(player);
        this.playerTries.replace(player, ++currentPlayerValue); // have to be pre-increment to make it work
        player.sendMessage();

        if (Math.random() <= this.stickProcChance && this.playerTries.get(player) < this.maxTries) {
            event.getClickedBlock().breakNaturally(); // to get saplings too
            player.playSound(event.getClickedBlock().getLocation(), Sound.BLOCK_AZALEA_LEAVES_BREAK, 0.33F, 1.0F);
            this.playerTries.replace(player, 0);

            ItemStack randomStick = this.stick;
            randomStick.setAmount(new Random().nextInt(1, 2));

            player.getInventory().addItem(randomStick);

            if (Math.random() <= this.appleProcChance) player.getInventory().addItem(this.apple);
        }
        if (this.playerTries.get(player) >= this.maxTries) { // chance failed, nothing gained
            event.getClickedBlock().breakNaturally(); // to get saplings too
            player.playSound(event.getClickedBlock().getLocation(), Sound.BLOCK_AZALEA_LEAVES_BREAK, 0.33F, 1.0F);
            this.playerTries.replace(player, 0);
        }
    }

    private boolean isWorldGuardExist() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        return plugin instanceof WorldGuardPlugin;
    }

    private boolean wgCancelled(final Player player, final Block block) {
        if(block == null || !this.isWorldGuardExist) return false;
        boolean result = false;

        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        final Location loc = BukkitAdapter.adapt(block.getLocation());
        final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionQuery query = container.createQuery();

        if (!query.testState(loc, localPlayer, Flags.LEAF_DECAY)) result = true;
        if (!query.testState(loc, localPlayer, Flags.BLOCK_BREAK)) result = true;

        return result;
    }
}
