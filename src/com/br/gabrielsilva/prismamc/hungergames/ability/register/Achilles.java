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

public class Achilles extends Kit {

	public Achilles() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
		
		ItemStack icone = new ItemBuilder().
		material(Material.WOOD_SWORD).
		durability(0).
		amount(1)
		.build();
		
		setIcone(icone);
		
		setPreço(3000);
		setCooldownSegundos(0);
        ArrayList indiob = new ArrayList();
/* 351 */         indiob.add(ChatColor.GRAY + "Leve menos dano para tudo menos espada de madeira");
		setDescrição(indiob);

		setItens(new ItemStack(Material.AIR));
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void AchillesEvent(EntityDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		
		if ((e.getEntity() instanceof Player) && (e.getDamager() instanceof Player)) {
			Player p = (Player) e.getEntity();
			if (useAbility(p)) {
				Player d = (Player) e.getDamager();
				if (d.getItemInHand().getType() != Material.WOOD_SWORD) {
					if (d.getItemInHand().getType().equals(Material.STONE_SWORD)) {
						e.setDamage(3.0D);
					} else if (d.getItemInHand().getType().equals(Material.IRON_SWORD)) {
						e.setDamage(3.0D);
					} else if (d.getItemInHand().getType().equals(Material.DIAMOND_SWORD)) {
						e.setDamage(3.0D);
					} else {
						e.setDamage(1.0D);
					}
				} else {
					e.setDamage(7.0D);
				}
			}
		}
	}
}