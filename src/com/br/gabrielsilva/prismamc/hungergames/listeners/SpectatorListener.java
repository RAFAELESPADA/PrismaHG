package com.br.gabrielsilva.prismamc.hungergames.listeners;

import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.VanishManager;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;

public class SpectatorListener implements Listener {

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = (Player)event.getEntity();
			if (!HungerGames.getManager().getGamer(player.getUniqueId()).isJogando()) {
			    event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player bateu = (Player) event.getDamager();
			if (VanishManager.inAdmin(bateu)) {
				event.setCancelled(true);
				return;
			}
			if (!HungerGames.getManager().getGamer(bateu.getUniqueId()).isJogando()) {
				event.setCancelled(true);
				return;
			}
		}
		if (event.getEntity() instanceof Player) {
			Player sofreu = (Player) event.getEntity();
			if (!HungerGames.getManager().getGamer(sofreu.getUniqueId()).isJogando()) {
				event.setCancelled(true);
				return;
			}
			if (VanishManager.inAdmin(sofreu)) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void pickUp(PlayerPickupItemEvent event) {
		if (VanishManager.inAdmin(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
		if (!HungerGames.getManager().getGamer(event.getPlayer().getUniqueId()).isJogando()) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onTarget(EntityTargetEvent event) {
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
			return;
		}
		
		if (event.getTarget() instanceof Player) {
			final Player player = (Player) event.getTarget();
			if (!HungerGames.getManager().getGamer(player.getUniqueId()).isJogando()) {
				event.setCancelled(true);
				return;
			}
			if (VanishManager.inAdmin(player)) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onInteractBlock(PlayerInteractEvent event) {
		if (!HungerGames.getManager().getGamer(event.getPlayer().getUniqueId()).isJogando()) {
			if (event.getAction() == Action.PHYSICAL) {
				event.setCancelled(true);
				return;
			}
			
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Block b = event.getClickedBlock();
				if (b.getState() instanceof DoubleChest || b.getState() instanceof Chest || b.getState() instanceof Hopper || b.getState() instanceof Dispenser || b.getState() instanceof Furnace || b.getState() instanceof Beacon) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void drop(PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
		if (!HungerGames.getManager().getGamer(player.getUniqueId()).isJogando()) {
			event.setCancelled(true);
			return;
		}
		if (VanishManager.inAdmin(player)) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (!HungerGames.getManager().getGameManager().isPreGame()) {
			final Player player = event.getPlayer();
			if (VanishManager.inAdmin(player)) {
				event.setCancelled(false);
				return;
			}
			if (!HungerGames.getManager().getGamer(player.getUniqueId()).isJogando()) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (!HungerGames.getManager().getGameManager().isPreGame()) {
			final Player player = event.getPlayer();
			if (VanishManager.inAdmin(player)) {
				event.setCancelled(false);
				return;
			}
			if (!HungerGames.getManager().getGamer(player.getUniqueId()).isJogando()) {
				event.setCancelled(true);
				return;
			}
		}
	}
}