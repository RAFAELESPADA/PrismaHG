package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Boxer extends Kit {

	public Boxer() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
			
		
	ItemStack icone = new ItemBuilder().
			material(Material.QUARTZ).
			durability(0).
			amount(1)
			.build();
			
			setIcone(icone);
			
			setPreço(3000);
			setCooldownSegundos(0);
	        ArrayList indiob = new ArrayList();
	/* 351 */         indiob.add(ChatColor.GRAY + "De mais dano e leve menos dano.");
			setDescrição(indiob);
		setItens(new ItemStack(Material.AIR));
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void BoxerHabilidade(EntityDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		
		if ((e.getEntity() instanceof Player) && (e.getDamager() instanceof Player)) {
			Player p = (Player) e.getEntity();
			Player d = (Player) e.getDamager();
			
			if (useAbility(p)) {
				e.setDamage(e.getDamage() - 1.0);
			}
			
			if (useAbility(d)) {
				if (d.getItemInHand().getType() == Material.AIR) {
					e.setDamage(e.getDamage() + 1.0D);
				}
				e.setDamage(e.getDamage() + 1.0);
			}
		}
	}
}