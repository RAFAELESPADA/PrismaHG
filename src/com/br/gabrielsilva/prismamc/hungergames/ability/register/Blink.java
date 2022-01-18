package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Blink extends Kit {

	public Blink() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
			
		
	ItemStack icone = new ItemBuilder().
			material(Material.NETHER_STAR).
			durability(0).
			amount(1)
			.build();
			
			setIcone(icone);
			
			setPreço(3000);
			setCooldownSegundos(0);
	        ArrayList indiob = new ArrayList();
	/* 351 */         indiob.add(ChatColor.GRAY + "Teleporte com sua nether star");
			setDescrição(indiob);
		
		setItens(new ItemBuilder().material(Material.NETHER_STAR).name(getItemColor() + "Kit " + getNome()).build());
		
	}
	
	public HashMap<UUID, Integer> blink = new HashMap<>();
	
	@EventHandler
	public void skill(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if ((p.getItemInHand().getType().equals(Material.NETHER_STAR)) && (useAbility(p))) {
			if (inCooldown(p)) {
				sendMessageCooldown(p);
				return;
			}
			blink.put(p.getUniqueId(), blink.containsKey(p.getUniqueId()) ? blink.get(p.getUniqueId()) + 1 : 1); 
			
			if (blink.get(p.getUniqueId()).equals(4)) {
				blink.remove(p.getUniqueId());
				addCooldown(p, getCooldownSegundos());
			}
			
			Block b = e.getPlayer().getTargetBlock((HashSet<Byte>) null, 10);
			if (b.getType().equals(Material.AIR)) {
				b.setType(Material.LEAVES);
				Vector v = p.getEyeLocation().getDirection();
				p.teleport(b.getLocation().add(0, 2, 0).setDirection(v));
				p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
			 }
		}
	}
}