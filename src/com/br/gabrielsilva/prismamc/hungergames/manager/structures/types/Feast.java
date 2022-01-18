package com.br.gabrielsilva.prismamc.hungergames.manager.structures.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.api.schematic.SchematicAPI;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Feast {
	
	private boolean useFeast, spawned;
	private Location feastLocation;
	
	public Feast() {
		this.useFeast = true;
		this.feastLocation = null;
		this.spawned = false;
	}
	
	public void createFeast() {
		final Location loc = HungerGames.getManager().getStructureManager().getValidLocation();
		this.feastLocation = loc;
		
		HungerGames.getManager().getGameManager().spawnar("feast", this.feastLocation, false);
		
		final int x = loc.getBlockX(), 
				y = loc.getBlockY(), 
				z = loc.getBlockZ();
		
		new BukkitRunnable() {
			int segundos = 300;
			public void run() {
				if (!isUseFeast()) {
					cancel();
					Bukkit.broadcastMessage("§aO feast foi cancelado!");
					return;
				}
				if (segundos == 300 || segundos == 240 || segundos == 180 || segundos == 120) {
					Bukkit.broadcastMessage("§aO feast irá spawnar em §f" + segundos / 60 + " §aminutos. nas coordenadas " + x + ", " + y + ", " + z);
				} else if (segundos == 60) {
					Bukkit.broadcastMessage("§aO feast irá spawnar em §f" + segundos / 60 + "§a minuto. nas coordenadas " + x + ", " + y + ", " + z);
				} else if (segundos == 30 || segundos == 15 || segundos == 10) {
					Bukkit.broadcastMessage("§aO feast irá spawnar em §f" + segundos + "§a segundos. nas coordenadas " + x + ", " + y + ", " + z);
				} else if (segundos > 1 && segundos <= 5) {
					Bukkit.broadcastMessage("§aO feast irá spawnar em §f" + segundos + " §asegundos. nas coordenadas " + x + ", " + y + ", " + z);
				} else if (segundos == 1) {
					Bukkit.broadcastMessage("§aO feast irá spawnar em §f" + segundos + " §asegundo. nas coordenadas " + x + ", " + y + ", " + z);
				} else if (segundos == 0) {
					fillChests(loc);
					Bukkit.broadcastMessage("§aO feast spawnou.");
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.playSound(p.getLocation(), Sound.GHAST_SCREAM, 10.0f, 10.0f);
				}
					setSpawned(true);
				}

				if (segundos > 0)
					segundos--;
				else
					cancel();
			}
		}.runTaskTimer(HungerGames.getInstance(), 20, 20);
	}
	
	public void fillChests(Location loc) {
		if (loc == null) {
			return;
		}
		
		for (Block baus : SchematicAPI.Baus) {
			 baus.setType(Material.CHEST);
		}
		
		for (Block enchant : SchematicAPI.Enchant) {
			 enchant.setType(Material.ENCHANTMENT_TABLE);
		}
		
		HungerGames.runLater(() -> {
			for (Block baus : SchematicAPI.Baus) {
				 HungerGames.getManager().getStructureManager().addChestItems((Chest) baus.getLocation().getBlock().getState());
			}
		}, 5);
	}
}