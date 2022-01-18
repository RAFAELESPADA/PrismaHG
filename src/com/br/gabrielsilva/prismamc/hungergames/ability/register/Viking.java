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

public class Viking extends Kit {

	public Viking() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
			
		ItemStack icone = new ItemBuilder().
				material(Material.STONE_AXE).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(5);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "De mais danos");
		indiob.add(ChatColor.GRAY + "Com machados.");
				setDescrição(indiob);
		
		setItens(new ItemStack(Material.AIR));

	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void Habilidade(EntityDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if ((p.getItemInHand().getType().toString().contains("AXE")) && (useAbility(p))) {
				 e.setDamage(e.getDamage() + 2.0D);
			}
		}
	}
}