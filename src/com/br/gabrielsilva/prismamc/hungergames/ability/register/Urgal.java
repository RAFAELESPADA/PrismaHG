package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Urgal extends Kit {

	public Urgal() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
		
		ItemStack icone = new ItemBuilder().
				material(Material.POTION).
				durability(8201).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(5);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Ganhe tres");
		indiob.add(ChatColor.GRAY + "poções de força.");
				setDescrição(indiob);
		setItens(new ItemBuilder().material(Material.POTION).name(getItemColor() + "Kit " + getNome()).durability(8201).amount(3).build());
	}
}