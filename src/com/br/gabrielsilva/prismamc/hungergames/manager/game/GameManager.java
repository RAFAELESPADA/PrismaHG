package com.br.gabrielsilva.prismamc.hungergames.manager.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.VanishManager;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.server.ServerAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.server.ServerOptions;
import com.br.gabrielsilva.prismamc.commons.bukkit.manager.config.PluginConfig;
import com.br.gabrielsilva.prismamc.commons.bukkit.utils.BungeeUtils;
import com.br.gabrielsilva.prismamc.commons.core.Core;
import com.br.gabrielsilva.prismamc.commons.core.data.DataHandler;
import com.br.gabrielsilva.prismamc.commons.core.data.category.DataCategory;
import com.br.gabrielsilva.prismamc.commons.core.data.type.DataType;
import com.br.gabrielsilva.prismamc.commons.core.group.Groups;
import com.br.gabrielsilva.prismamc.commons.core.server.ServerType;
import com.br.gabrielsilva.prismamc.commons.core.server.types.Stages;
import com.br.gabrielsilva.prismamc.commons.core.utils.system.MachineOS;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.api.schematic.SchematicAPI;
import com.br.gabrielsilva.prismamc.hungergames.manager.gamer.Gamer;
import com.br.gabrielsilva.prismamc.hungergames.manager.kit.KitManager;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;

@Getter @Setter
public class GameManager {

	private Player winner;
	private Stages estagio;
	private List<Location> locaisToRespawn;
	private int invenciblityTime;
	
	private Location spawn;
	
	public GameManager() {
		this.winner = null;
		this.estagio = Stages.CARREGANDO;
		this.locaisToRespawn = new ArrayList<>();
		this.invenciblityTime = 120;
	}
	
	public void init() {
		PluginConfig.createLoc(HungerGames.getInstance(), "spawn");
		setSpawn(PluginConfig.getNewLoc(HungerGames.getInstance(), "spawn"));
	}
	
	public void startGame() {
		World world = Bukkit.getWorlds().get(0);
		world.setTime(0);
		world.setStorm(false);
		world.setThundering(false);
		
		if (BukkitMain.getServerType() != ServerType.EVENTO) {
			for (Block blocks : SchematicAPI.portaoColiseu) {
				 blocks.setType(Material.AIR);
			}
		}
		
		for (Player gamers : getAliveGamers()) {
			 prepararGamer(gamers);
		}
		
		HungerGames.getManager().getTimerManager().setTempo(getInvenciblityTime());
		
		setEstagio(Stages.INVENCIBILIDADE);
		Bukkit.broadcastMessage("§aA partida foi iniciada com " + getGamersVivos().size() + " jogadores.");
		
		for (Player ons : Bukkit.getOnlinePlayers()) {
			 HungerGames.getManager().getScoreboardManager().createSideBar(ons);
		}
		
	    world.playSound(world.getSpawnLocation(), Sound.AMBIENCE_THUNDER, 4.0F, 4.0F);
		removeInvalidGamers();
		
		ServerAPI.registerAntiAbuser();
	}
	
	private void removeInvalidGamers() {
		List<UUID> toRemove = new ArrayList<>();
		
		for (UUID uuids : HungerGames.getManager().getGamers().keySet()) {
			 Player target = Bukkit.getPlayer(uuids);
			 if (target == null) {
				 toRemove.add(uuids);
			 }
		}
		
		if (toRemove.size() != 0) {
			for (UUID uuids : toRemove) {
				 HungerGames.getManager().removeGamer(uuids);
			}
			HungerGames.console("Foram removidos -> " + toRemove.size() + " Gamers offlines");
			toRemove.clear();
		}
		toRemove = null;
	}
	
	public void prepararGamer(Player gamer) {
		gamer.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
		gamer.getPlayer().getInventory().clear();
		
		gamer.getPlayer().setItemOnCursor(new ItemStack(0));
		gamer.closeInventory();
        
        for (PotionEffect pe : gamer.getActivePotionEffects()) {
        	 gamer.removePotionEffect(pe.getType());
        }
        
        gamer.setGameMode(GameMode.SURVIVAL);
        gamer.setAllowFlight(false);
        gamer.setFlying(false);
        gamer.setFireTicks(0);
        
        KitManager.darItens(gamer);
	}
	
