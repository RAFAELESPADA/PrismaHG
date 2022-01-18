package com.br.gabrielsilva.prismamc.hungergames.manager.structures.types;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.br.gabrielsilva.prismamc.hungergames.HungerGames;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MiniFeast {

	private boolean useMinifeast;
	
	public MiniFeast() {
		this.useMinifeast = true;
	}
	
	public void createMinifeast() {
		final Location loc = HungerGames.getManager().getStructureManager().getValidLocation();
		
		HungerGames.getManager().getGameManager().spawnar("minifeast", loc, false);
		
		Random random = new Random();
		int reduceX = random.nextInt(60) + 1, reduceZ = random.nextInt(40) + 1;
		int upX = random.nextInt(40) + 1, upZ = random.nextInt(70) + 1;
		
		Bukkit.broadcastMessage("§eUm minifeast spawnou entre: (x " + (loc.getBlockX() - upZ) + ", z " + (loc.getBlockZ() - reduceX) + " e" + 
		" x " + (loc.getBlockX() - upX) + ", z " + (loc.getBlockZ() + reduceZ) + ").");
		
		HungerGames.console("MiniFeast spawnou em -> " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
	}
}