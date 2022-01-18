package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Archer extends Kit {

	public Archer() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
			
		ItemStack icone = new ItemBuilder().
				material(Material.BOW).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(0);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Ganhe arco e 25 flechas");
				setDescrição(indiob);
		
		setItens(new ItemBuilder().material(Material.BOW).name(getItemColor() + "Kit " + getNome()).
				addEnchant(Enchantment.ARROW_DAMAGE).addEnchant(Enchantment.ARROW_KNOCKBACK, 2).build(), 
		new ItemBuilder().material(Material.ARROW).amount(25).build());
	}
}
