package com.br.gabrielsilva.prismamc.hungergames.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.VanishManager;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.server.ServerAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.utils.BungeeUtils;
import com.br.gabrielsilva.prismamc.commons.core.data.DataHandler;
import com.br.gabrielsilva.prismamc.commons.core.data.category.DataCategory;
import com.br.gabrielsilva.prismamc.commons.core.data.type.DataType;
import com.br.gabrielsilva.prismamc.commons.core.server.ServerType;
import com.br.gabrielsilva.prismamc.commons.core.utils.string.PluginMessages;
import com.br.gabrielsilva.prismamc.commons.custompackets.BukkitClient;
import com.br.gabrielsilva.prismamc.commons.custompackets.bungee.packets.PacketBungeeUpdateField;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.ability.register.Gladiator;
import com.br.gabrielsilva.prismamc.hungergames.ability.register.Gladiator.GladiatorFight;
import com.br.gabrielsilva.prismamc.hungergames.manager.gamer.Gamer;
import com.br.gabrielsilva.prismamc.hungergames.manager.kit.KitManager;
import com.br.gabrielsilva.prismamc.hungergames.menus.KitSelector;
import com.br.gabrielsilva.prismamc.hungergames.menus.enums.InventoryMode;

public class GameListener implements Listener {
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onLogin(PlayerLoginEvent event) {
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if ((HungerGames.getManager().getGameManager().isLoading()) || (HungerGames.getManager().getGameManager().isEnd())) {
			if (HungerGames.getManager().getGameManager().isEnd()) {
				event.disallow(Result.KICK_OTHER, "§cO jogo esta sendo encerrado.");
			} else {
				event.disallow(Result.KICK_OTHER, "§cO servidor esta carregando.");
			}
			BukkitMain.getManager().getDataManager().removeBukkitPlayerIfExists(player.getUniqueId());
			return;
		}

		if (HungerGames.getManager().getGameManager().getAliveGamers().size() >= 100) {
			if (!HungerGames.isVip(player)) {
				event.disallow(Result.KICK_OTHER, "§cOs Slots para membros acabaram, compre VIP e tenha slot reservado.");
				BukkitMain.getManager().getDataManager().removeBukkitPlayerIfExists(player.getUniqueId());
				return;
			}
		}
		
		if (!BukkitMain.getManager().getDataManager().getBukkitPlayer(player.getUniqueId()).getDataHandler().load(DataCategory.HUNGER_GAMES)) {
			event.disallow(Result.KICK_OTHER, "§cOcorreu um erro ao tentar carregar suas informa§§es...");
			BukkitMain.getManager().getDataManager().removeBukkitPlayerIfExists(player.getUniqueId());
			return;
		}
		
		if (!HungerGames.getManager().getGameManager().isPreGame()) {
			
			if (HungerGames.getManager().getGamers().containsKey(player.getUniqueId())) {
				Gamer gamer = HungerGames.getManager().getGamer(player.getUniqueId());
				
				if (gamer.isRelogar()) {
					event.allow();
					return;
				}
				
				if (gamer.isEliminado()) {
					if (!HungerGames.isVip(player)) {
						event.disallow(Result.KICK_OTHER, "§cVoce foi eliminado da partida, compre VIP e ganhe a possibilidade de espectar partidas.");
						BukkitMain.getManager().getDataManager().removeBukkitPlayerIfExists(player.getUniqueId());
					} else {
						event.allow();
					}
				}
			} else {
				if (!HungerGames.isVip(player)) {
					event.disallow(Result.KICK_OTHER, "§cA partida já come§ou, compre VIP e especte a partida.");
					BukkitMain.getManager().getDataManager().removeBukkitPlayerIfExists(player.getUniqueId());
				} else {
					event.allow();
				}
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		HungerGames.getManager().prepareData(player);
		
		HungerGames.getManager().getScoreboardManager().createSideBar(player);
		
		Gamer gamer = HungerGames.getManager().getGamer(player.getUniqueId());
		gamer.setOnline(true);
		
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			gamer.setJogando(true);
			
			if (!player.getGameMode().equals(GameMode.ADVENTURE)) {
			    player.setGameMode(GameMode.ADVENTURE);
			}
			
			player.setFoodLevel(20);
			player.setFireTicks(0);
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			
			player.getInventory().setItem(3, new ItemBuilder().material(Material.CHEST).name("§aKit").build());
			player.getInventory().setItem(5, new ItemBuilder().material(Material.DOUBLE_PLANT).name("§aLoja de Kits").build());
			
			player.updateInventory();
			player.teleport(HungerGames.getManager().getGameManager().getSpawn());
		} else {
			if (gamer.isRelogar()) {
				gamer.setRelogar(false);
				gamer.setEliminado(true);
				Bukkit.broadcastMessage("§a" + player.getName() + " voltou ao jogo.");
				return;
			}
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			player.updateInventory();
			
			if (gamer.isEliminado()) {
				HungerGames.getManager().getGameManager().setEspectador(player);
				return;
			}
			if ((HungerGames.isVip(player)) && (HungerGames.getManager().getTimerManager().getTempo() <= 300)) {
			     gamer.setEliminado(true);
				 HungerGames.getManager().getGameManager().setGamer(player);
				 player.teleport(HungerGames.getManager().getGameManager().getRespawnLocation());
				 Bukkit.broadcastMessage("§a" + player.getName() + " entrou no jogo.");
			} else {
			    HungerGames.getManager().getGameManager().setEspectador(player);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if (!HungerGames.getManager().getGamers().containsKey(player.getUniqueId())) {
			HungerGames.getManager().getGameManager().checkWin();
			return;
		}
		
		Gamer gamer = HungerGames.getManager().getGamer(player.getUniqueId());
		gamer.setOnline(false);
		
		if (!HungerGames.getManager().getGameManager().isPreGame()) {
			if (!gamer.isJogando()) {
				HungerGames.getManager().getGameManager().checkWin();
				return;
			}
			if (VanishManager.inAdmin(player)) {
				gamer.setJogando(false);
				HungerGames.getManager().getGameManager().checkWin();
				return;
			}
			
			final Location loc = player.getLocation();
			BukkitPlayer bukkitPlayer = BukkitMain.getManager().getDataManager().getBukkitPlayer(player.getUniqueId());
			
			if (Gladiator.isQuitedOnGladiator(player, loc)) {
				gamer.setJogando(false);
				gamer.setEliminado(true);
				
				DataHandler dataHandlerLoser = bukkitPlayer.getDataHandler();
				dataHandlerLoser.getData(DataType.HG_DEATHS).add();
				dataHandlerLoser.getData(DataType.COINS).remove(PlayerAPI.DEATH_COINS);
				dataHandlerLoser.getData(DataType.XP).remove(PlayerAPI.DEATH_XP);
				
				int jogadoresRestantes = HungerGames.getManager().getGameManager().getAliveGamers().size();
				Bukkit.broadcastMessage("§b" + player.getName() + " ("+gamer.getKit1()+") saiu em combate.\n" + 
				"§c" + jogadoresRestantes + " jogadores restantes.");
				
				dataHandlerLoser.updateValues(DataCategory.HUNGER_GAMES, DataType.HG_DEATHS);
				dataHandlerLoser.updateValues(DataCategory.PRISMA_PLAYER, DataType.COINS, DataType.XP);
				if (jogadoresRestantes == 1) {
					HungerGames.getManager().getGameManager().checkWin();
				}
				return;
			}
			
			final Player lastHit = CombatLogListener.getLastHit(player);
			if (lastHit != null) {
				gamer.setJogando(false);
				gamer.setEliminado(true);
				
				DataHandler dataHandlerLoser = bukkitPlayer.getDataHandler();
				dataHandlerLoser.getData(DataType.HG_DEATHS).add();
				dataHandlerLoser.getData(DataType.COINS).remove(PlayerAPI.DEATH_COINS);
				dataHandlerLoser.getData(DataType.XP).remove(PlayerAPI.DEATH_XP);
				
				int jogadoresRestantes = HungerGames.getManager().getGameManager().getAliveGamers().size();
				Bukkit.broadcastMessage("§b" + player.getName() + " ("+gamer.getKit1()+") saiu em combate.\n" + 
				"§c" + jogadoresRestantes + " jogadores restantes.");
				
				dataHandlerLoser.updateValues(DataCategory.HUNGER_GAMES, true, DataType.HG_DEATHS);
				dataHandlerLoser.updateValues(DataCategory.PRISMA_PLAYER, true, DataType.COINS, DataType.XP);
				
				BukkitPlayer bukkitPlayerWinner = BukkitMain.getManager().getDataManager().getBukkitPlayer(lastHit.getUniqueId());
				
				final int xp = PlayerAPI.getXPKill(lastHit, player),
						coins = PlayerAPI.KILL_COINS;
				
				bukkitPlayerWinner.addXP(xp);
				bukkitPlayerWinner.getData(DataType.COINS).add(coins);
				bukkitPlayerWinner.getData(DataType.HG_KILLS).add();
				
				bukkitPlayerWinner.getDataHandler().updateValues(DataCategory.HUNGER_GAMES, true, DataType.HG_KILLS);
				bukkitPlayerWinner.getDataHandler().updateValues(DataCategory.PRISMA_PLAYER, true, DataType.COINS, DataType.XP);
				
				HungerGames.getManager().getGamer(lastHit.getUniqueId()).addKill();
				
				if (jogadoresRestantes == 1) {
					HungerGames.getManager().getGameManager().checkWin();
				}
				return;
			}

			if (gamer.isEliminado()) {
				gamer.setJogando(false);
				PlayerAPI.dropItems(player, loc);
				
				Bukkit.broadcastMessage("§b" + player.getName() + " ("+gamer.getKit1()+") desistiu da partida.\n" + 
				"§c" + HungerGames.getManager().getGameManager().getAliveGamers().size() + " jogadores restantes.");
				HungerGames.getManager().getGameManager().checkWin();
				return;
			}
			
			if (!gamer.isRelogar()) {
			    gamer.setRelogar(true);
			    handleRelog(player, loc);
				Bukkit.broadcastMessage("§c" + player.getName() + " saiu do servidor.");
			}
		} else {
			gamer.setJogando(false);
			BukkitMain.getManager().getDataManager().removeBukkitPlayerIfExists(player.getUniqueId());
		}
	}
	
	@EventHandler
	public void onFastRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(HungerGames.getManager().getGameManager().getRespawnLocation());
		
		Player player = event.getPlayer();
		
		boolean moveToLobby = false;
		
		if (BukkitMain.getServerType() == ServerType.EVENTO) {
			if (HungerGames.isVip(player)) {
				HungerGames.runLater(() -> {
					HungerGames.getManager().getGameManager().setEspectador(player);
				}, 5);
			} else {
				moveToLobby = true;
			}
		} else {
			if (HungerGames.isVip(player) && (HungerGames.getManager().getTimerManager().getTempo() <= 300)) {
				HungerGames.runLater(() -> {
					HungerGames.getManager().getGameManager().setGamer(player);
					player.teleport(HungerGames.getManager().getGameManager().getRespawnLocation());
				}, 5);
			} else {
				if (HungerGames.isVip(player)) {
					HungerGames.runLater(() -> {
						HungerGames.getManager().getGameManager().setEspectador(player);
					}, 5);
				} else {
					moveToLobby = true;
				}
			}
		}
		
		if (moveToLobby) {
			for (Player ons : Bukkit.getOnlinePlayers()) {
				 ons.hidePlayer(player);
			}
			
			HungerGames.runLater(() -> {
				BungeeUtils.redirecionarWithKick(player, "Lobby");
			}, 40);
		}
		
		HungerGames.runLater(() -> {
			HungerGames.getManager().getGameManager().checkWin();
		}, 60);
	}
	
	private void handleRelog(Player player, Location loc) {
		List<ItemStack> drop = new ArrayList<>();
		drop.addAll(Arrays.asList(player.getInventory().getContents()));
		drop.addAll(Arrays.asList(player.getInventory().getArmorContents()));
		drop.add(player.getItemOnCursor());
		 
		HungerGames.runLater(() -> {
			Gamer gamer = HungerGames.getManager().getGamer(player.getUniqueId());
		    if ((!player.isOnline()) && (gamer.isRelogar())) {
		    	 gamer.setJogando(false);
		    	 gamer.setEliminado(true);
		    	 gamer.setRelogar(false);
		    	 
		    	 PlayerAPI.dropItems(player, drop, loc);
		    	 
		 		 Bukkit.broadcastMessage("§b" + player.getName() + " ("+gamer.getKit1()+") demorou muito para retornar e foi desclassificado\n" + 
					   	 "§c" + HungerGames.getManager().getGameManager().getAliveGamers().size() + " jogadores restantes.");
				
		    	 HungerGames.getManager().getGameManager().checkWin();
		    }
		}, 20 * 90);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
		event.getDrops().clear();

		Player morreu = event.getEntity(),
				matou = morreu.getKiller();
		
		Location loc = morreu.getLocation();
		if (Gladiator.inGlad(morreu)) {
			GladiatorFight glad = Gladiator.getGladiatorFight(morreu);
			
			loc = glad.getBackForPlayer(morreu);

			glad.cancelGlad();
		}
		
		HungerGames.getManager().getGameManager().strikeLightning(loc);
		PlayerAPI.dropItems(morreu, loc);
		
		BukkitPlayer bukkitPlayer = BukkitMain.getManager().getDataManager().getBukkitPlayer(morreu.getUniqueId());
		bukkitPlayer.getData(DataType.COINS).remove(PlayerAPI.DEATH_COINS);
		bukkitPlayer.getData(DataType.XP).remove(PlayerAPI.DEATH_XP);
		bukkitPlayer.getData(DataType.HG_DEATHS).add();
		
		bukkitPlayer.getDataHandler().updateValues(DataCategory.HUNGER_GAMES, true, DataType.HG_DEATHS);
		bukkitPlayer.getDataHandler().updateValues(DataCategory.PRISMA_PLAYER, true, DataType.COINS, DataType.XP);
		
		boolean respawn = false;
		Gamer gamer = HungerGames.getManager().getGamer(morreu.getUniqueId());
		if (BukkitMain.getServerType() == ServerType.EVENTO) {
			gamer.setEliminado(true);
			gamer.setJogando(false);
		} else {
			if (HungerGames.isVip(morreu)) {
				if (HungerGames.getManager().getTimerManager().getTempo() <= 300) {
					respawn = true;
				}
			}
			
			if (!respawn) {
				gamer.setEliminado(true);
				gamer.setJogando(false);
			}
		}

		final int jogadoresRestantes = 
				HungerGames.getManager().getGameManager().getAliveGamers().size();
		
		if (matou != null) {
			Gamer gamerMatou = HungerGames.getManager().getGamer(matou.getUniqueId());
			
			Bukkit.broadcastMessage("§b" + matou.getName() + "("+gamerMatou.getKit1()+") matou " + morreu.getName() + "("+gamer.getKit1()+
			") utilizando " + getItemInHand(matou.getItemInHand().getType()) + "\n§c" + jogadoresRestantes + " jogadores restantes");
			
			BukkitPlayer bukkitPlayerWinner = BukkitMain.getManager().getDataManager().getBukkitPlayer(matou.getUniqueId());
			
			
			final int xp = PlayerAPI.getXPKill(matou, morreu), 
					coins = PlayerAPI.KILL_COINS;
		
			bukkitPlayerWinner.getData(DataType.COINS).add(coins);
			bukkitPlayerWinner.getData(DataType.HG_KILLS).add(1);
			
			bukkitPlayerWinner.addXP(xp);
			
			bukkitPlayerWinner.getDataHandler().updateValues(DataCategory.HUNGER_GAMES, true, DataType.HG_KILLS);
			bukkitPlayerWinner.getDataHandler().updateValues(DataCategory.PRISMA_PLAYER, true, DataType.COINS, DataType.XP);
			
			gamerMatou.addKill();
			handleClanElo(matou, morreu,
					bukkitPlayerWinner.getString(DataType.CLAN), bukkitPlayer.getString(DataType.CLAN), bukkitPlayerWinner.getNick());
		} else {
			Bukkit.broadcastMessage("§b" + morreu.getName() + "(" + gamer.getKit1() + ") "
			+ "morreu " + getCausa(morreu.getLastDamageCause().getCause()) + (respawn ? "." : " e foi eliminado do torneio." + "\n§c"+
			jogadoresRestantes + " jogadores restantes"));
		}
		
		morreu.spigot().respawn();
	}
	
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	
		if (!HungerGames.getManager().getGameManager().isPreGame()) {
			return;
		}
		
        if (!event.getAction().name().contains("RIGHT")) {
        	return;
        }
        
        event.setCancelled(true);
        
        ItemStack itemStack = player.getInventory().getItemInHand();
        if (ServerAPI.checkItem(itemStack, "§aKit")) {
        	if (KitManager.isAllKitsDesativados()) {
	    		player.sendMessage("§cTodos os Kits estão desativados.");
	    		return;
	    	}
        	new KitSelector(event.getPlayer()).open(event.getPlayer());
        } else if (ServerAPI.checkItem(itemStack, "§aLoja de Kits")) {
           	new KitSelector(event.getPlayer(), InventoryMode.LOJA).open(event.getPlayer());
        }
    }

	private static void handleClanElo(Player winner, Player loser, String clanWin, String clanLoser, String nick) {
		if (!clanWin.equalsIgnoreCase("Nenhum")) {
			if (!clanLoser.equalsIgnoreCase(clanWin)) {
				BukkitClient.sendPacket(winner, new PacketBungeeUpdateField(nick, "Clan", "AddElo", clanWin, "10"));

				winner.sendMessage(PluginMessages.CLAN_WIN_ELO.replace("%quantia%", "10"));
			}
		}
		
		if (!clanLoser.equalsIgnoreCase("Nenhum")) {
			if (!clanLoser.equalsIgnoreCase(clanWin)) {
				BukkitClient.sendPacket(winner, new PacketBungeeUpdateField(nick, "Clan", "RemoveElo", clanLoser, "5"));
				if (loser != null && loser.isOnline()) {
					loser.sendMessage(PluginMessages.CLAN_LOSE_ELO.replace("%quantia%", "5"));
				}
			}
		}
	}
	
	private static String getItemInHand(Material material) {
		String causa = "";
		if (material.equals(Material.WOOD_SWORD))
			causa = "uma Espada de Madeira";
		 else if (material.equals(Material.STONE_SWORD))
			causa = "uma Espada de Pedra";
		 else if (material.equals(Material.GOLD_SWORD))
			causa = "uma Espada de Ouro";
		 else if (material.equals(Material.IRON_SWORD))
			causa = "uma Espada de Ferro";
		 else if (material.equals(Material.DIAMOND_SWORD))
			causa = "uma Espada de Diamante";
		 else if (material.equals(Material.WOOD_PICKAXE))
			causa = "uma Picareta de Madeira";
		 else if (material.equals(Material.STONE_PICKAXE))
			causa = "uma Picareta de Pedra";
		else if (material.equals(Material.GOLD_PICKAXE))
			causa = "uma Picareta de Ouro";
		else if (material.equals(Material.IRON_PICKAXE))
			causa = "uma Picareta de Ferro";
		else if (material.equals(Material.DIAMOND_PICKAXE))
			causa = "uma Picareta de Diamante";
		else if (material.equals(Material.WOOD_AXE))
			causa = "um Machado de Madeira";
		else if (material.equals(Material.STONE_AXE))
			causa = "um Machado de Pedra";
		else if (material.equals(Material.GOLD_AXE))
			causa = "um Machado de Ouro";
		else if (material.equals(Material.IRON_AXE))
			causa = "um Machado de Ferro";
		else if (material.equals(Material.DIAMOND_AXE))
			causa = "um Machado de Diamante";
		else if (material.equals(Material.COMPASS))
			causa = "uma Bussola";
		else if (material.equals(Material.MUSHROOM_SOUP))
			causa = "uma Sopa";
		else if (material.equals(Material.STICK))
			causa = "um Graveto";
		else if (material.equals(Material.AIR))
			causa = "o Punho";
		else 
			causa = "o Punho";
		return causa;
	}
	
	private String getCausa(DamageCause deathCause) {
		String cause = "";
		if (deathCause.equals(DamageCause.ENTITY_ATTACK)) {
			cause = "atacado por um monstro";
		} else if (deathCause.equals(DamageCause.CUSTOM)) {
			cause = "de uma forma não conhecida";
		} else if (deathCause.equals(DamageCause.BLOCK_EXPLOSION)) {
			cause = "explodido em mil pedaços";
		} else if (deathCause.equals(DamageCause.ENTITY_EXPLOSION)) {
			cause = "explodido por um monstro";
		} else if (deathCause.equals(DamageCause.CONTACT)) {
			cause = "espetado por um cacto";
		} else if (deathCause.equals(DamageCause.FALL)) {
			cause = "de queda";
		} else if (deathCause.equals(DamageCause.FALLING_BLOCK)) {
			cause = "stompado por um bloco";
		} else if ((deathCause.equals(DamageCause.FIRE_TICK)) || (deathCause.equals(DamageCause.FIRE))) {
			cause = "pegando fogo";
		} else if (deathCause.equals(DamageCause.LAVA)) {
			cause = "nadando na lava";
		} else if (deathCause.equals(DamageCause.LIGHTNING)) {
			cause = "atingido por um raio";
		} else if (deathCause.equals(DamageCause.MAGIC)) {
			cause = "atingido por uma magia";
		} else if (deathCause.equals(DamageCause.MELTING)) {
			cause = "atingido por um boneco de neve";
		} else if (deathCause.equals(DamageCause.POISON)) {
			cause = "envenenado";
		} else if (deathCause.equals(DamageCause.PROJECTILE)) {
			cause = "atingido por um projétil";
		} else if (deathCause.equals(DamageCause.STARVATION)) {
			cause = "de fome";
		} else if (deathCause.equals(DamageCause.SUFFOCATION)) {
			cause = "sufocado";
		} else if (deathCause.equals(DamageCause.SUICIDE)) {
			cause = "se suicidando";
		} else if (deathCause.equals(DamageCause.THORNS)) {
			cause = "encostando em alguns espinhos";
		} else if (deathCause.equals(DamageCause.VOID)) {
			cause = "pelo void";
		} else if (deathCause.equals(DamageCause.WITHER)) {
			cause = "pelo efeito do whiter";
		} else {
			cause = "por uma causa desconhecida";
		}
		return cause;
	}
}