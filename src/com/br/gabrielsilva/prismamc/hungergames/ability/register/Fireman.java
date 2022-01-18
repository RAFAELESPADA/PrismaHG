package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Fireman extends Kit {

	public Fireman() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
		
		ItemStack icone = new ItemBuilder().
				material(Material.LAVA_BUCKET).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(60);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Seja imune a fogo e lava");
				setDescrição(indiob);
		setItens(new ItemStack(Material.WATER_BUCKET));
	}
	
	@EventHandler
	public void Habilidade(EntityDamageEvent e) {
		if (e.isCancelled())
			return;
		
		if (e.getEntity() instanceof Player) {
			if (e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.FIRE_TICK || e.getCause() == DamageCause.LAVA) {
				if (containsHability((Player) e.getEntity())) {
				    e.setCancelled(true);
				    e.getEntity().setFireTicks(0);
				}
			}
		}
	}
}
