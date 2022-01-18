package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Demoman extends Kit {

	public Demoman() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
		
		ItemStack icone = new ItemBuilder().
				material(Material.STONE_PLATE).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(0);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Crie armadilhas com gravel e placa de pressão");
				setDescrição(indiob);
		
		setItens(new ItemBuilder().material(Material.GRAVEL).amount(8).name(getItemColor() + "Kit " + getNome()).build(),
		new ItemBuilder().material(Material.STONE_PLATE).name(getItemColor() + "Kit " + getNome()).amount(8).build());
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (e.isCancelled())
			return;
		
		if (e.getBlock().getType().name().contains("PLATE") && useAbility(e.getPlayer())) {
			e.getBlock().setMetadata("demoman", new FixedMetadataValue(HungerGames.getInstance(), e.getPlayer()));
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.isCancelled())
			return;
		
		if (!HungerGames.getManager().getGamer(e.getPlayer().getUniqueId()).isJogando()) {
			return;
		}
		
		if (e.getAction() == Action.PHYSICAL && e.getClickedBlock() != null && e.getClickedBlock().hasMetadata("demoman")
			&& e.getClickedBlock().getRelative(BlockFace.DOWN).getType() == Material.GRAVEL) {
			e.getPlayer().getWorld().createExplosion(e.getClickedBlock().getLocation(), 4.0F);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getCause().toString().contains("EXPLOSION") && event.getEntity() instanceof Player && 
				containsHability((Player) event.getEntity())) {
			double damage = event.getDamage();
			double porcent = 50.0D;
			if (((Player) event.getEntity()).isBlocking()) {
				 porcent = 75.0D;
			}
			event.setDamage(event.getDamage() - ((damage / 100) * porcent));
		}
	}
}