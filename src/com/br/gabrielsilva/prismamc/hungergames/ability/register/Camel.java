package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Camel extends Kit {

	public Camel() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
			
		
	ItemStack icone = new ItemBuilder().
			material(Material.SAND).
			durability(0).
			amount(1)
			.build();
			
			setIcone(icone);
			
			setPreço(3000);
			setCooldownSegundos(0);
	        ArrayList indiob = new ArrayList();
	/* 351 */         indiob.add(ChatColor.GRAY + "Ganhe efeitos no deserto");
			setDescrição(indiob);
		setItens(new ItemStack(Material.AIR));
	}
	
	@EventHandler
	public void Habilidade(PlayerMoveEvent e) {
		if (e.isCancelled())
			return;
		
		Player p = e.getPlayer();
		if ((e.getTo().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SAND) || e.getTo().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SANDSTONE)) && (useAbility(p))) {
			 p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1));
		}
	}
}