	public void setGamer(Player player) {
		Gamer gamer = HungerGames.getManager().getGamer(player.getUniqueId());
		gamer.setJogando(true);
		
		if (isPreGame()) {
			return;
		}
		
		player.setNoDamageTicks(100);
		
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		
		player.updateInventory();
		
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setGameMode(GameMode.SURVIVAL);
		
		KitManager.darItens(player);
	}
	
	public void setEspectador(Player player) {
		setEspectador(player, false);
	}
	
	public void setEspectador(Player player, boolean adminChange) {
		Gamer gamer = HungerGames.getManager().getGamer(player.getUniqueId());
		
		gamer.setJogando(false);
		gamer.setEliminado(true);
		
		if (!player.getGameMode().equals(GameMode.ADVENTURE)) {
		    player.setGameMode(GameMode.ADVENTURE);
		}
		
		player.setHealth(20.0D);
		player.setFireTicks(0);
		player.setFoodLevel(20);
		player.setAllowFlight(true);
		player.setFlying(true);
		
		if (!adminChange) {
			for (Player ons : Bukkit.getOnlinePlayers()) {
	    	     ons.hidePlayer(player);
			}
		}
	    
	    HungerGames.runLater(() -> {
			if (!adminChange) {
				player.getInventory().addItem(new ItemBuilder().material(Material.COMPASS).name("§aJogadores Vivos").build());
				if (BukkitMain.getManager().getDataManager().getBukkitPlayer(
					player.getUniqueId()).getDataHandler().getData(DataType.GRUPO).getGrupo().getNivel() >= Groups.YOUTUBER_PLUS.getNivel()) {
	    			VanishManager.changeAdmin(player, false);
	    		}
	    	}
			checkWin();
	    }, 20);
	}
	
	public void prepararLocations() {
		for (int i = 0; i <= 20; i++) {
			 addLocation(160);
		}
	}
	
	public Location getRespawnLocation() {
		return locaisToRespawn.get(new Random().nextInt(locaisToRespawn.size()));
	}
	
	public void addLocation(int maximo) {
		Random r = new Random();
		int x = r.nextInt(maximo) + maximo;
		int z = r.nextInt(maximo) + maximo;
		
		if (r.nextBoolean()) {
			x = -x;
		}
		
		if (r.nextBoolean()) {
			z = -z;
		}
		
		World world = (World) Bukkit.getServer().getWorlds().get(0);
		Location loc = new Location(world, x + 0.500, (world.getHighestBlockYAt(x, z) + 1.200), z + 0.500);
		
		locaisToRespawn.add(loc);
	}
	
