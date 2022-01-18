package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;
import java.util.Random;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Cannibal extends Kit {

	public Cannibal() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
			
		ItemStack icone = new ItemBuilder().
				material(Material.ROTTEN_FLESH).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(0);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "De fome nos inimigos e regenere sua fome ao hitar");
				setDescrição(indiob);
		
		setItens(new ItemStack(Material.AIR));
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void Dano(EntityDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			Player p = (Player) e.getDamager();
			if (useAbility(p)) {
				if (new Random().nextInt(100) <= 35) {
					Player d = (Player) e.getEntity();
					
					HungerGames.runLater(() -> {
						d.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 150, 2));
						if (new Random().nextInt(100) <= 35) {
							d.setFoodLevel((int) (d.getFoodLevel() - 1.0D));
						}
					}, 1);
				}
			}
		}
	}
}