package com.br.gabrielsilva.prismamc.hungergames.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.server.ServerAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.gabrielsilva.prismamc.commons.bukkit.commands.register.StaffCommand;
import com.br.gabrielsilva.prismamc.commons.bukkit.manager.config.PluginConfig;
import com.br.gabrielsilva.prismamc.commons.core.command.CommandClass;
import com.br.gabrielsilva.prismamc.commons.core.command.CommandFramework.Command;
import com.br.gabrielsilva.prismamc.commons.core.group.Groups;
import com.br.gabrielsilva.prismamc.commons.core.utils.string.StringUtils;
import com.br.gabrielsilva.prismamc.commons.core.utils.system.DateUtils;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.api.InventoryStore;
import com.br.gabrielsilva.prismamc.hungergames.manager.gamer.Gamer;
import com.br.gabrielsilva.prismamc.hungergames.manager.kit.KitManager;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;

public class StafferCommand implements CommandClass {

	HashMap<String, InventoryStore> Skits = new HashMap<>();
	public static boolean quebrar = true, colocar = true, dropar = true;
	
	@Command(name = "setloc", groupsToUse= {Groups.DONO})
	public void setloc(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		
		if (args.length == 0) {
			commandSender.sendMessage("§cUtilize /setloc spawn");
			commandSender.sendMessage("§cUtilize /setloc hologramas <Wins/Kills/Ranking/Clans>");
			return;
		}
		Player player = commandSender.getPlayer();
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("spawn")) {
				PluginConfig.putNewLoc(HungerGames.getInstance(), "spawn", player);
				HungerGames.getManager().getGameManager().setSpawn(PluginConfig.getNewLoc(HungerGames.getInstance(), "spawn"));
				commandSender.sendMessage("§aSpawn setado!");
			} else {
				commandSender.sendMessage("§cUtilize /setloc spawn");
				commandSender.sendMessage("§cUtilize /setloc hologramas <Wins/Kills/Ranking/Clans>");
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("hologramas")) {
				if (isValidHologram(args[1])) {
					String name = args[1];
					PluginConfig.putNewLoc(HungerGames.getInstance(), "hologramas." + name.toLowerCase(), player);
					player.sendMessage("§aHolograma setado, é preciso reiniciar o servidor para aplicar o novo local.");
				} else {
					commandSender.sendMessage("§cHolograma inválido.");
				}
			} else {
				commandSender.sendMessage("§cUtilize /setloc spawn");
				commandSender.sendMessage("§cUtilize /setloc hologramas <Wins/Kills/Ranking/Clans>");
			}
		} else {
			commandSender.sendMessage("§cUtilize /setloc spawn");
			commandSender.sendMessage("§cUtilize /setloc hologramas <Wins/Kills/Ranking/Clans>");
		}
	}
	
	private boolean isValidHologram(String name) {
		if (name.equalsIgnoreCase("wins")) {
			return true;
		}
		if (name.equalsIgnoreCase("kills")) {
			return true;
		}
		if (name.equalsIgnoreCase("ranking")) {
			return true;
		}
		if (name.equalsIgnoreCase("clans")) {
			return true;
		}
		return false;
	}
	
	@Command(name = "hginfo", groupsToUse= {Groups.DONO})
	public void hginfo(BukkitCommandSender commandSender, String label, String[] args) {
		commandSender.sendMessage("");
		commandSender.sendMessage("§eKits dos jogadores:");
		commandSender.sendMessage("");
		
		HashMap<String, List<String>> hash = new HashMap<>();
		 
		for (Gamer gamers : HungerGames.getManager().getGamers().values()) {
			 if (!gamers.isJogando()) {
				 continue;
			 }
			 if (!gamers.getKit1().equalsIgnoreCase("Nenhum")) {
				 if (!hash.containsKey(gamers.getKit1())) {
					 List<String> list = new ArrayList<>();
					 list.add("§7" + gamers.getGamer().getName());
					 hash.put(gamers.getKit1(), list);
				 } else {
					 List<String> list = hash.get(gamers.getKit1());
					 list.add("§7" + gamers.getGamer().getName());
					 hash.put(gamers.getKit1(), list);
				 }
			 }
		}
		
		for (String kits : hash.keySet()) {
	         Collections.sort(hash.get(kits), String.CASE_INSENSITIVE_ORDER);
	         String string1 = StringUtils.formatArrayToString(hash.get(kits));
	         
	         if (string1.contains(",")) {
	        	 string1 = string1.replaceAll(",", "§f,");
	         }
	         
	         commandSender.sendMessage("§e" + kits + " [" + hash.get(kits).size() + "] " + string1);
		}
		
		hash.clear();
		hash = null;
		
		commandSender.sendMessage("");
		commandSender.sendMessage("§fJogadores jogando: §a" + HungerGames.getManager().getGameManager().getGamersVivos().size());
		commandSender.sendMessage("§fJogadores espectando: §a" + HungerGames.getManager().getGameManager().getGamersSpecs().size());
		commandSender.sendMessage("§fJogadores eliminados: §a" + HungerGames.getManager().getGameManager().getGamersEliminateds().size());
		commandSender.sendMessage("§fJogadores para relogar: §a" + HungerGames.getManager().getGameManager().getGamersToRelog().size());
		commandSender.sendMessage("§fJogadores online: §a" + Bukkit.getOnlinePlayers().size());
		commandSender.sendMessage("");
	}
	
	@Command(name = "toggle", groupsToUse= {Groups.MOD_PLUS})
	public void toggle(BukkitCommandSender commandSender, String label, String[] args) {
		if (args.length == 0) {
			commandSender.sendMessage("§cUse /toggle <kits/drops/break/place/feast/minifeast> <on/off>");
			commandSender.sendMessage("§cUse /toggle kit <kit> <on/off>");
    	} else if (args.length == 2) {
    		if (args[0].equalsIgnoreCase("feast")) {
				if (args[1].equalsIgnoreCase("on")) {
					if (!HungerGames.getManager().getStructureManager().getFeast().isUseFeast()) {
						Bukkit.broadcastMessage("§aFeast ativado!");
						HungerGames.getManager().getStructureManager().getFeast().setUseFeast(true);
					} else {
						commandSender.sendMessage("§cO Feast ja está ativado!");
					}
				} else if (args[1].equalsIgnoreCase("off")) {
					if (HungerGames.getManager().getStructureManager().getFeast().isUseFeast()) {
						Bukkit.broadcastMessage("§aFeast desativado!");
						HungerGames.getManager().getStructureManager().getFeast().setUseFeast(false);
					} else {
						commandSender.sendMessage("§cO Feast ja está desativado!");
					}
				}
    		} else if (args[0].equalsIgnoreCase("minifeast")) {
				if (args[1].equalsIgnoreCase("on")) {
					if (!HungerGames.getManager().getStructureManager().getMiniFeast().isUseMinifeast()) {
						Bukkit.broadcastMessage("§aMiniFeast ativado!");
						HungerGames.getManager().getStructureManager().getMiniFeast().setUseMinifeast(true);
					} else {
						commandSender.sendMessage("§cO MiniFeast ja está ativado!");
					}
				} else if (args[1].equalsIgnoreCase("off")) {
					if (HungerGames.getManager().getStructureManager().getMiniFeast().isUseMinifeast()) {
						Bukkit.broadcastMessage("§aMiniFeast desativado!");
						HungerGames.getManager().getStructureManager().getMiniFeast().setUseMinifeast(false);
					} else {
						commandSender.sendMessage("§cO MiniFeast ja está desativado!");
					}
				}
    		} else if (args[0].equalsIgnoreCase("kits")) {
				if (args[1].equalsIgnoreCase("on")) {
					if (KitManager.isAllKitsDesativados()) {
						Bukkit.broadcastMessage("§aKits ativados!");
						KitManager.setAllKitsDesativados(false);
					} else {
						commandSender.sendMessage("§cOs kits já estáo ativados.");
					}
				} else if (args[1].equalsIgnoreCase("off")) {
					if (!KitManager.isAllKitsDesativados()) {
						Bukkit.broadcastMessage("§cKits desativados.");
						KitManager.setAllKitsDesativados(true);
						for (Player on : Bukkit.getOnlinePlayers()) {
							 KitManager.removeKit(on, true);
						}
					} else {
						commandSender.sendMessage("§cOs kits já estáo desativados.");
					}
				}
			} else if (args[0].equalsIgnoreCase("drops")) {
				if (args[1].equalsIgnoreCase("on")) {
					if (dropar == false) {
						Bukkit.broadcastMessage("§aDrops ativados!");
						dropar = true;
					} else {
						commandSender.sendMessage("§cOs drops já estáo ativados.");
					}
				} else if (args[1].equalsIgnoreCase("off")) {
					if (dropar == true) {
						Bukkit.broadcastMessage("§cDrops desativado.");
						dropar = false;
					} else {
						commandSender.sendMessage("§cOs drops já estáo desativados.");
					}
				}
			} else if (args[0].equalsIgnoreCase("break")) {
				if (args[1].equalsIgnoreCase("on")) {
					if (quebrar == false) {
						Bukkit.broadcastMessage("§aBreak ativado!");
						quebrar = true;
					} else {
						commandSender.sendMessage("§cO break já está ativado.");
					}
				} else if (args[1].equalsIgnoreCase("off")) {
					if (quebrar == true) {
						Bukkit.broadcastMessage("§cBreak desativado.");
						quebrar = false;
					} else {
						commandSender.sendMessage("§cO break já está desativado.");
					}
				}
			} else if (args[0].equalsIgnoreCase("place")) {
				if (args[1].equalsIgnoreCase("on")) {
					if (colocar == false) {
						Bukkit.broadcastMessage("§aPlace ativado!");
						colocar = true;
					} else {
						commandSender.sendMessage("§cO place já está ativado.");
					}
				} else if (args[1].equalsIgnoreCase("off")) {
					if (colocar == true) {
						Bukkit.broadcastMessage("§cPlace desativado.");
						colocar = false;
					} else {
						commandSender.sendMessage("§cO place já está desativado.");
					}
				}
			} else {
				commandSender.sendMessage("§cUse /toggle <kits/drops/break/place/feast/minifeast> <on/off>");
				commandSender.sendMessage("§cUse /toggle kit <kit> <on/off>");
			}
		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("kit")) {
				String s = args[1].toLowerCase();
				if (KitManager.getKits().containsKey(s)) {
					s = KitManager.getKitInfo(s).getNome();
					if (args[2].equalsIgnoreCase("on")) {
						if (KitManager.getKitsDesativados().contains(s.toLowerCase())) {
							KitManager.getKitsDesativados().remove(s.toLowerCase());
							commandSender.sendMessage("§fO Kit " + s + " foi §a§lHABILITADO§f.");
						} else {
							commandSender.sendMessage("§cO Kit '§7" + s + "§c' ja está habilitado.");
						}
					} else if (args[2].equalsIgnoreCase("off")) {
						if (KitManager.getKitsDesativados().contains(s.toLowerCase())) {
							commandSender.sendMessage("§cO Kit '§7" + s + "§c' ja está desabilitado.");
						} else {
							KitManager.getKitsDesativados().add(s.toLowerCase());
							commandSender.sendMessage("§fO Kit " + s + " foi §c§lDESABILITADO§f.");
							for (Player on : Bukkit.getOnlinePlayers()) {
								 Gamer data = HungerGames.getManager().getGamer(on.getUniqueId());
								 if (!data.isJogando()) {
									 continue;
								 }
								 if (data.getKit1().equalsIgnoreCase(s)) {
									 KitManager.removeKit(on, true);
								 }
							}
						}
					}
				} else {
					commandSender.sendMessage("§cO KIT não existe.");
				}
			}
		} else {
			commandSender.sendMessage("§cUse /toggle <kits/drops/break/place/feast/minifeast> <on/off>");
			commandSender.sendMessage("§cUse /toggle kit <kit> <on/off>");
		}
	}
	
	@Command(name = "fkit", aliases= {"forcekit"}, groupsToUse= {Groups.MOD_PLUS})
	public void fkit(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		Player p = commandSender.getPlayer();
		if (args.length != 2) {
    		p.sendMessage("§cUse: /forcekit <Kit> <Jogador/Todos>");
    		return;
    	}
    	String kit = args[0].toLowerCase(), jogador = args[1];
    	if (!KitManager.getKits().containsKey(kit.toLowerCase())) {
    		p.sendMessage("§cEste kit não existe.");
    		return;
    	}
		kit = KitManager.getKitInfo(kit).getNome();
    	if ((jogador.equalsIgnoreCase("todos")) || (jogador.equals("*"))) {
    		KitManager.getKitInfo(kit).registerListener();
    		
    		for (Player on : Bukkit.getOnlinePlayers()) {
    			 KitManager.handleKitSelect(on, kit);
    		}
    		
    		if (!HungerGames.getManager().getGameManager().isPreGame()) {
    			for (Player on : HungerGames.getManager().getGameManager().getAliveGamers()) {
    				 KitManager.checkSurprise(on);
    				 KitManager.darItensKit1(on);
    			}
    		}
    		p.sendMessage("§aVocê setou o Kit: '§7" + kit + "§a' para todos os jogadores.");
    	} else {
    		Player t = Bukkit.getPlayer(jogador);
    		if (t == null) {
    			p.sendMessage("§cJogador offline.");
    			return;
    		}
    		
    		KitManager.handleKitSelect(t, kit);

    		if (!HungerGames.getManager().getGameManager().isPreGame()) {
    			KitManager.checkSurprise(t);
    			KitManager.darItensKit1(t);
    		}
    		
    		p.sendMessage("§aVocê setou o Kit: '§7" + kit + "§a' para o " + t.getName());
    	}
	}

	@Command(name = "skit", aliases= {"simplekit"}, groupsToUse= {Groups.TRIAL})
	public void skit(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		Player p = commandSender.getPlayer();
		if (args.length == 0) {
			p.sendMessage("§cUse: /skit criar <Nome>");
			p.sendMessage("§cUse: /skit aplicar <Nome> <Nick/Todos/Distancia>");
			p.sendMessage("§cUse: /skit lista");
    		return;
    	} else if (args.length == 1) {
    		if (args[0].equalsIgnoreCase("lista")) {
    			if (Skits.size() == 0) {
    				p.sendMessage("§cNenhum SKIT criado.");
    				return;
    			}
    			String skits = "";
    			for (String kits : Skits.keySet()) {
    				 if (skits.equals("")) {
    					 if (Skits.size() == 1) {
    						 skits = kits;
    						 break;
    					 } else {
    						 skits = kits;
    					 }
    				 } else {
    					 skits = skits + "," + kits;
    				 }
    			}	
    			p.sendMessage("§aSkits: §7" + skits);
    		} else {
    			p.sendMessage("§cUse: /skit criar <Nome>");
    			p.sendMessage("§cUse: /skit aplicar <Nome> <Nick/Todos/Distancia>");
    			p.sendMessage("§cUse: /skit lista");
    		}
    	} else if (args.length == 2) {
    		if (!args[0].equalsIgnoreCase("criar")) {
    			p.sendMessage("§cUse: /skit criar <Nome>");
    			p.sendMessage("§cUse: /skit aplicar <Nome> <Nick/Todos/Distancia>");
    			p.sendMessage("§cUse: /skit lista");
    			return;
    		}
    		String kit = args[1];
    		if (Skits.containsKey(kit)) {
    			p.sendMessage("§cEste Skit já existe.");
    			return;
    		}
    		Skits.put(kit, new InventoryStore(kit, p.getInventory().getArmorContents(), p.getInventory().getContents(), (List<PotionEffect>) p.getActivePotionEffects()));
    		p.sendMessage("§aVocê criou o Skit: §7" + kit + "§a.");
    	} else if (args.length == 3) {
    		if (!args[0].equalsIgnoreCase("aplicar")) {
    			p.sendMessage("§cUse: /skit criar <Nome>");
    			p.sendMessage("§cUse: /skit aplicar <Nome> <Nick/Todos/Distancia>");
    			p.sendMessage("§cUse: /skit lista");
    			return;
    		}
    		String kit = args[1];
    		if (!Skits.containsKey(kit)) {
    			p.sendMessage("§cEste Skit não existe.");
    			return;
    		}
			InventoryStore inv = Skits.get(kit);
    		if ((args[2].equalsIgnoreCase("todos")) || (args[2].equalsIgnoreCase("*"))) {
    			for (Player ons : Bukkit.getOnlinePlayers()) {
    				 if (HungerGames.getManager().getGamer(ons.getUniqueId()).isJogando()) {
    					 ons.getPlayer().setItemOnCursor(new ItemStack(0));
    					 ons.getInventory().setArmorContents(inv.getArmor());
    					 ons.getInventory().setContents(inv.getInv());
    					 ons.addPotionEffects(inv.getPotions());
    					 ons.sendMessage("§aVocê recebeu o Skit: §7" + inv.getNome() + "§a.");
    					 
    					 if (ons.getInventory().contains(Material.WOOL)) {
    						 ons.getInventory().setItem(ons.getInventory().first(Material.WOOL), null);
    						 KitManager.darItensKit1(ons);
        					 if (!PlayerAPI.isFull(ons.getInventory()))
        						 ons.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
    					 }
    				 }
    			}
    			p.sendMessage("§aVocê aplicou o Skit: '§7" + kit + "§a' para todos os jogadores vivos.");
    		} else if (StringUtils.isInteger(args[2])) {
    			int distancia = Integer.valueOf(args[2]);
    			if (distancia >= 501) {
    				p.sendMessage("§cDistancia muito grande.");
    				return;
    			}
				for (Entity ent : p.getNearbyEntities(distancia, 130, distancia)) {
					 if (!(ent instanceof Player))
						 continue;
					 
				  	 Player ons = (Player) ent;
				  	 if (HungerGames.getManager().getGamer(ons.getUniqueId()).isJogando()) {
    					 ons.getPlayer().setItemOnCursor(new ItemStack(0));
    					 ons.getInventory().setArmorContents(inv.getArmor());
    					 ons.getInventory().setContents(inv.getInv());
    					 ons.addPotionEffects(inv.getPotions());
    					 ons.sendMessage("§aVocê recebeu o Skit: §7" + inv.getNome() + "§a.");
    					 
    					 if (ons.getInventory().contains(Material.WOOL)) {
    						 ons.getInventory().setItem(ons.getInventory().first(Material.WOOL), null);
    						 KitManager.darItensKit1(ons);
        					 if (!PlayerAPI.isFull(ons.getInventory()))
        						 ons.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
    					 }
					 }
				}
    			p.sendMessage("§aVocê aplicou o Skit: '§7" + kit + "§a' para todos os jogadores vivos no raio de §7" + distancia + " §ablocos.");
    		} else {
    			Player t = Bukkit.getPlayer(args[2]);
    			if (t == null) {
    				p.sendMessage("§cJogador offline");
    				return;
    			}
				t.getPlayer().setItemOnCursor(new ItemStack(0));
				t.getInventory().setArmorContents(inv.getArmor());
				t.getInventory().setContents(inv.getInv());
				t.addPotionEffects(inv.getPotions());
				if (t.getInventory().contains(Material.WOOL)) {
					t.getInventory().setItem(t.getInventory().first(Material.WOOL), null);
					KitManager.darItensKit1(t);
				}
				
				if (!PlayerAPI.isFull(t.getInventory()))
					t.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
				
				t.sendMessage("§aVocê recebeu o Skit: §7" + inv.getNome() + "§a.");
				p.sendMessage("§aVocê aplicou o Skit: '§7" + kit + "§a' para o jogador: §7" + t.getName() + "§a.");
    		}
    	} else {
    		p.sendMessage("§cUse: /skit criar <Nome>");
    		p.sendMessage("§cUse: /skit aplicar <Nome> <Nick/Todos/Distancia>");
    		p.sendMessage("§cUse: /skit lista");
    	}	
	}
	
	@Command(name = "start", aliases= {"iniciar"}, groupsToUse= {Groups.MOD})
	public void start(BukkitCommandSender commandSender, String label, String[] args) {
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			commandSender.sendMessage("§6§lTORNEIO §fVocê iniciou partida!");
			HungerGames.getManager().getGameManager().startGame();
			if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
				ServerAPI.warnStaff("§7["+StaffCommand.getRealNick(commandSender.getPlayer()) + " iniciou a partida]", Groups.ADMIN);
			}
		} else {
			commandSender.sendMessage("§cO jogo já iniciou.");
		}
	}
	
	@Command(name = "tempo", aliases= {"t"}, groupsToUse= {Groups.MOD})
	public void tempo(BukkitCommandSender commandSender, String label, String[] args) {
		if (args.length != 1) {
			commandSender.sendMessage("§cUtilize o comando: /tempo (segundos)");
			return;
		}
		if (!StringUtils.isInteger(args[0])) {
			commandSender.sendMessage("§cUtilize o comando: /tempo (segundos)");
			return;
		}
		int segundos = Integer.valueOf(args[0]);
		if (segundos <= 0) {
			commandSender.sendMessage("§cUse um tempo maior que 0.");
			return;
		}
		HungerGames.getManager().getTimerManager().setLastFormatted(DateUtils.formatarSegundos2(segundos));
		HungerGames.getManager().getTimerManager().setTempo(segundos);
		Bukkit.broadcastMessage("§aO tempo foi alterado para §7" + DateUtils.formatarTempo(segundos));
		if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
			ServerAPI.warnStaff("§7["+StaffCommand.getRealNick(commandSender.getPlayer()) + " alterou o tempo da partida]", Groups.ADMIN);
		}
	}
	
	@Command(name = "ffeast", aliases= {"forcefeast"}, groupsToUse= {Groups.MOD_PLUS})
	public void ffeast(BukkitCommandSender commandSender, String label, String[] args) {
		Location forceFeast = HungerGames.getManager().getStructureManager().getValidLocation();
		HungerGames.getManager().getStructureManager().getFeast().setFeastLocation(forceFeast);
		
		HungerGames.getManager().getGameManager().spawnar("feast", forceFeast, true);
		
    	Bukkit.broadcastMessage("§e§lFEAST §fO feast §a§lSPAWNOU §fem: (x " + forceFeast.getBlockX() + ", y " + forceFeast.getBlockY() + ", z " + forceFeast.getBlockZ() +  ")");
	}
	
	@Command(name = "arena", groupsToUse= {Groups.MOD})
	public void arena(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		Player player = commandSender.getPlayer();
		if (args.length == 0) {
			player.sendMessage("");
    		player.sendMessage("§cUse: /arena <largura> <altura>");
    		player.sendMessage("§cUse: /arena limpar");
    		player.sendMessage("§cUse: /arena final");
    		player.sendMessage("");
		} else if (args.length == 1) {
    		if (args[0].equalsIgnoreCase("limpar")) {
    			limparArena(player);
    		} else if (args[0].equalsIgnoreCase("final")) {
    			HungerGames.getManager().getGameManager().spawnarArenaFinal();
    		} else {
    			player.chat("/arena");
    		}
		} else if (args.length == 2) {
    		String largura = args[0], altura = args[1];
    		if ((!StringUtils.isInteger(largura)) || (!StringUtils.isInteger(altura))) {
    			player.chat("/arena");
    			return;
    		}
    		criarArena(player, Integer.valueOf(largura), Integer.valueOf(altura));
    		player.sendMessage("§aVocê criou uma arena: §7" + largura + "§ax§7" + altura + "§ax§7" + largura + "§a.");
		} else {
			player.chat("/arena");
		}
	}
	
	private Location ponto_baixo, ponto_alto;
	
	public void criarArena(Player p, int largura, int altura) {
		ServerAPI.warnStaff("§7[" +StaffCommand.getRealNick(p) + " está criando uma arena " + largura + "x" + altura + "]", Groups.ADMIN);
		
		p.sendMessage("§6§lARENA §fVocê está criando uma arena, aguarde até completar a construção.");
		
		Location loc = p.getLocation();
        List<Location> cuboid = new ArrayList<>();
        
    	for (int bX = -largura; bX <= largura; bX++) {
             for (int bZ = -largura; bZ <= largura; bZ++) {
                  for (int bY = -1; bY <= altura; bY++) {
         	           if (bY == -1) {
        	               cuboid.add(loc.clone().add(bX, bY, bZ));
                       } else if ((bX == -largura) || (bZ == -largura) || (bX == largura) || (bZ == largura)) {
                           cuboid.add(loc.clone().add(bX, bY, bZ));
                       }
                  }
             }
    	}
    	
		new BukkitRunnable() {
			
	    	final int blocksPerTick = largura >= 30 ? 60 : 100;
	    	boolean ended = false;
		  	int blockAtual = 0, 
		  			max = cuboid.size() + 10;
			
		  	public void run() {
		  		if (ended) {
		  			cancel();
		  			if (p.isOnline()) {
		  				p.sendMessage("§6§lARENA §fArena construída com sucesso!");
		  			}
		  			cuboid.clear();
		  			return;
		  		}
			
		  		if (blockAtual >= max) {
		  			ended = true;
		  			return;
		  		}
			
		  		for (int i = 0; i < blocksPerTick; i++) {
		  			 try {
		  				 Location location = cuboid.get(blockAtual + i);
		  				 setAsyncBlock(location.getWorld(), location, Material.BEDROCK.getId());
		  			 } catch (IndexOutOfBoundsException e) {
		  				 continue; 
		  			 } catch (NullPointerException e) {
		  				 continue;
		  			 }
		  		}
		  		this.blockAtual+=55;
		  	}
		}.runTaskTimer(HungerGames.getInstance(), 1L, 1L);
         
		Location PB = loc.clone().add(largura - 1, 0, largura - 1);
        ponto_baixo = PB;
        Location PA = loc.clone().subtract(largura - 1, 0, largura - 1);
        PA.add(0, altura - 1, 0);
        ponto_alto = PA;
	}
	
	public void limparArena(Player p) {
		if (ponto_alto == null) {
			p.sendMessage("§cnão existe uma arena para limpar.");
			return;
		}
		
		for (Location location : getLocationsFromTwoPoints(ponto_baixo, ponto_alto)) {
			 setAsyncBlock(location.getWorld(), location, Material.AIR.getId());
		}
        
		p.sendMessage("§aArena limpa");
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
	
	public List<Location> getLocationsFromTwoPoints(Location location1, Location location2) {
		List<Location> locations = new ArrayList<>();
		int topBlockX = (location1.getBlockX() < location2.getBlockX() ? location2.getBlockX() : location1.getBlockX());
		int bottomBlockX = (location1.getBlockX() > location2.getBlockX() ? location2.getBlockX() : location1.getBlockX());
		int topBlockY = (location1.getBlockY() < location2.getBlockY() ? location2.getBlockY() : location1.getBlockY());
		int bottomBlockY = (location1.getBlockY() > location2.getBlockY() ? location2.getBlockY() : location1.getBlockY());
		int topBlockZ = (location1.getBlockZ() < location2.getBlockZ() ? location2.getBlockZ() : location1.getBlockZ());
		int bottomBlockZ = (location1.getBlockZ() > location2.getBlockZ() ? location2.getBlockZ() : location1.getBlockZ());
		for (int x = bottomBlockX; x <= topBlockX; x++)
		for (int z = bottomBlockZ; z <= topBlockZ; z++) 
		for (int y = bottomBlockY; y <= topBlockY; y++) 
		     locations.add(new Location(location1.getWorld(), x, y, z));
		return locations;
	}
	
	public List<Block> getBlocks(Location location1, Location location2) {
		List<Block> blocks = new ArrayList<>();
		for (Location loc : getLocationsFromTwoPoints(location1, location2)) {
			 Block b = loc.getBlock();
			 if (!b.getType().equals(Material.AIR))
			     blocks.add(b);
		}
		return blocks;
	}
}