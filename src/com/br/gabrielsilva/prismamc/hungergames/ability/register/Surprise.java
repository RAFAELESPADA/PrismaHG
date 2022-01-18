package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Surprise extends Kit {

	public Surprise() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
		
		ItemStack icone = new ItemBuilder().
				material(Material.CAKE).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(5);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Começe com um kit aleatorio!");
		indiob.add(ChatColor.GRAY + "Mesmo aqueles que voce nao possui.");
				setDescrição(indiob);
		setItens(new ItemStack(Material.AIR));
	}
}