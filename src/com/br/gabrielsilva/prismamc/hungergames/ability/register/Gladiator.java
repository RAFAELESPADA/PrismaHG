package com.br.gabrielsilva.prismamc.hungergames.ability.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.UpdateEvent;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.UpdateEvent.UpdateType;
import com.br.gabrielsilva.prismamc.commons.core.data.type.DataType;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Gladiator extends Kit {

	public Gladiator() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
		
		ItemStack icone = new ItemBuilder().
				material(Material.IRON_FENCE).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(8000);
				setCooldownSegundos(60);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Puxe inimigos");
		indiob.add(ChatColor.GRAY + "Para sua Arena Individual");
		indiob.add(ChatColor.RED + "KIT DO MÊS");
				setDescrição(indiob);
		setItens(new ItemBuilder().material(Material.IRON_FENCE).name(getItemColor() + "Kit " + getNome()).build());
	}
	
	public static ConcurrentHashMap<String, GladiatorFight> gladiatorFights = new ConcurrentHashMap<>();
	public static HashMap<UUID, String> gladArena = new HashMap<>();
	
	@EventHandler
	public void onSecond(UpdateEvent event) {
		if (event.getType() == UpdateType.SEGUNDO) {
			
			for (GladiatorFight glads : gladiatorFights.values()) {
				 if (!glads.ended) {
					 glads.onSecond();
				 }
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			if (inGlad((Player) event.getEntity())) {
				if (!getGladArena((Player) event.getEntity()).equals(getGladArena((Player) event.getDamager()))) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	public static String getGladArena(Player player) {
		return gladArena.get(player.getUniqueId());
	}

	public static boolean inGlad(Player player) {
		return gladArena.containsKey(player.getUniqueId());
	}
	
	public static GladiatorFight getGladiatorFight(Player player) {
		return gladiatorFights.get(getGladArena(player));
	}

	@EventHandler
	public void hability(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Player))
			return;
		
	    Player player = event.getPlayer();
	    if ((player.getItemInHand().getType().equals(Material.IRON_FENCE)) && (useAbility(player))) {
	    	if (inGlad(player)) {
	    		player.sendMessage("§cVocê já está em um gladiator.");
	    		return;
	    	}
	    	Player player1 = (Player)event.getRightClicked();
	    	if (inGlad(player1)) {
	    		player.sendMessage("§cEste jogador já está em um Gladiator.");
	    		return;
	    	}
	    	
	        Location toGlad = getLocationForGlad(player.getLocation());
	        if (toGlad == null) {
	        	player.sendMessage("§cVocê não pode criar sua arena aqui.");
	        	return;
	        }
	    	
	 		int id = getArenaID();
			if ((gladiatorFights.containsKey("arena" + id)) || (id == 0)) {
				 player.sendMessage("§cVocê não pode criar sua arena aqui. #2");
				 return;
			}
			gladiatorFights.put("arena" + id, new GladiatorFight(toGlad.getBlock(), id, player, player1));
			 
	        gladArena.put(player.getUniqueId(), "arena" + id);
	        gladArena.put(player1.getUniqueId(), "arena" + id);
	    	
	        removerBlocos(toGlad);
	        criarGladiator(toGlad);
	        
	 	    Location l1 = new Location(toGlad.getWorld(), toGlad.getX() + 6.5D, toGlad.getY() + 1.500, toGlad.getZ() + 6.5D);
		    l1.setYaw(135.0F);
		    Location l2 = new Location(toGlad.getWorld(), toGlad.getX() - 5.5D, toGlad.getY() + 1.500, toGlad.getZ() - 5.5D);
		    l2.setYaw(315.0F);
	        
		    player.setFallDistance(-5);
		    player1.setFallDistance(-5);
		    
		    player.teleport(l1);
		    player1.teleport(l2);
	    }
	}
	
	private int getArenaID() {
		int sala = 0;
		for (int i = 1; i <= 50; i++) {
			 if (!gladiatorFights.containsKey("arena" + i)) {
				  sala = i;
				  break;
			 }
		}
		return sala;
	}
	
    @EventHandler
	public void Break(BlockBreakEvent e) {
    	if (e.getBlock().getType().equals(Material.GLASS)) {
    		if (gladArena.containsKey(e.getPlayer().getUniqueId())) {
			    e.setCancelled(true);
    		}
		}
	}
    
    @EventHandler(priority=EventPriority.LOWEST)
	public void onPlace(BlockPlaceEvent event) {
    	Player player = event.getPlayer();
    	if (event.getBlock().getType() == Material.IRON_FENCE) {
    		if (containsHability(event.getPlayer())) {
    			event.setCancelled(true);
    		}
    	}
    }

	public static Location getLocationForGlad(Location loc) {
		loc.setY(110);
		
		boolean hasGladi = true;
		while (hasGladi) {
			hasGladi = false;
			boolean stop = false;
			for (double x = -8; x <= 8; x++) {
				for (double z = -8; z <= 8; z++) {
					for (double y = 0; y <= 10; y++) {
						Location l = new Location(loc.getWorld(), loc.getX() + x, 110 + y, loc.getZ() + z);
						if (l.getBlock().getType() != Material.AIR) {
							hasGladi = true;
							loc = new Location(loc.getWorld(), loc.getX() + 40, loc.getY(), loc.getZ());
							stop = true;
						}
						if (stop)
							break;
					}
					if (stop)
						break;
				}
				if (stop)
					break;
			}
		}

		return loc;
	}
	
	public static void criarGladiator(Location loc) {
		List<Location> cuboid = new ArrayList<>();

    	for (int bX = -8; bX <= 8; bX++) {
    		 for (int bZ = -8; bZ <= 8; bZ++) {
    			  for (int bY = -1; bY <= 10; bY++) {
    				  if (bY == -1) {
    					  cuboid.add(loc.clone().add(bX, bY, bZ));
    				  } else if ((bX == -8) || (bZ == -8) || (bX == 8) || (bZ == 8)) {
    					  cuboid.add(loc.clone().add(bX, bY, bZ));
    				  }
    			  }
    		 }
    	}
    	
         for (Location loc1 : cuboid) {
              loc1.getBlock().setType(Material.GLASS);
       	      loc1.getBlock().setData((byte) 0);
         }
         
         cuboid.clear();
         cuboid = null;
	}
	
	public static void removerBlocos(Location loc) {
		List<Location> cuboid = new ArrayList<>();
		
    	for (int bX = -8; bX <= 8; bX++) {
   		     for (int bZ = -8; bZ <= 8; bZ++) {
   			      for (int bY = -1; bY <= 10; bY++) {
   				       cuboid.add(loc.clone().add(bX, bY, bZ));
   			      }
   		     }
    	}
    	
    	for (Location loc1 : cuboid) {
    		 loc1.getBlock().setType(Material.AIR);
    	}
    	
    	cuboid.clear();
    	cuboid = null;
	}
	
	public class GladiatorFight {
		
		private Player[] players;
		private Location[] locations;
		private Block mainBlock;
		private int seconds, id;
		private boolean ended;
		
		public GladiatorFight(Block mainBlock, int id, Player... players) {
			this.mainBlock = mainBlock;
			this.seconds = 201;
			this.ended = false;
			
			this.id = id;
			
			this.players = new Player[2];
			this.locations = new Location[2];
			
			this.players[0] = players[0];
			this.players[1] = players[1];
			
			this.locations[0] = players[0].getLocation();
			this.locations[1] = players[1].getLocation();
		}

		public void onSecond() {
			if (this.ended) {
				return;
			}
			
			for (Player player : this.players) {
				 if (player.isOnline()) {
					 if (player.getLocation().getBlockY() < mainBlock.getLocation().getBlockY() - 2) {
						 cancelGlad();
					 }
				 }
			}
			
			if (this.seconds == 80) {
				for (Player player : this.players) {
					 if (player.isOnline()) {
						 player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 9999999, 0));
					 }
				}
			} else if (this.seconds == 0) {
				cancelGlad();
				return;
			}	
			this.seconds--;
		}
		
		public void teleportBack() {
			for (Player player : this.players) {
				 if (player.isOnline()) {
					 player.setFallDistance(-5);
					 player.setNoDamageTicks(40);
					 player.teleport(getBackForPlayer(player));
				 }
			}
		}
		
		public Location getBackForPlayer(Player player) {
			if (player.getUniqueId().equals(players[0].getUniqueId())) {
				return locations[0];
			} else {
				return locations[1];
			}
		}
		
		public void cancelGlad() {
			this.ended = true;
			
			for (Player player : this.players) {
				 if (player.isOnline()) {
					 if (player.hasPotionEffect(PotionEffectType.WITHER)) {
						 player.removePotionEffect(PotionEffectType.WITHER);
					 }
				 }
				 
				 gladArena.remove(player.getUniqueId());
			}
			
			teleportBack();
			removerBlocos(mainBlock.getLocation());
			
			gladiatorFights.remove("arena" + id);
		}
	}
	
	public static boolean isQuitedOnGladiator(Player player, Location loc) {
		if (inGlad(player)) {
			
			GladiatorFight glad = getGladiatorFight(player);
			glad.teleportBack();
			
			Player winner = null;
			
			if (glad.players[0].getUniqueId().equals(player.getUniqueId())) {
				winner = glad.players[1];
			} else {
				winner = glad.players[0];
			}
			
			PlayerAPI.dropItems(player, glad.getBackForPlayer(player).add(0, 0.5, 0));
			
			glad.cancelGlad();
			BukkitPlayer bukkitPlayerWinner = BukkitMain.getManager().getDataManager().getBukkitPlayer(winner.getUniqueId());
			
			final int xp = PlayerAPI.getXPKill(winner, player),
					coins = PlayerAPI.KILL_COINS;
			
			bukkitPlayerWinner.addXP(xp);
			bukkitPlayerWinner.getData(DataType.COINS).add(coins);
			bukkitPlayerWinner.getData(DataType.HG_KILLS).add();
			HungerGames.getManager().getGamer(winner.getUniqueId()).addKill();
			return true;
		}
		return false;
	}
}