	public void checkBorder(Player player, int raio) {
		if (player.getLocation().getBlockY() >= 130) {
			
			if (isPreGame()) {
				return;
			}
			
			if (!HungerGames.getManager().getGamer(player.getUniqueId()).isJogando()) {
				player.teleport(player.getLocation().subtract(0, 3, 0));
				return;
			}
			
			player.setFireTicks(100);
			player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20, 2));
			player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20, 2));
			player.damage(3.0D);
			return;
		}
		
		if (player.getLocation().getBlockX() >= raio || player.getLocation().getBlockX() <= -raio
			||  player.getLocation().getBlockZ() >= raio || player.getLocation().getBlockZ() <= -raio) {
			
			if (isPreGame()) {
				return;
			}
			
			if (!HungerGames.getManager().getGamer(player.getUniqueId()).isJogando()) {
				player.teleport(getRespawnLocation());
				return;
			}
			
			player.setFireTicks(100);
			player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20, 2));
			player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20, 2));
			player.damage(3.0D);
		}
	}
	
	public void strikeLightning(Location coords) {
		coords.getWorld().strikeLightningEffect(coords);
		Block block = coords.getBlock();
		block.setType(Material.AIR);
	}
	
	public void spawnarArenaFinal() {
		if (!ServerOptions.isDano()) {
		    ServerOptions.setDano(false);
		}
		
		int x = 0, y = 0, z = 0;
		
		Location loc = Bukkit.getWorld("world").getBlockAt(0, 15, 0).getLocation();
        List<Location> cuboid = new ArrayList<>();
        
		for (x = -25; x < 25; x++) {
			 for (y = 0; y < 80; y++) {
				  for (z = -25; z < 25; z++) {
					   setAsyncBlock(loc.getWorld(), loc.clone().add(x, y, z), Material.AIR.getId());
				  }
			 }
		}
    	for (int bX = -25; bX <= 25; bX++) {
    		 for (int bZ = -25; bZ <= 25; bZ++) {
    			  for (int bY = -1; bY <= 130; bY++) {
    				   if (bY == -1) {
    					   cuboid.add(loc.clone().add(bX, bY, bZ));
    				   } else if ((bX == -25) || (bZ == -25) || (bX == 25) || (bZ == 25)) {
    					   cuboid.add(loc.clone().add(bX, bY, bZ));
    				   }
    			  }
    		 }
    	}
    	
    	for (Location loc1 : cuboid) {
    		 setAsyncBlock(loc1.getWorld(), loc1, Material.BEDROCK.getId());
    	}
    	
    	Location local = new Location(Bukkit.getWorld("world"), 0, 15, 0);
    	
		int delay = 0;
		for (Player onlines : Bukkit.getOnlinePlayers()) {
			 new BukkitRunnable() {
				 public void run() {
					 onlines.setFallDistance(-5);
					 onlines.setNoDamageTicks(30);
					 onlines.teleport(local);
				 }
			 }.runTaskLater(BukkitMain.getInstance(), delay);
			 
			 delay++;
		}
        cuboid.clear();
        Bukkit.broadcastMessage("§4§lARENA §fA arena foi gerada.");
        
        HungerGames.runLater(() -> {
    		if (!ServerOptions.isDano()) {
    		    ServerOptions.setDano(true);
    		}
        });
	}
	
	public void setAsyncBlock(World world, Location location, int blockId) {
		setAsyncBlock(world, location, blockId, (byte) 0);
	}
	
	public void setAsyncBlock(World world, Location location, int blockId, byte data) {
		setAsyncBlock(world, location.getBlockX(), location.getBlockY(), location.getBlockZ(), blockId, data);
	}
	
	public void setAsyncBlock(World world, int x, int y, int z, int blockId, byte data) {
		net.minecraft.server.v1_8_R3.World w = ((CraftWorld) world).getHandle();
		net.minecraft.server.v1_8_R3.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
		BlockPosition bp = new BlockPosition(x, y, z);
		int i = blockId + (data << 12);
		IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(i);
		chunk.a(bp, ibd);
		w.notify(bp);
	}
	
	public void updateHGStats() {
		if (BukkitMain.getServerType() == ServerType.EVENTO) {
			Core.getServersHandler().sendUpdateHungerGamesEvent(BukkitMain.getServerID(), 
					getGamersVivos().size(), HungerGames.getManager().getTimerManager().getTempo(), estagio.getNome(), Bukkit.getMaxPlayers());
			return;
		}
		Core.getServersHandler().sendUpdateHungerGamesServer(
				BukkitMain.getServerID(), getGamersVivos().size(), HungerGames.getManager().getTimerManager().getTempo(), estagio.getNome(), Bukkit.getMaxPlayers());
	}
	
	public List<Player> getAliveGamers() {
		List<Player> gamers = new ArrayList<>();
		for (Player onlines : Bukkit.getOnlinePlayers()) {
			 if (HungerGames.getManager().getGamer(onlines.getUniqueId()).isJogando()) {
				 gamers.add(onlines);
			 }
		}
		return gamers;
	}
	
	public List<Gamer> getGamersVivos() {
		List<Gamer> vivos = new ArrayList<>();
		for (Gamer gamers : HungerGames.getManager().getGamers().values()) {
			 if (gamers.isJogando()) {
				 vivos.add(gamers);
			 }
		}
		return vivos;
	}
	
	public List<Gamer> getGamersSpecs() {
		List<Gamer> specs = new ArrayList<>();
		for (Gamer gamers : HungerGames.getManager().getGamers().values()) {
			 if (!gamers.isJogando() && gamers.isOnline()) {
				 specs.add(gamers);
			 }
		}
		return specs;
	}
	
	public List<Gamer> getGamersEliminateds() {
		List<Gamer> list = new ArrayList<>();
		for (Gamer gamers : HungerGames.getManager().getGamers().values()) {
			 if (gamers.isEliminado()) {
				 list.add(gamers);
			 }
		}
		return list;
	}
	
	public List<Gamer> getGamersToRelog() {
		List<Gamer> list = new ArrayList<>();
		for (Gamer gamers : HungerGames.getManager().getGamers().values()) {
			 if (gamers.isRelogar()) {
				 list.add(gamers);
			 }
		}
		return list;
	}

	public boolean isLoading() {
		return estagio.equals(Stages.CARREGANDO);
	}
	
	public boolean isPreGame() {
		return estagio.equals(Stages.PREJOGO);
	}
	
	public boolean isInvencibilidade() {
		return estagio.equals(Stages.INVENCIBILIDADE);
	}
	
	public boolean isGaming() {
		return estagio.equals(Stages.JOGANDO);
	}
	
	public boolean isEnd() {
		return estagio.equals(Stages.FIM);
	}
	
	public Player getWinner() {
		return winner;
	}

	public void setWinner(Player winner) {
		this.winner = winner;
	}

	public void setEstagio(Stages estagio) {
		this.estagio = estagio;
	}
	
	public void spawnar(String tipo, Location loc, boolean force) {
		File file = new File(
				Bukkit.getPluginManager().getPlugin(HungerGames.getInstance().getDescription().getName()).getDataFolder(), tipo + ".schematic");
		//root/plugins/PrismaConfig/schematics
		if (!file.exists()) {
			HungerGames.console("SchematicLoader '"+tipo+"' nao existe.");
			return;
		}
		
		SchematicAPI.spawnarSchematic(tipo, file, loc.getWorld(), loc, force); 
	}
	
	
	public void checkWin() {
		if (isEnd() || isPreGame()) {
			return;
		}
		
		if (Bukkit.getOnlinePlayers().size() == 0) {
			setEstagio(Stages.FIM);
			BungeeUtils.redirecionarTodosAsync("Lobby", true);
			return;
		}
		
		if (getAliveGamers().size() == 0) {
			setEstagio(Stages.FIM);
			BungeeUtils.redirecionarTodosAsync("Lobby", true);
			return;
		}
		
		if (getAliveGamers().size() == 1) {
			setEstagio(Stages.FIM);
			updateHGStats();
			
			ServerOptions.setDano(false);
			ServerOptions.setPvP(false);
			
			final Player vencedor = getAliveGamers().get(0);
			if (vencedor == null) {
				BungeeUtils.redirecionarTodosAsync("Lobby", true);
				return;
			}
			setWinner(vencedor);

			HungerGames.runLater(() -> {
				Location cake = vencedor.getLocation().clone();
			    cake.setY(156.0D);
			    for (int x = -1; x <= 1; x++) {
			    	 for (int z = -1; z <= 1; z++) {
			    		  cake.clone().add(x, 0.0D, z).getBlock().setType(Material.GLASS);
			    		  cake.clone().add(x, 1.0D, z).getBlock().setType(Material.CAKE_BLOCK);
			    	 }
			    }
			    for (Player onlines : Bukkit.getOnlinePlayers()) {
			    	 onlines.teleport(cake.clone().add(0, 4, 0));
			    }
			}, 1L);
			
			
		    BukkitPlayer bukkitPlayer = BukkitMain.getManager().getDataManager().getBukkitPlayer(vencedor.getUniqueId());
		    DataHandler dataHandlerWinner = bukkitPlayer.getDataHandler();
			
			winner.sendMessage("");
			winner.sendMessage("§e§lHG");
			winner.sendMessage("§aVocê venceu!");
			winner.sendMessage("");
			winner.sendMessage("Matou: §e" + HungerGames.getManager().getGamer(vencedor.getUniqueId()).getKills());
			winner.sendMessage("Tempo da partida: §7" + HungerGames.getManager().getTimerManager().getLastFormatted());
			winner.sendMessage("");
			
			final int xp = 100, 
					coins = 100;
			
			winner.sendMessage("§b+ " + xp + " XP");
			winner.sendMessage("§6+ " + coins + " moedas");
			winner.sendMessage("");
			
			bukkitPlayer.addXP(xp);
			
			dataHandlerWinner.getData(DataType.COINS).add(coins);
			dataHandlerWinner.getData(DataType.HG_WINS).add();
			
			dataHandlerWinner.updateValues(DataCategory.PRISMA_PLAYER, DataType.XP, DataType.COINS);
			dataHandlerWinner.updateValues(DataCategory.HUNGER_GAMES, DataType.HG_WINS);
			countDown();
		}
	}
	
	public void countDown() {
		new BukkitRunnable() {
			int segundos = 18;
			Random r = new Random();
			public void run() {
				if (segundos == 0) {
					BungeeUtils.redirecionarTodosAsync("Lobby", true);
					cancel();
					return;
				}
				
				Bukkit.broadcastMessage("§a" + getWinner().getName() + " ganhou a partida!");
				
				spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(getWinner().getLocation().add(0.0D, 0.0D, r.nextInt(5) + 5).add(0.0D, 5.0D, 0.0D)).getLocation());
		        spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(getWinner().getLocation().add(r.nextInt(5) + 5, 0.0D, 0.0D).add(0.0D, 5.0D, 0.0D)).getLocation());
		        spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(getWinner().getLocation().add(r.nextInt(5) + 5, 0.0D, r.nextInt(5) + 5).add(0.0D, 5.0D, 0.0D)).getLocation());
		        spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(getWinner().getLocation().add(-r.nextInt(5) - 5, 0.0D, 0.0D).add(0.0D, 5.0D, 0.0D)).getLocation());
		        spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(getWinner().getLocation().add(0.0D, 0.0D, -r.nextInt(5) - 5).add(0.0D, 5.0D, 0.0D)).getLocation());
		        spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(getWinner().getLocation().add(-r.nextInt(5) - 5, 0.0D, -r.nextInt(5) - 5).add(0.0D, 5.0D, 0.0D)).getLocation());
		        spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(getWinner().getLocation().add(-r.nextInt(5) - 5, 0.0D, r.nextInt(5) + 5).add(0.0D, 5.0D, 0.0D)).getLocation());
		        spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(getWinner().getLocation().add(r.nextInt(5) + 5, 0.0D, -r.nextInt(5) - 5).add(0.0D, 5.0D, 0.0D)).getLocation());
				segundos--;
			}
			}.runTaskTimer(HungerGames.getInstance(), 0, 20);
	}
	
	private void spawnRandomFirework(Location loc) {
		Firework fw = (Firework)loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();
		Random r = new Random();    
		int rt = r.nextInt(4) + 3;
		FireworkEffect.Type type = FireworkEffect.Type.BALL;
		if (rt == 1) {
			type = FireworkEffect.Type.BALL;
		} else if (rt == 2) {
		    type = FireworkEffect.Type.BALL_LARGE;
		} else if (rt == 3) {
		    type = FireworkEffect.Type.BURST;
		} else if (rt == 4) {
		    type = FireworkEffect.Type.CREEPER;
		} else if (rt == 5) {
			type = FireworkEffect.Type.STAR;
		} else if (rt > 5) {
			type = FireworkEffect.Type.BALL_LARGE;
		}
		Color c1 = Color.RED;
		Color c2 = Color.LIME;
		Color c3 = Color.SILVER;		    
		FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withColor(c2).withFade(c3).with(type).trail(r.nextBoolean()).build();
		fwm.addEffect(effect);			    
		int rp = r.nextInt(2) + 1;
		fwm.setPower(rp);
		fw.setFireworkMeta(fwm);
	}
}