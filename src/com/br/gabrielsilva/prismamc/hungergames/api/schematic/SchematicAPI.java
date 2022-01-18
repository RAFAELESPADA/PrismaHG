package com.br.gabrielsilva.prismamc.hungergames.api.schematic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.br.gabrielsilva.prismamc.hungergames.HungerGames;

import lombok.Getter;

public class SchematicAPI {
	
    @Getter
	public static ArrayList<Block> Circulo = new ArrayList<Block>(), Baus = new ArrayList<Block>(), 
			Enchant = new ArrayList<Block>(), bFeast = new ArrayList<Block>(), blocksMiniFeast = new ArrayList<Block>(),
			bausForced = new ArrayList<Block>(), portaoColiseu = new ArrayList<>();
	
	public static void spawnarSchematic(String tipo, File file, World world, Location loc, boolean force) {
		SchematicLoader schematic = null;
		
		HungerGames.console("Tentando carregar a schematic '"+tipo+"'...");
		
		try {
			schematic = new SchematicLoader(tipo, file);
			
			schematic.paste(tipo, loc, getBlockSpeed(tipo), force);
		} catch (IOException e) {
			HungerGames.console("Ocorreu um erro ao tentar carregar a schematic '"+tipo+"' -> " + e.getLocalizedMessage());
		}
	}
	
	private static int getBlockSpeed(String type) {
		if (type.equalsIgnoreCase("feast")) {
			return 200;
		} else if (type.equalsIgnoreCase("minifeast")) {
			return 150;
		} else if (type.equalsIgnoreCase("coliseu")) {
			return 800;
		} else {
			return 100;
		}
	}
}