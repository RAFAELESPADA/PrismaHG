package com.br.gabrielsilva.prismamc.hungergames.manager.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.core.data.type.DataType;
import com.br.gabrielsilva.prismamc.commons.core.group.Groups;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;
import com.br.gabrielsilva.prismamc.hungergames.manager.gamer.Gamer;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class KitManager {
	
	@Getter @Setter
	private static HashMap<String, Kit> kits;
	
	@Getter @Setter
	private static ArrayList<String> kitsDesativados, kitsFree;
	
	@Getter @Setter
	private static boolean allKitsDesativados, allKitsFREE;
	
	static {
		kits = new HashMap<>();
		kitsDesativados = new ArrayList<>();
		kitsFree = new ArrayList<>();
		
		allKitsDesativados = false;
		allKitsFREE = false;
	}
	
	public static List<Kit> getAllKits() {
		List<Kit> allKits = new ArrayList<>();
		for (Kit kit : kits.values()) {
			 allKits.add(kit);
		 }
		 return allKits;
	}
	
	public static List<Kit> getPlayerKits(Player player) {
		List<Kit> playerKits = new ArrayList<>();
		for (Kit kit : kits.values()) {
			 if (player.hasPermission("kit." + kit.getNome().toLowerCase()) || player.hasPermission("kit.all")) {
				 playerKits.add(kit);
			 } else {
				 if (hasPermissionKit(player, kit.getNome(), false)) {
					 playerKits.add(kit);
				 }
			 }
		 }
		 return playerKits;
	}
	
	public static Kit getKitInfo(String nome) {
		return kits.get(nome.toLowerCase());
	}
	
	public static boolean hasPermissionKit(Player player, String kit, boolean msg) {
		if (allKitsFREE) {
			return true;
		}
		if (BukkitMain.getManager().getDataManager().getBukkitPlayer(
				player.getUniqueId()).getDataHandler().getData(DataType.GRUPO).getGrupo().getNivel() >= Groups.SAPPHIRE.getNivel()) {
			return true;
		}
		if (kit.equalsIgnoreCase("PvP")) {
			return true;
		}
		if (kit.equalsIgnoreCase("Grandpa")) {
			return true;
		}
		if (kit.equalsIgnoreCase("Kangaroo")) {
			return true;
		}
		if (kit.equalsIgnoreCase("Camel")) {
			return true;
		}
		if (kit.equalsIgnoreCase("Surprise")) {
			return true;
		}
		if (kit.equalsIgnoreCase("Stomper")) {
			return true;
		}
		if (kit.equalsIgnoreCase("Endermage")) {
			return true;
		}
		if (kit.equalsIgnoreCase("Cultivator")) {
			return true;
		}
		if (kit.equalsIgnoreCase("Fireman")) {
			return true;
		}
		if (kit.equalsIgnoreCase("Fisherman")) {
			return true;
		}
		if (kit.equalsIgnoreCase("Worm")) {
			return true;
		}
		if (kit.equalsIgnoreCase("Viper")) {
			return true;
		}
		if (kit.equalsIgnoreCase("Snail")) {
			return true;
		}
		
		if (player.hasPermission("kit.all")) {
			return true;
		}
		
		if (player.hasPermission("kit." + kit.toLowerCase())) {
			return true;
		}
		
		/**if (getKitsFree().contains(kit.toLowerCase())) {
			return true;
		}*/
		
		if (BukkitMain.getManager().getDataManager().getBukkitPlayer(
				player.getUniqueId()).getDataHandler().getData(DataType.GRUPO).getGrupo().getNivel() >= Groups.RUBY.getNivel()) {
			return true;
		}
		
		if (msg) {
			player.sendMessage("§cVocê não possue este Kit.");
		}
		
		return false;
	}
	
	public static void darItens(Player player) {
	    player.closeInventory();
		
		checkSurprise(player);
		
		giveBussola(player);
		
		darItensKit1(player);
		
		player.updateInventory();
	}
	
	public static void checkSurprise(Player player) {
		if (HungerGames.getManager().getGamer(player.getUniqueId()).getKit1().equalsIgnoreCase("Surprise")) {
			prepararSurprise(player);
		}
	}
	
	public static void giveBussola(Player player) {
		if (!player.getInventory().contains(Material.COMPASS)) {
			player.getInventory().addItem(new ItemBuilder().material(Material.COMPASS).name("§bB§ssola").build());
			player.updateInventory();
		}
	}
	
	public static void prepararSurprise(Player player) {
		List<Kit> kits = getAllKits();
		Gamer gamer = HungerGames.getManager().getGamer(player.getUniqueId());
		Kit randomKit = kits.get(new Random().nextInt(kits.size()));
		
		registerListenerKit(player, randomKit);
		
		gamer.setKit1(randomKit.getNome());
		player.sendMessage("§aO Surprise escolheu o kit: §7" + randomKit.getNome());
	}
	
	public static void darItensKit1(Player player) {
		Kit kit =
				getKitInfo(HungerGames.getManager().getGamer(player.getUniqueId()).getKit1().toLowerCase());
		
		if (kit != null) {
			if (kit.getItens() != null) {
				for (ItemStack item : kit.getItens()) {
					 player.getInventory().addItem(item);
				}
			}
		}
		player.updateInventory();
	}

    public static void registerListenerKit(Player player, Kit kit) {
    	removeKit(player);
    	kit.registerListener();
    }

    public static void removeKit(Player player) {
    	removeKit(player, false);
    }
    
    public static void removeKit(Player player, boolean setInGamer) {
    	Gamer gamer = HungerGames.getManager().getGamer(player.getUniqueId());
    	String playerKit = gamer.getKit1();
        Kit kit = getKitInfo(playerKit);
        
        if (kit != null) {
        	kit.unregisterPlayer(player);
        }
        
        if (setInGamer) {
        	gamer.setKit1("Nenhum");
        }
    }
    
    public static void removeKit(Player player, Kit kit) {
        if (kit != null) {
        	kit.unregisterPlayer(player);
        }
    }

	public static void handleKitSelect(Player player, String kitToSet) {
    	Gamer gamer = HungerGames.getManager().getGamer(player.getUniqueId());
    	Kit oldKit = getKitInfo(gamer.getKit1());
        
        if (oldKit != null) {
        	oldKit.unregisterPlayer(player);
        }
        
        Kit newKit = getKitInfo(kitToSet);
        if (newKit != null) {
        	newKit.registerListener();
        }
        
        gamer.setKit1(kitToSet);
	}
}