package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Launcher extends Kit {

	public Launcher() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
			
		ItemStack icone = new ItemBuilder().
				material(Material.SPONGE).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(20);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Ganhe esponjas");
		indiob.add(ChatColor.GRAY + "Para dar pulos grandes");
				setDescrição(indiob);
		setItens(new ItemBuilder().material(Material.SPONGE).name(getItemColor() + "Kit" + getNome()).amount(20).build());
	}
	
	public static ArrayList<UUID> fallProtect = new ArrayList<>();
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void JumpBlocks(PlayerMoveEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Material material = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
		if (material.equals(Material.SPONGE)) {
			Player player = event.getPlayer();
			
			Location loc = event.getTo().getBlock().getLocation();
			
			//DamageListener.addBypassVelocity(player);
			Vector sponge = player.getLocation().getDirection().multiply(0).setY(4);
			player.setVelocity(sponge);
			player.playSound(loc, Sound.FIREWORK_LAUNCH, 6.0F, 1.0F);
			
			if (!fallProtect.contains(player.getUniqueId())) {
				fallProtect.add(player.getUniqueId());
			}
			
			HungerGames.runLater(() -> {
				if (!fallProtect.contains(player.getUniqueId())) {
					fallProtect.add(player.getUniqueId());
				}
			}, 10);
		}
	}
	
	@EventHandler
	public void damage(EntityDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		final Player player = (Player)event.getEntity();
		if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
			if (fallProtect.contains(player.getUniqueId())) {
				if (HungerGames.getManager().getGamer(player.getUniqueId()).containsKit("Stomper")) {
					return;
				}
				event.setCancelled(true);
				fallProtect.remove(player.getUniqueId());
			}
		}
	}
	
	@EventHandler
	public void removeOnMove(PlayerMoveEvent e) {
		if (fallProtect.contains(e.getPlayer().getUniqueId()) && 
				(e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR 
				   || e.getPlayer().isOnGround())) {
			
			HungerGames.runLater(() -> {
				if (fallProtect.contains(e.getPlayer().getUniqueId())) {
					fallProtect.remove(e.getPlayer().getUniqueId());
				}
			}, 2);
		}
	}
}