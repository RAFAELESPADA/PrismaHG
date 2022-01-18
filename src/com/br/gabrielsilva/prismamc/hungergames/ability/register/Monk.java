package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;
import java.util.Random;
import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Monk extends Kit {

	public Monk() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
		ItemStack icone = new ItemBuilder().
				material(Material.BLAZE_ROD).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(5);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Embaralhe o inventario do seu");
		indiob.add(ChatColor.GRAY + "Inimigo com sua vara.");
				setDescrição(indiob);
		
		setItens(new ItemBuilder().material(Material.BLAZE_ROD).name(
				getItemColor() + "Kit " + getNome()).build());
	}
	
	@EventHandler
	public void Habilidade(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Player) {
			Player p = e.getPlayer();
			if ((p.getItemInHand().getType().equals(Material.BLAZE_ROD)) && (useAbility(p))) {
				 if (inCooldown(p)) {
					 sendMessageCooldown(p);
					 return;
				 }
				 Player d = (Player) e.getRightClicked();
				 final ItemStack item = d.getItemInHand();
				 final int r = new Random().nextInt(35);
				 final ItemStack i = d.getInventory().getItem(r);
				 d.getInventory().setItem(r, item);
				 d.setItemInHand(i);
				 addCooldown(p, getCooldownSegundos());
				 p.sendMessage(ChatColor.YELLOW + "Monkado!");
				 d.sendMessage(ChatColor.GREEN + "Você foi monkado!");
			}
		}
	}
}