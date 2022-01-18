package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class LumberJack extends Kit {

	public LumberJack() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
			
		ItemStack icone = new ItemBuilder().
				material(Material.WOOD_AXE).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(5);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Use seu machado");
		indiob.add(ChatColor.GRAY + "E quebre arvores instanteneamente");
				setDescrição(indiob);
		
		setItens(new ItemBuilder().material(Material.WOOD_AXE).name(getItemColor() + "Kit " + getNome()).build());
	}
	
	@EventHandler
	public void Habilidade(BlockBreakEvent e) {
		if (e.isCancelled())
			return;
		
		if ((e.getPlayer().getItemInHand().getType().equals(Material.WOOD_AXE)) && (useAbility(e.getPlayer()))) {
			
			 Block b = e.getBlock().getRelative(BlockFace.UP), b1 = e.getBlock().getRelative(BlockFace.DOWN);
			 while (b.getType().name().contains("LOG")) {
					b.breakNaturally();
					b = b.getRelative(BlockFace.UP);
			 }
			 while (b1.getType().name().contains("LOG")) {
					b1.breakNaturally();
					b1 = b1.getRelative(BlockFace.DOWN);
			 }
		}
	}
}