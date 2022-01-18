package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Hulk extends Kit {

	public Hulk() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
		
		ItemStack icone = new ItemBuilder().
				material(Material.SADDLE).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(15);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Segure o inimigo");
		indiob.add(ChatColor.GRAY + "E Arremesse ele");
				setDescrição(indiob);
		setItens(new ItemStack(Material.AIR));
	}
	
	@EventHandler
	public void hulk(PlayerInteractEntityEvent e) {
		if (e.getPlayer() instanceof Player && e.getRightClicked() instanceof Player) {
			Player p = (Player) e.getPlayer();
			if (p.getInventory().getItemInHand().getType().equals(Material.AIR)) {
				if (useAbility(p)) {
					if (inCooldown(p)) {
						sendMessageCooldown(p);
						return;
					}
					Player d = (Player) e.getRightClicked();
					p.setPassenger(d);
					addCooldown(p, getCooldownSegundos());
				}
			}
		}
	}

	@EventHandler
	public void hulkmatar(EntityDeathEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (p.isInsideVehicle()) {
				p.leaveVehicle();
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
		
		Player damaged = (Player) event.getEntity(),
				damager = (Player) event.getDamager();
		
		if ((damaged.isInsideVehicle()) && (useAbility(damager))) {
			 event.setCancelled(true);
			 damaged.leaveVehicle();
			 
			 damaged.setVelocity(new Vector(damager.getLocation().getDirection().getX() + 0.3, 1.5, damager.getLocation().getDirection().getZ() + 0.3));
		}
	}
}