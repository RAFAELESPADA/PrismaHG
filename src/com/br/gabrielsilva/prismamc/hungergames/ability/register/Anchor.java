package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Anchor extends Kit {

	public Anchor() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
		
		ItemStack icone = new ItemBuilder().
				material(Material.ANVIL).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(0);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Não tome ou leve KB");
				setDescrição(indiob);
			
		setItens(new ItemStack(Material.AIR));
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void Habilidade(EntityDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		
 		if ((e.getEntity() instanceof Player) && (e.getDamager() instanceof Player)) {
			 Player p = (Player) e.getEntity();
			 Player d = (Player) e.getDamager();
			 
			 if (useAbility(p)) {
				 handle(p, d);
			 } else if (useAbility(d)) {
				 handle(p, d);
			 }
 		}
	}
	
	public void handle(Player player1, Player player2) {
		 player1.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
 		 player2.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
 		 
		 player1.playSound(player1.getLocation(), Sound.ANVIL_LAND, 1, 1);
		 player2.playSound(player2.getLocation(), Sound.ANVIL_LAND, 1, 1);
 		 
		 HungerGames.runLater(() -> {
			 player1.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
	 		 player2.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
		 }, 1L);
	}
}