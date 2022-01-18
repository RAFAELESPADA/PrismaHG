package com.br.gabrielsilva.prismamc.hungergames;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.commands.BukkitCommandFramework;
import com.br.gabrielsilva.prismamc.commons.core.Core;
import com.br.gabrielsilva.prismamc.commons.core.data.type.DataType;
import com.br.gabrielsilva.prismamc.commons.core.group.Groups;
import com.br.gabrielsilva.prismamc.commons.core.server.ServerType;
import com.br.gabrielsilva.prismamc.commons.core.server.types.Stages;
import com.br.gabrielsilva.prismamc.commons.core.utils.loaders.CommandLoader;
import com.br.gabrielsilva.prismamc.commons.core.utils.loaders.ListenerLoader;
import com.br.gabrielsilva.prismamc.hungergames.manager.Manager;
import com.br.gabrielsilva.prismamc.hungergames.manager.hologram.HologramManager;
import com.br.gabrielsilva.prismamc.hungergames.manager.kit.KitLoader;

import lombok.Getter;
import lombok.Setter;

public class HungerGames extends JavaPlugin {

	@Getter @Setter
	private static HungerGames instance;
	
	@Getter @Setter
	private static Manager manager;
	
	public void onLoad() {
		setInstance(this);
		
		setManager(new Manager());
		getManager().getGameManager().updateHGStats();
		Bukkit.getConsoleSender().sendMessage("Deletando mundo...");
			Bukkit.getServer().unloadWorld("world", false);
			deleteDir(new File("world"));
			Bukkit.getConsoleSender().sendMessage("Mundo deletado");
		super.onLoad();
	}


		public static void deleteDir(File dir) {
			if (dir.isDirectory()) {
				String[] children = dir.list();
				for (int i = 0; i < children.length; i++) {
					deleteDir(new File(dir, children[i]));
				}
			}
			dir.delete();
		}
		
	
	
	public void onEnable() {
		if (!Core.correctlyStarted()) {
			Bukkit.shutdown();
			return;
		}
		saveDefaultConfig();
		
		KitLoader.init();
		getManager().getGameManager().prepararLocations();
		
		BukkitMain.getManager().getConfigManager().loadBaus();
		getManager().getStructureManager().addItensChance();
		
		ListenerLoader.loadListenersBukkit(getInstance(), "com.br.gabrielsilva.prismamc.hungergames.listeners");
		
		new CommandLoader(new BukkitCommandFramework(getInstance())).
		loadCommandsFromPackage("com.br.gabrielsilva.prismamc.hungergames.commands");
		if (BukkitMain.getServerType() != ServerType.EVENTO) {
			BukkitMain.getManager().enableHologram();
			BukkitMain.getInstance().injectCustomKnockback();
			HungerGames.getManager().getGameManager().spawnar("coliseu", new Location(Bukkit.getWorld("world"), 0, 150, 0), true);
			Bukkit.getWorld("world").setSpawnLocation(0, 155, 0);
		} else {
			Bukkit.getWorld("world").setSpawnLocation(0, Bukkit.getWorld("world").getHighestBlockYAt(0, 0) + 1, 0);
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), new Runnable() {
			public void run() {
				World world = getServer().getWorld("world");
	
				//for (int x = -5; x <= 5; x++)
				//	for (int z = -5; z <= 5; z++)
				//		world.getSpawnLocation().clone().add(x * 16, 0, z * 16).getChunk().load();
				
				world.setDifficulty(Difficulty.NORMAL);
				if (world.hasStorm())
					world.setStorm(false);
				world.setWeatherDuration(999999999);
				world.setGameRuleValue("doDaylightCycle", "false");
				org.bukkit.WorldBorder border = world.getWorldBorder();
				border.setCenter(0, 0);
				border.setSize(1000);
				for (Entity e : world.getEntities()) {
					e.remove();
				}
			}
		});

		HungerGames.getManager().getGameManager().setEstagio(Stages.PREJOGO);
		HungerGames.getManager().getGameManager().updateHGStats();
		
		getManager().getTimerManager().init();
		getManager().getScoreboardManager().init();
		getManager().getGameManager().init();
		
		HologramManager.init();
	}
	
	public void onDisable() {
		getManager().getGameManager().setEstagio(Stages.OFFLINE);
		getManager().getGameManager().updateHGStats();
	}
	public static boolean isVip(Player player) {
		if (BukkitMain.getManager().getDataManager().getBukkitPlayer(
				player.getUniqueId()).getDataHandler().getData(DataType.GRUPO).getGrupo().getNivel() >= Groups.SAPPHIRE.getNivel()) {
			return true;
		}
		 return false;
	}

	public static void console(String msg) {
		Bukkit.getConsoleSender().sendMessage("[HungerGames] " + msg);
	}
	
	public static void runAsync(Runnable runnable) {
		Bukkit.getScheduler().runTaskAsynchronously(getInstance(), runnable);	
	}
	
	public static void runLater(Runnable runnable) {
		runLater(runnable, 5);
	}
	
	public static void runLater(Runnable runnable, long ticks) {
		Bukkit.getScheduler().runTaskLater(getInstance(), runnable, ticks);	
	}
}