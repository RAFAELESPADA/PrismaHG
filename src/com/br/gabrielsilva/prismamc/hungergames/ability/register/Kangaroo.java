package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Kangaroo extends Kit {

	public Kangaroo() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
		
		ItemStack icone = new ItemBuilder().
				material(Material.FIREWORK).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(5);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Use seu firework");
		indiob.add(ChatColor.GRAY + "Para dar pulos duplos");
				setDescrição(indiob);
		
		setItens(new ItemBuilder().material(Material.FIREWORK).name(
				getItemColor() + "Kit " + getNome()).build());
	}
	
	ArrayList<Player> Kanga = new ArrayList<>();
	
	@EventHandler
	public void Habilidade(PlayerInteractEvent e) {
		if ((!e.getAction().equals(Action.PHYSICAL)) && (e.getPlayer().getItemInHand().getType().equals(Material.FIREWORK)) && 
				(useAbility(e.getPlayer()))) {
			
		 	 e.setCancelled(true);
			 Player p = e.getPlayer();
			 
			 if (inCooldown(p, "CombatLog")) {
				 p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3, 5), true);
				 p.sendMessage("§cVocê levou um hit recentemente, aguarde para usar a habilidade novamente.");
	 	    	 return;
			 }
	 	     
	 	     //DamageListener.addBypassVelocity(p);
	 	     
	 	     if (!Kanga.contains(p)) {
				  if (!p.isSneaking()) {
					  if (!p.isOnGround()) {
						   p.setVelocity(new Vector(p.getVelocity().getX(), 1.0D, p.getVelocity().getZ())); 
						   Kanga.add(p);
					  } else if (p.isOnGround()) {
						   p.setVelocity(new Vector(p.getVelocity().getX(), 0.9D, p.getVelocity().getZ()));
					  }
				  } else if (p.isSneaking()) {
					  if (!p.isOnGround()) {
						   p.setVelocity(p.getLocation().getDirection().multiply(1.75D));
						   p.setVelocity(new Vector(p.getVelocity().getX(), 0.5D, p.getVelocity().getZ()));
						   Kanga.add(p);
					  } else if (p.isOnGround()) {
						  p.setVelocity(p.getLocation().getDirection().multiply(1.35D));
						  p.setVelocity(new Vector(p.getVelocity().getX(), 0.6D, p.getVelocity().getZ()));
					  }
				  }
			  }
		}
	}
	
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            final Player player = (Player) event.getEntity();
            
            if (containsHability(player)) {
            	addCooldown(player, "CombatLog", 3);
            }
        }
    }
	
	@EventHandler
	public void removeOnMove(PlayerMoveEvent e) {
		if (Kanga.contains(e.getPlayer()) &&
				(e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR || e.getPlayer().isOnGround()))
			Kanga.remove(e.getPlayer());
	}
	
    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && containsHability(player)) {
                double damage = event.getDamage();
                if (damage > 7.0D) {
                    event.setDamage(7.0D);
                } else {
                	event.setCancelled(true);
                }
            }
        }
    }
}