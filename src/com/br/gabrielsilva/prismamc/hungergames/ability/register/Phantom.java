package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Phantom extends Kit {

	public Phantom() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
			
		ItemStack icone = new ItemBuilder().
				material(Material.FEATHER).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(45);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Use sua pena");
		indiob.add(ChatColor.GRAY + "Para voar por 5 segundos.");
				setDescrição(indiob);
		setItens(new ItemBuilder().material(Material.FEATHER).name(getItemColor() + "Kit " + getNome()).build());
	}
	
	@EventHandler
	public void use(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if ((player.getItemInHand().getType().equals(Material.FEATHER)) && (useAbility(player))) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (inCooldown(player)) {
					sendMessageCooldown(player);
					return;
				}
				addCooldown(player, getCooldownSegundos());
				player.setAllowFlight(true);
				player.setFlying(true);
				player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 2.0F, 1.0F);
				
				for (Entity c : player.getNearbyEntities(30, 30, 30)) {
				     if (c instanceof Player && c != player) {
						 Player d = (Player) c;
						 d.sendMessage("§cHa um phantom por perto!");
						 d.playSound(d.getLocation(), Sound.ENDERMAN_TELEPORT, 3.0F, 4.0F);
				     }
				}
			    HungerGames.runLater(() -> {
			    	if (!player.isOnline()) {
			    		return;
			    	}
					player.playSound(player.getLocation(), Sound.AMBIENCE_RAIN, 3.0F, 4.0F);
					player.setAllowFlight(false);
					player.setFlying(false);
				 }, 142L);
			}
		}
	}
}