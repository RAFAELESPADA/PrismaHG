package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Fisherman extends Kit {

	public Fisherman() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
		
		ItemStack icone = new ItemBuilder().
				material(Material.FISHING_ROD).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(60);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Pesque players");
				setDescrição(indiob);
		
		setItens(new ItemBuilder().material(Material.FISHING_ROD).name(
				getItemColor() + "Kit " + getNome()).inquebravel().build());
	}
	
	@EventHandler
	public void pescar(PlayerFishEvent e) {
		if ((e.getPlayer() instanceof Player) && (e.getCaught() instanceof Player)) {
			 Player p = e.getPlayer();
			 if (useAbility(p)) {
				 Player d = (Player) e.getCaught();
				 d.teleport(p.getLocation());
			 }
		}
	}
}