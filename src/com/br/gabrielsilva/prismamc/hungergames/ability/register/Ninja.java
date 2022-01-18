package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Ninja extends Kit {

	public Ninja() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
			
		ItemStack icone = new ItemBuilder().
				material(Material.COAL).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(8);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Teleporte ao ultimo player hitado");
		indiob.add(ChatColor.GRAY + "Apertando SHIFT");
				setDescrição(indiob);
		
		setItens(new ItemStack(Material.AIR));

	}
	
	HashMap<UUID, UUID> tp = new HashMap<>();
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (tp.containsKey(event.getPlayer().getUniqueId())) {
			tp.remove(event.getPlayer().getUniqueId());
		}
	}
	
	 @EventHandler
	 public void setTarget(EntityDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		
		if ((e.getEntity() instanceof Player) && (e.getDamager() instanceof Player)) {
	         Player d = (Player)e.getDamager();
	 	     if (useAbility(d)) {
		         Player p = (Player)e.getEntity();
	             tp.put(d.getUniqueId(), p.getUniqueId());
	 	     }
		}
	 }

	 @EventHandler
	 public void teleport(PlayerToggleSneakEvent e) {
	     Player p = e.getPlayer();
	     if ((p.isSneaking()) && (useAbility(p))) {
	     	  if (tp.containsKey(p.getUniqueId())) {
				  if (!inCooldown(p)) {
	                  Player req = Bukkit.getPlayer(tp.get(p.getUniqueId()));
	                  if (req != null) {
	                	  if (!Gladiator.inGlad(req)) {
							  if (p.getLocation().distance(req.getLocation()) <= 45) {
		                 	      p.teleport(req);
		                 	      addCooldown(p, getCooldownSegundos());
							  } else {
								  p.sendMessage("§cO jogador está muito longe.");
							  } 
	                	  } else {
	                		  p.sendMessage("§cO jogador está em um Gladiator!");
	                	  }
	                 }
	     		  }
	     	  }
	     }
	 }
}