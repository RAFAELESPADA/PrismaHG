package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class JackHammer extends Kit {

	public JackHammer() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
			
		ItemStack icone = new ItemBuilder().
				material(Material.IRON_AXE).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(60);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Crie um buraco");
		indiob.add(ChatColor.GRAY + "Ate a bedrock");
				setDescrição(indiob);
		
		setItens(new ItemBuilder().material(Material.STONE_AXE).name(getItemColor() + "Kit " + getNome()).build());
	}
	
	HashMap<UUID, Integer> blocos = new HashMap<>();
	
	@EventHandler
	public void Habilidade(BlockBreakEvent e) {
		if ((e.getPlayer().getItemInHand().getType().equals(Material.STONE_AXE)) && (useAbility(e.getPlayer()))) {
			if (inCooldown(e.getPlayer())) {
			    sendMessageCooldown(e.getPlayer());
			    return;
			}
			blocos.put(e.getPlayer().getUniqueId(), blocos.containsKey(e.getPlayer().getUniqueId()) ? blocos.get(e.getPlayer().getUniqueId()) + 1 : 1);
			if (blocos.get(e.getPlayer().getUniqueId()).equals(4)) {
				addCooldown(e.getPlayer(), getCooldownSegundos());
				blocos.remove(e.getPlayer().getUniqueId());
				return;
			}
			if (e.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
			    quebrar(e.getBlock(), BlockFace.UP);
			} else {
				quebrar(e.getBlock(), BlockFace.DOWN);
			}
		}
	}
	
	void quebrar(final Block b, final BlockFace face) {
		new BukkitRunnable() {
		Block block = b;
		public void run() {
			if (block.getType() != Material.BEDROCK && block.getType() != Material.ENDER_PORTAL_FRAME && block.getY() <= 128) {
				block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType().getId(), 30);
				block.setType(Material.AIR);
				block = block.getRelative(face);
			} else {
				cancel();
			}
		}
		}.runTaskTimer(HungerGames.getInstance(), 2L, 2L);
	}
}