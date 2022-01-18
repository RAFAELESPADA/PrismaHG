package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Tank extends Kit {

	public Tank() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
		
		ItemStack icone = new ItemBuilder().
				material(Material.DIAMOND_CHESTPLATE).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(5);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Crie uma grande explosão");
		indiob.add(ChatColor.GRAY + "Ao matar players.");
				setDescrição(indiob);
		setItens(new ItemStack(Material.AIR));
	}
	
	@EventHandler
	public void onDamage1(EntityDamageEvent event) {
	    if (event.getEntity() instanceof Player) {
	        Player p = (Player)event.getEntity();
	        if ((event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) || (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
	        	if (containsHability(p)) {
	        	    event.setCancelled(true);
	        	}
	        }
	    }
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent event) {
		final Player morreu = event.getEntity(),
				killer = morreu.getKiller();
		
		if (killer == null) {
			return;
		}
		
		
		if (!(killer instanceof Player)) {
			return;
		}
		
		if (containsHability(killer)) {
			World world = morreu.getWorld();
			world.createExplosion(morreu.getLocation(), 2.0F);
		}
	}
}