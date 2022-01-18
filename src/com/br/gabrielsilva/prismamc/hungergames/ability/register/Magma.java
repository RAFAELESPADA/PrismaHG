package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Magma extends Kit {

	public Magma() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
			
		ItemStack icone = new ItemBuilder().
				material(Material.LAVA_BUCKET).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(5);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Tenha chance de botar fogo");
		indiob.add(ChatColor.GRAY + "Ao hitar o inimigo");
				setDescrição(indiob);
		
		setItens(new ItemStack(Material.AIR));
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void Dano(EntityDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		
		if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
			Player p = (Player) e.getDamager();
			if (useAbility(p)) {
				if (new Random().nextInt(100) <= 35) {
					LivingEntity d = (LivingEntity) e.getEntity();
					d.setFireTicks(d.getFireTicks() + 120);
				}
			}
		}
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
	
	@EventHandler
	public void Habilidade(PlayerMoveEvent e) {
		if (e.isCancelled())
			return;
		
		Player p = e.getPlayer();
		if (e.getTo().getBlock().getType().equals(Material.WATER) || e.getTo().getBlock().getType().equals(Material.STATIONARY_WATER)) {
		    if (useAbility(p)) {
		    	p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 140, 6));
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
				p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 30, 0));
				p.damage(1.0D);
		    }
		}
	}
}