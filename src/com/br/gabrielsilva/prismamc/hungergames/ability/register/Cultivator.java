package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.CocoaPlant;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Cultivator extends Kit {

	public Cultivator() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
		
		ItemStack icone = new ItemBuilder().
				material(Material.SAPLING).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(0);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Plante mudas instantaneamente.");
				setDescrição(indiob);
		setItens(new ItemStack(Material.AIR));
	}
	
	@EventHandler
	public void place(BlockPlaceEvent e) {
		if (e.getBlock().getType() == Material.SAPLING && useAbility(e.getPlayer())) {
			e.getBlock().setType(Material.AIR);
			e.getBlock().getWorld().generateTree(e.getBlock().getLocation(), TreeType.TREE);
		} else if (e.getBlock().getType() == Material.CROPS && useAbility(e.getPlayer())) {
			e.getBlock().setData((byte) 7);
		} else if (e.getBlock().getType() == Material.COCOA && useAbility(e.getPlayer())) {
			CocoaPlant bean = (CocoaPlant)e.getBlock().getState().getData();
            if (bean.getSize() != CocoaPlant.CocoaPlantSize.LARGE) {
                bean.setSize(CocoaPlant.CocoaPlantSize.LARGE);
                e.getBlock().setData(bean.getData());
            }
		}
	}
}
