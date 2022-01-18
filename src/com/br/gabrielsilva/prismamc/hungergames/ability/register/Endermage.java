package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.VanishManager;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;
import com.br.gabrielsilva.prismamc.hungergames.manager.gamer.Gamer;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Endermage extends Kit {

	public Endermage() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
		
		ItemStack icone = new ItemBuilder().
				material(Material.NETHER_BRICK_ITEM).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(3000);
				setCooldownSegundos(60);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Puxe inimigos ate você com seu portal");
				setDescrição(indiob);
		setItens(new ItemBuilder().material(Material.NETHER_BRICK_ITEM).name(getItemColor() + "Kit " + getNome()).build());
	}
	
	@EventHandler
	public void Habilidade(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
		    if ((e.getPlayer().getItemInHand().getType().equals(Material.NETHER_BRICK_ITEM)) && 
		    		(useAbility(e.getPlayer()))) {
		    	
				 if (e.getClickedBlock().getType() != Material.ENDER_PORTAL_FRAME) {
					 Player p = e.getPlayer();
					 if (inCooldown(p)) {
						 sendMessageCooldown(p);
						 return;
					 }
					 final Block b = e.getClickedBlock();
					 final BlockState bs = b.getState();
					 if (b.getType().equals(Material.BEDROCK)) {
					     p.sendMessage("§cVocê não pode puxar neste bloco.");
						 return;
					 }
					 if (p.getLocation().getBlockY() > 128) {
					     p.sendMessage("§cVocê não pode puxar nesta altura.");
						 return;
					 }
					 b.setType(Material.ENDER_PORTAL_FRAME);
					 addCooldown(p, getCooldownSegundos());
					 
					 new BukkitRunnable() {
						 int segundos = 5;
						 Location portal2 = b.getLocation().clone().add(0.5, 1.5, 0.5);
						 
						 boolean reseted = false;
						 
						 public void run() {
					    	 ArrayList<Player> players = getNearbyPlayers(p, portal2);
					    	 
						     if (!p.isOnline() || calculateDistance(p.getLocation(), portal2) > 50) {
							     if (!p.isOnline()) {
								     cancel();
							     }
								 if (!reseted) {
									 reseted = true;
									 resetBlock(b, bs);
								 }
						     }
						     
							 if (!players.isEmpty()) {
								 for (Player pl : players) {
									  pl.setFallDistance(0);
									  pl.setNoDamageTicks(110);
									  pl.teleport(portal2);
									  p.sendMessage("§bVocê puxou: " + pl.getName());
									  pl.sendMessage("§bVocê foi puxado pelo " + p.getName());
									  pl.sendMessage("§bVocê esta invencivel por 5 segundos");
								 }
								 p.setFallDistance(0);
								 p.setNoDamageTicks(110);
								 p.teleport(portal2);
								 p.sendMessage("§bVocê esta invencivel por 5 segundos");
								 if (!reseted) {
									 reseted = true;
									 resetBlock(b, bs);
								 }
								 cancel();
							 }
							 
							 if (segundos == 0) {
								 if (!reseted) {
									 reseted = true;
									 resetBlock(b, bs);
								 }
								 cancel();
							 }
							 segundos--;
					     }
				         }.runTaskTimer(HungerGames.getInstance(), 1L, 20L);
				     }
				 }
		}
	}
	
	private void resetBlock(Block b, BlockState bs) {
		HungerGames.runLater(() -> {
			 b.setType(bs.getType());
		     b.setData(bs.getBlock().getData());
		}, 60);
	}
	
	private ArrayList<Player> getNearbyPlayers(Player p2, Location portal) {
		ArrayList<Player> players = new ArrayList<Player>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			 Gamer gamer = HungerGames.getManager().getGamer(p.getUniqueId());
			 if ((p.equals(p2)) || (!isEnderable(portal, p.getLocation())) || (containsHability(p))
			 || (!gamer.isJogando()) || (VanishManager.isInvisivel(p)) || (p.getLocation().getBlockY() > 128))
				 continue;
			 
			players.add(p);
		}
		return players;
	}

	private boolean isEnderable(Location portal, Location player) {
		return (Math.abs(portal.getX() - player.getX()) < 2.0D) && (Math.abs(portal.getZ() - player.getZ()) < 2.0D)
		&& (Math.abs(portal.getY() - player.getY()) > 2.0D);
	}
	
	public int calculateDistance(Location a, Location b) {
		int distance = 0, x1 = a.getBlockX(), x2 = b.getBlockX(), z1 = a.getBlockZ(), z2 = b.getBlockZ();
		if (x1 != x2) {
		if (x1 < x2) {
			for (int i = x1; i <= x2 - 1; i++)
				 distance++;
		} else {
			for (int i = x2; i <= x1 - 1; i++)
				 distance++;
		}
		}
		if (z1 != z2) {
		if (z1 < z2) {
			for (int i = z1; i <= z2 - 1; i++) 
				 distance++;
		} else {
			for (int i = z2; i <= z1 - 1; i++) 
				distance++;	
		}
		}
		return distance;
	}
}