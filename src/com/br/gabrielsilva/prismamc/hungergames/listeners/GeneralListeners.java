package com.br.gabrielsilva.prismamc.hungergames.listeners;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.bossbar.BossBarAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.VanishManager;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.server.ServerAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.AdminChangeEvent;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.PlayerGriefEvent;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.AdminChangeEvent.ChangeType;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.ScoreboardChangeEvent;
import com.br.gabrielsilva.prismamc.commons.core.data.type.DataType;
import com.br.gabrielsilva.prismamc.commons.core.group.Groups;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.api.schematic.SchematicAPI;
import com.br.gabrielsilva.prismamc.hungergames.commands.StafferCommand;
import com.br.gabrielsilva.prismamc.hungergames.manager.gamer.Gamer;

public class GeneralListeners implements Listener {
	
	private Random random = new Random();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager(),
					entity = (Player) event.getEntity();
			
			BossBarAPI.sendBossBar(damager,entity.getName() + " - " + HungerGames.getManager().getGamer(entity.getUniqueId()).getKit1(), 3);
		}
	}
	
	@EventHandler
	public void onGrief(PlayerGriefEvent event) {
		Player player = event.getPlayer();
		if (VanishManager.inAdmin(player)) {
			event.setCancelled(false);
		} else {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void adminChange(AdminChangeEvent event) {
		Player player = event.getPlayer();
		
		if (event.getChangeType() == ChangeType.ENTROU) {
			HungerGames.getManager().getGameManager().setEspectador(player, true);
		} else {
			HungerGames.getManager().getGameManager().setGamer(player);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			if (event.getAction().equals(Action.PHYSICAL)) {
				event.setCancelled(true);
			} else {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onScoreChange(ScoreboardChangeEvent event) {
		if (event.getChangeType() == com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.ScoreboardChangeEvent.ChangeType.ATIVOU) {
			HungerGames.getManager().getScoreboardManager().createSideBar(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			return;
		}
		
		if (ServerAPI.checkItem(event.getItemDrop().getItemStack(), "§bKit")) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop1(PlayerDropItemEvent event) {
		if (!StafferCommand.dropar) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPick(PlayerPickupItemEvent event) {
		if (!StafferCommand.dropar) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlace1(BlockPlaceEvent event) {
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
			return;
		}
		if ((HungerGames.getManager().getGameManager().isGaming()) && (SchematicAPI.Circulo.contains(event.getBlock()))) {
			if (!HungerGames.getManager().getStructureManager().getFeast().isSpawned()) {
			    event.setCancelled(true);
			    event.getPlayer().sendMessage("§cVoce nao pode construir no feast ate que os baús sejam spawnados.");
			    return;
			}
		}
		if (!StafferCommand.colocar) {
			if (BukkitMain.getManager().getDataManager().getBukkitPlayer(
				event.getPlayer().getUniqueId()).getDataHandler().getData(DataType.GRUPO).getGrupo().getNivel()  <= Groups.YOUTUBER_PLUS.getNivel()) {
	    	    event.setCancelled(true);
			}
		}
		
		if (event.getBlock().getLocation().getBlockY() > 128) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("§cA altura maxima permitida para construção e de 128.");
		}
	}

	@EventHandler
	public void onBrakBlock(BlockDamageEvent event) {
		Block block = event.getBlock();
		if (block.getLocation().getBlockX() == 501 || block.getLocation().getBlockZ() == 501
				|| block.getLocation().getBlockX() == -501 || block.getLocation().getBlockZ() == -501) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onBrakBlock(BlockBreakEvent event) {
		if (!StafferCommand.quebrar) {
			if (BukkitMain.getManager().getDataManager().getBukkitPlayer(
				event.getPlayer().getUniqueId()).getDataHandler().getData(DataType.GRUPO).getGrupo().getNivel()  <= Groups.YOUTUBER_PLUS.getNivel()) {
		    	event.setCancelled(true);
		    	return;
			}
		}
		
		Block block = event.getBlock();
		if (block.getLocation().getBlockX() == 501 || block.getLocation().getBlockZ() == 501
		|| block.getLocation().getBlockX() == -501 || block.getLocation().getBlockZ() == -501) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Fome(FoodLevelChangeEvent event) {
		if (!HungerGames.getManager().getGameManager().isGaming()) {
			event.setCancelled(true);
			return;
		}
		Player player = (Player)event.getEntity();
		if (!HungerGames.getManager().getGamer(player.getUniqueId()).isJogando()) {
			event.setCancelled(true);
			return;
		}
		player.setSaturation(4.2F);
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		
		if (!HungerGames.getManager().getGamer(player.getUniqueId()).isJogando()) {
			if (VanishManager.inAdmin(player)) {
				return;
			}
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent event) {
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntitySpawn(CreatureSpawnEvent event) {
		if (event.getEntityType() == EntityType.GHAST || event.getEntityType() == EntityType.PIG_ZOMBIE) {
			event.setCancelled(true);
			return;
		}
		
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
			return;
		}
		
		if (event.getSpawnReason() != SpawnReason.NATURAL)
			return;
		
		if (random.nextInt(5) != 0) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onExplosao(ExplosionPrimeEvent event) {
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void aoChover(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			if (HungerGames.getManager().getGameManager().isPreGame()) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (HungerGames.getManager().getGameManager().isEnd()) {
			event.setCancelled(true);
			return;
		}
		if (!HungerGames.getManager().getGameManager().isGaming()) {
			if (event.getEntity() instanceof Player) {
				event.setCancelled(true);
			} else {
				event.setCancelled(false);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (HungerGames.getManager().getGameManager().isPreGame() || (HungerGames.getManager().getGameManager().isEnd())) {
			event.setCancelled(true);
			return;
		}
		
		if (HungerGames.getManager().getGameManager().isInvencibilidade()) {
			if (event.getEntity() instanceof Player) {
				event.setCancelled(true);
			} else {
				event.setCancelled(false);
			}
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void BlockIgnite(BlockIgniteEvent event) {
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void craftItem(CraftItemEvent event) {
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void fisicaDoCogu(BlockPhysicsEvent e) {
		if (e.getBlock().getType().equals(Material.BROWN_MUSHROOM)) {
			e.setCancelled(true);
		} else if (e.getBlock().getType().equals(Material.RED_MUSHROOM)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player player = event.getPlayer();
		
		final Gamer gamer = HungerGames.getManager().getGamer(player.getUniqueId());
		
		if (!gamer.isJogando()) {
			return;
		}
		Block block = event.getBlock();
		
		if (block.getType() == Material.COBBLESTONE || block.getType() == Material.STONE) {
			if (!PlayerAPI.isFull(player.getInventory())) {
				event.getBlock().setType(Material.AIR);
				player.getInventory().addItem(new ItemStack(Material.COBBLESTONE));
			} else {
				for (ItemStack itens : player.getInventory().getContents()) {
					 if ((itens.getType().equals(block.getType())) && (itens.getAmount() != 64)) {
						  event.getBlock().setType(Material.AIR);
						  player.getInventory().addItem(new ItemStack(Material.COBBLESTONE));
						  break;
					 }
				}
			}
		} else if (block.getType() == Material.BROWN_MUSHROOM) {
			if (!PlayerAPI.isFull(player.getInventory())) {
				event.getBlock().setType(Material.AIR);
				player.getInventory().addItem(new ItemStack(Material.BROWN_MUSHROOM));
			} else {
				for (ItemStack itens : player.getInventory().getContents()) {
					 if ((itens.getType().equals(block.getType())) && (itens.getAmount() != 64)) {
						  event.getBlock().setType(Material.AIR);
						  player.getInventory().addItem(new ItemStack(Material.BROWN_MUSHROOM));
						  break;
					 }
				}
			}
		} else if (block.getType() == Material.RED_MUSHROOM) {
			if (!PlayerAPI.isFull(player.getInventory())) {
				event.getBlock().setType(Material.AIR);
				player.getInventory().addItem(new ItemStack(Material.RED_MUSHROOM));
			} else {
				for (ItemStack itens : player.getInventory().getContents()) {
					 if ((itens.getType().equals(block.getType())) && (itens.getAmount() != 64)) {
						  event.getBlock().setType(Material.AIR);
						  player.getInventory().addItem(new ItemStack(Material.RED_MUSHROOM));
						  break;
					 }
				}
			}
		} else if (block.getType().name().contains("LOG")) {
			if (gamer.containsKit("Lumberjack")) {
				return;
			}
			if (gamer.containsKit("JackHammer")) {
				return;
			}
			if (!PlayerAPI.isFull(player.getInventory())) {
				ArrayList<ItemStack> items = new ArrayList<>(event.getBlock().getDrops());
				for (ItemStack item : items) {
					 player.getInventory().addItem(item);
				}
				event.getBlock().setType(Material.AIR);
			} else {
				for (ItemStack itens : player.getInventory().getContents()) {
					 if ((itens.getType().equals(block.getType())) && (itens.getAmount() != 64)) {
						  ArrayList<ItemStack> items = new ArrayList<>(event.getBlock().getDrops());
						  for (ItemStack item : items) {
							   player.getInventory().addItem(item);
						  }
						  event.getBlock().setType(Material.AIR);
						  break;
					 }
				}
			}
		}
	}
}