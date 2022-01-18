package com.br.gabrielsilva.prismamc.hungergames.manager.structures;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.server.ItemChance;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.manager.structures.types.Feast;
import com.br.gabrielsilva.prismamc.hungergames.manager.structures.types.MiniFeast;

import lombok.Getter;

@Getter
public class StructureManager {

	public ArrayList<ItemChance> itens = new ArrayList<>(), 
			itensMinifeast = new ArrayList<>();
	
	private Feast feast;
	private MiniFeast miniFeast;
	
	public StructureManager() {
		this.feast = new Feast();
		this.miniFeast = new MiniFeast();
	}
	
	public Location getValidLocation() {
		int x = getCoord(400), 
				z = getCoord(400);
		
		World world = Bukkit.getWorld("world");
		Location loc = new Location(Bukkit.getWorld("world"), x, world.getHighestBlockYAt(x, z) + 2, z);
		
		boolean localValido = false;
		while (!localValido) {
			if (loc.getBlockY() >= 80 || loc.getBlockY() <= 55) {
				x = getCoord(400);
				z = getCoord(400);
				loc = new Location(Bukkit.getWorld("world"), x, world.getHighestBlockYAt(x, z) + 2, z);
			} else {
				localValido = true;
			}
		}
		return loc;
	}
	
	public void addItensChance() {
		for (String código : BukkitMain.getManager().getConfigManager().getBausConfig().getStringList("minifeast.itens")) {
			 String separador[] = código.split(",");
			 String material = separador[0].replace("Material:", "");
			 int quantidade = Integer.valueOf(separador[1].replace("Quantidade:", ""));
			 int durabilidade = Integer.valueOf(separador[2].replace("Durabilidade:", ""));
			 int chance = Integer.valueOf(separador[3].replace("Chance:", ""));
			 
			 Material m = null;
			 try {
				 m = Material.getMaterial(material);
			 } catch (NullPointerException e) {
				 HungerGames.console("Material invalido no Mini-Feast foi encontrado. -> " + material);
			 }
			 
			 if (m == null) 
				 continue;
			 
			 itensMinifeast.add(new ItemChance(new ItemBuilder().material(m).durability(durabilidade).build(), chance, (quantidade == 1 ? 0 : quantidade)));
			 
		}
		
		for (String código : BukkitMain.getManager().getConfigManager().getBausConfig().getStringList("feast.itens")) {
			 String separador[] = código.split(",");
			 String material = separador[0].replace("Material:", "");
			 int quantidade = Integer.valueOf(separador[1].replace("Quantidade:", ""));
			 int durabilidade = Integer.valueOf(separador[2].replace("Durabilidade:", ""));
			 int chance = Integer.valueOf(separador[3].replace("Chance:", ""));
			 
			 Material m = null;
			 try {
				 m = Material.getMaterial(material);
			 } catch (NullPointerException e) {
				 HungerGames.console("Material invalido no Feast foi encontrado. -> " + material);
			 }
			 
			 if (m == null) 
				 continue;
			 
			 itens.add(new ItemChance(new ItemBuilder().material(m).durability(durabilidade).build(), chance, (quantidade == 1 ? 0 : quantidade)));
		}
	}
	
	public void addChestItems(Chest chest) {	
		for (int i = 0; i < itens.size(); i++) {
			 if (new Random().nextInt(100) < itens.get(i).getChance()) {
				 int slot = new Random().nextInt(chest.getBlockInventory().getSize());
				 chest.getBlockInventory().setItem(slot, itens.get(i).getItem());
			 }
		}
		chest.update();
	}
	
	public int getCoord(int range) {
		return new Random().nextBoolean() ? new Random().nextInt(range - 13) : -new Random().nextInt(range + 17);
	}
	
	public void addChestItemsMinifeast(Chest chest) {	
		for (int i = 0; i < itensMinifeast.size(); i++)
			 if (new Random().nextInt(100) < itensMinifeast.get(i).getChance()) {
				 int slot = new Random().nextInt(chest.getBlockInventory().getSize());
				 chest.getBlockInventory().setItem(slot, itensMinifeast.get(i).getItem());
			 }
		chest.update();
	}
}