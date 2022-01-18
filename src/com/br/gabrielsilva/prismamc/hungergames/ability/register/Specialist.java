package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Specialist extends Kit {

	public Specialist() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
			
		
		ItemStack icone = new ItemBuilder().
				material(Material.ENCHANTED_BOOK).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(5);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Ganhe 1 nivel de XP");
		indiob.add(ChatColor.GRAY + "A cada player que voce matar.");
				setDescrição(indiob);
		
		setItens(new ItemBuilder().material(Material.ENCHANTMENT_TABLE).name(getItemColor() + "Kit " + getNome()).build());
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
			killer.setLevel(killer.getLevel() + 1);
		}
	}
}