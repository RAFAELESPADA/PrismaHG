package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Worm extends Kit {

	public Worm() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
			
		ItemStack icone = new ItemBuilder().
				material(Material.DIRT).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(5);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Quebre terras instantaneamente");
		indiob.add(ChatColor.GRAY + "E recupe sua fome quebrando elas");
		setDescrição(indiob);
		setItens(new ItemStack(Material.AIR));
	}
	
	@EventHandler
	public void entityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if ((e.getCause().equals(DamageCause.FALL)) && (useAbility(p))) {
				if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.DIRT)) {
					e.setDamage(8D);
				}
			}
		}
	}
	
	@EventHandler
	public void damage(BlockDamageEvent e) {
		if ((e.getBlock().getType().equals(Material.DIRT)) && (useAbility(e.getPlayer()))) {
			 e.setInstaBreak(true);
			 e.getPlayer().setFoodLevel(20);
			 e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
		}
	}
}
