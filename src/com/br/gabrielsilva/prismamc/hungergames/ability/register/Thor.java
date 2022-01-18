package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Thor extends Kit {

	public Thor() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
		
		ItemStack icone = new ItemBuilder().
				material(Material.GOLD_AXE).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(5);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Use seu machado para");
		indiob.add(ChatColor.GRAY + "invocar raios.");
				setDescrição(indiob);
		
		setItens(new ItemBuilder().material(Material.WOOD_AXE).name(
				getItemColor() + "Kit " + getNome()).build());
	}
	
	@EventHandler
	public void Habilidade(PlayerInteractEvent event) {
		if ((event.getPlayer().getItemInHand().getType().equals(Material.WOOD_AXE)) && (event.getAction() == Action.RIGHT_CLICK_BLOCK) && 
				(useAbility(event.getPlayer()))) {
			
			if (inCooldown(event.getPlayer())) {
				sendMessageCooldown(event.getPlayer());
				return;
			}
			addCooldown(event.getPlayer(), getCooldownSegundos());
			
			Location loc = new Location(event.getPlayer().getWorld(),
					event.getClickedBlock().getX(), (event.getPlayer().getWorld().getHighestBlockYAt(event.getClickedBlock().getX(),
							event.getClickedBlock().getZ())), event.getClickedBlock().getZ());
				
			loc = loc.subtract(0, 1, 0);
			if (loc.getBlock().getType() == Material.AIR) {
				loc = loc.subtract(0, 1, 0);
			}
				
			LightningStrike strike = loc.getWorld().strikeLightning(loc);
	
			for (Entity nearby : strike.getNearbyEntities(4.0D, 4.0D, 4.0D)) {
				 if ((nearby instanceof Player)) {
					 nearby.setFireTicks(100);
				 }
				 event.getPlayer().setFireTicks(0);
			}
			
			if (loc.getBlock().getType() == Material.NETHERRACK) {
			    event.getPlayer().getWorld().createExplosion(loc.clone().add(0, 2, 0), 2.7F);
			} else {
				loc.getBlock().setType(Material.NETHERRACK);
				loc.getBlock().getRelative(BlockFace.UP).setType(Material.FIRE);
			}
		}
	}
	
	@EventHandler
	public void damage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (event.getCause() == DamageCause.LIGHTNING) {
				if (containsHability(player)) {
					player.setFireTicks(0);
					event.setCancelled(true);
				}
			}
		}
	}
}