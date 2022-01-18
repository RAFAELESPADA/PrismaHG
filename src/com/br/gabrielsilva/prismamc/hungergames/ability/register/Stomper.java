package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Stomper extends Kit {

	public Stomper() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
			
		
		ItemStack icone = new ItemBuilder().
				material(Material.IRON_BOOTS).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(5);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Transfira seu dano de queda");
		indiob.add(ChatColor.GRAY + "Para seus inimigos.");
				setDescrição(indiob);
		
		setItens(new ItemStack(Material.AIR));

	}
	
	private final double STOMPER_FALL_NERF = 8.5D;
	
	@EventHandler
	public void stomperFall(EntityDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			
			if ((event.getCause() == EntityDamageEvent.DamageCause.FALL) && (useAbility(player))) {
				
				 if (event.getDamage() <= 4.0) {
					 return;
				 }
				 
				 boolean stompou = false;
				 List<Entity> entity = player.getNearbyEntities(6.0D, 3.0D, 6.0D);
				 for (Entity en : entity) {
					  if (en instanceof Player) {
						  Player stompados = (Player) en;
						  
						  if (!stompados.getGameMode().equals(GameMode.SURVIVAL)) {
							  continue;
						  }
						  
						  if (!HungerGames.getManager().getGamer(stompados.getUniqueId()).isJogando()) {
							  continue;
						  }
						  
						  double damage = 4.0D,
								  life = stompados.getHealth();
						  
						  if (!stompados.isSneaking()) {
							  damage = player.getFallDistance() - STOMPER_FALL_NERF;
						  }
						  
						  if (life - damage <= 0.0D) {
							  stompados.setHealth(1.0D);
							  stompados.damage(2.0, player);
						  } else {
							  stompados.setHealth(life - damage);
						  }
						  
						  stompados.playSound(stompados.getLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);
						  stompou = true;
					  }
				 }
				 
				 if (stompou) {
					 player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);
				 }
				 
				 entity.clear();
				 entity = null;
				 
				 if (event.getDamage() > 4.0D) {
					 event.setDamage(4.0D);
				 }
			}
		}
	}
}