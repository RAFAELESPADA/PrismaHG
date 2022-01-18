package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Miner extends Kit {

	public Miner() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
		
		ItemStack icone = new ItemBuilder().
				material(Material.STONE_PICKAXE).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(5);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Use sua picareta para");
		indiob.add(ChatColor.GRAY + "Quebrar minerios conectados");
				setDescrição(indiob);
		setItens(new ItemBuilder().material(Material.STONE_PICKAXE).inquebravel().name(getItemColor() + "Kit " + getNome()).addEnchant(Enchantment.DIG_SPEED).addUnsafeEnchant(Enchantment.DURABILITY, 5).build());
	}
	
	@EventHandler
	public void Habilidade(BlockBreakEvent e) {
		if (e.isCancelled())
			return;
		
		if ((e.getBlock().getType().equals(Material.IRON_ORE)) && (useAbility(e.getPlayer()))) {
			
			 for (int x = -3; x < 3; x++) {
			 for (int z = -3; z < 3; z++) {
			 for (int y = -3; y < 3; y++) {
			      Block b = e.getBlock().getLocation().clone().add(x, y, z).getBlock();
			      if (b.getType().equals(Material.IRON_ORE)) {
			    	  b.breakNaturally();
				      if (new Random().nextInt(100) <= 10)
				          e.getPlayer().giveExp(1);
			      }
			 }
			 }
			 }
		} else if ((e.getBlock().getType().equals(Material.COAL_ORE)) && (useAbility(e.getPlayer()))) {
			for (int x = -3; x < 3; x++) {
			for (int z = -3; z < 3; z++) {
			for (int y = -3; y < 3; y++) {
			     Block b = e.getBlock().getLocation().clone().add(x, y, z).getBlock();
			     if (b.getType().equals(Material.COAL_ORE)) {
				     b.breakNaturally();
				     if (new Random().nextInt(100) <= 10)
				         e.getPlayer().giveExp(1);
			     }
			}
			}
			}
		}
	}
}
