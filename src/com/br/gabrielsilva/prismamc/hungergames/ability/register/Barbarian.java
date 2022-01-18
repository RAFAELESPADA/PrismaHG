package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.server.ServerAPI;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Barbarian extends Kit {

	public Barbarian() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
			
		
	ItemStack icone = new ItemBuilder().
			material(Material.IRON_SWORD).
			durability(0).
			amount(1)
			.build();
			
			setIcone(icone);
			
			setPreço(3000);
			setCooldownSegundos(0);
	        ArrayList indiob = new ArrayList();
	/* 351 */         indiob.add(ChatColor.GRAY + "Upe de espada a cada kill");
			setDescrição(indiob);
	
		setItens(new ItemBuilder().material(Material.WOOD_SWORD).name(getItemColor() + "Barbarian Sword").inquebravel().build());
	}
	
	private HashMap<UUID, Integer> kills = new HashMap<UUID, Integer>();

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
		
		if (this.kills.containsKey(killer.getUniqueId())) {
			this.kills.put(killer.getUniqueId(), this.kills.get(killer.getUniqueId()) + 1);
		} else {
			this.kills.put(killer.getUniqueId(), 1);
		}
		
		if (ServerAPI.checkItem(killer.getItemInHand(), getItemColor() + "Barbarian Sword")) {
			switch (this.kills.get(killer.getUniqueId())) {
			case 1:
				killer.getItemInHand().setType(Material.STONE_SWORD);
				killer.getItemInHand().setDurability((short) 0);
				break;
			case 5:
				killer.getItemInHand().setType(Material.IRON_SWORD);
				killer.getItemInHand().setDurability((short) 0);
				break;
			case 8:
				killer.getItemInHand().setType(Material.DIAMOND_SWORD);
				killer.getItemInHand().setDurability((short) 0);
				break;
			case 10:
				killer.getItemInHand().addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
				killer.getItemInHand().setDurability((short) 0);
				break;
			case 12:
				killer.getItemInHand().addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
				killer.getItemInHand().setDurability((short) 0);
				break;
			}
		}
	}
}