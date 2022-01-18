package com.br.gabrielsilva.prismamc.hungergames.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.VanishManager;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.title.TitleAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.gabrielsilva.prismamc.commons.core.command.CommandClass;
import com.br.gabrielsilva.prismamc.commons.core.command.CommandFramework.Command;
import com.br.gabrielsilva.prismamc.commons.core.command.CommandFramework.Completer;
import com.br.gabrielsilva.prismamc.commons.core.group.Groups;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;
import com.br.gabrielsilva.prismamc.hungergames.manager.kit.KitManager;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerCommand implements CommandClass {

	@Command(name = "specs", groupsToUse = {Groups.SAPPHIRE})
	public void specs(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		Player player = commandSender.getPlayer();
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			player.sendMessage("§cO jogo ainda não iniciou.");
    		return;
    	}
		
		if (args.length != 1) {
			commandSender.sendMessage("§cUse /specs <on/off>");
			return;
		}
		
		if (args[0].equalsIgnoreCase("on")) {
			int vendo = 0;
			for (Player onlines : Bukkit.getOnlinePlayers()) {
				 if ((VanishManager.inAdmin(onlines)) || (VanishManager.isInvisivel(onlines))) {
					 continue;
				 }
				 if (!HungerGames.getManager().getGamer(onlines.getUniqueId()).isJogando()) {
					 vendo++;
					 player.showPlayer(onlines);
				 }
			}
			
			if (vendo != 0) {
			    player.sendMessage("§aVoce esta vendo todos os espectadores.");
			} else {
				player.sendMessage("§cNenhum espectador encontrado.");
			}
		} else if (args[0].equalsIgnoreCase("off")) {
			int escondidos = 0;
			for (Player onlines : Bukkit.getOnlinePlayers()) {
				 if (!HungerGames.getManager().getGamer(onlines.getUniqueId()).isJogando()) {
					 escondidos++;
					 player.hidePlayer(onlines);
				 }
			}
			
			if (escondidos != 0) {
			    player.sendMessage("§aEspectadores escondidos.");
			} else {
				player.sendMessage("§cNenhum espectador encontrado.");
			}
		} else {
			commandSender.sendMessage("§cUse /specs <on/off>");
		}
	}
	
	@Command(name = "desistir", groupsToUse = {Groups.SAPPHIRE})
	public void desistir(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		Player player = commandSender.getPlayer();
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			player.sendMessage("§cO jogo ainda não iniciou.");
    		return;
    	}
		
		if (HungerGames.getManager().getGamer(player.getUniqueId()).isJogando()) {
			player.sendMessage("§aVoce desistiu da partida!");
			
			PlayerAPI.dropItems(player, player.getLocation());
			
			HungerGames.getManager().getGameManager().setEspectador(player, false);
			
			HungerGames.runLater(() -> {
				Bukkit.broadcastMessage("§b" + player.getName() + " ("+HungerGames.getManager().getGamer(player.getUniqueId()).getKit1()+") desistiu da partida.\n" + 
						"§c" + HungerGames.getManager().getGameManager().getAliveGamers().size() + " jogadores restantes.");
			}, 2);
		} else {
			player.sendMessage("§cVoce nao esta jogando.");
		}
	}
	
	@Command(name = "feast")
	public void feast(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		Player player = commandSender.getPlayer();
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			player.sendMessage("§cO jogo ainda nao iniciou.");
    		return;
    	}
    	if (!player.getInventory().contains(Material.COMPASS)) {
			player.sendMessage("§cVoc§ precisa ter uma bussola no inventario.");
    		return;
    	}
    	if (HungerGames.getManager().getStructureManager().getFeast().getFeastLocation() == null) {
    		player.sendMessage("§cO feast ainda nao spawnou.");
    		return;
    	}
    	player.setCompassTarget(HungerGames.getManager().getStructureManager().getFeast().getFeastLocation());
    	player.sendMessage("§eBússola apontando para o feast.");
	}
	
	@Command(name = "kit")
	public void kit(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		Player player = commandSender.getPlayer();
		if (args.length == 0) {
    		List<String> kits =
    				KitManager.getPlayerKits(player).stream().map(Kit::getNome).collect(Collectors.toList());
    		
    		TextComponent tagsMessage = new TextComponent("§aSeus Kits (" + kits.size() + "): \n\n");
    		for (int i = 0; i < kits.size(); i++) {
    			 String kit = kits.get(i);
    			 tagsMessage.addExtra(i == 0 ? "" : ", ");
    			 tagsMessage.addExtra(buildKitComponent(KitManager.getKitInfo(kit.toLowerCase())));
    		}
    		player.spigot().sendMessage(tagsMessage);
    		player.sendMessage("");
		} else if (args.length == 1) {
    		String kit = args[0].toLowerCase();
    		if (!KitManager.getKits().containsKey(kit)) {
    			player.sendMessage("§cEste kit não existe.");
    			return;
    		}
			if (KitManager.isAllKitsDesativados()) {
				player.sendMessage("§cTodos os kits estão desativados.");
				return;
			}
			if (KitManager.getKitsDesativados().contains(kit.toLowerCase())) {
				player.sendMessage("§cEste Kit esta desativado.");
			    return;
			}
			
			if (!KitManager.hasPermissionKit(player, kit, true)) {
				return;
			}
			
			kit = KitManager.getKitInfo(kit).getNome();
			if (HungerGames.getManager().getGameManager().isPreGame()) {
				KitManager.handleKitSelect(player, kit);
    			player.sendMessage("§aVoce selecionou o Kit: §f" + kit);
    			TitleAPI.enviarTitulos(player, "§b" + kit, "§fselecionado!", 1, 1, 5);
			} else {
				if (HungerGames.isVip(player)) {
					if (HungerGames.getManager().getTimerManager().getTempo() <= 300) {
		 				if (HungerGames.getManager().getGamer(player.getUniqueId()).getKit1().equalsIgnoreCase("Nenhum")) {
		 					KitManager.handleKitSelect(player, kit);

		 	    			player.sendMessage("§aVoce selecionou o Kit: §f" + kit);
		 	    			TitleAPI.enviarTitulos(player, "§b" + kit, "§fselecionado!", 1, 1, 5);
		 	    			
			    			KitManager.checkSurprise(player);
			    			KitManager.darItensKit1(player);
		 				} else {
		 					player.sendMessage("§cVoce ja possue um kit!");
		 				}
					} else {
						player.sendMessage("§cO tempo para pegar KIT sendo VIP esgotou.");
					}
				} else {
					player.sendMessage("§cVoce nao tem permissao para pegar um Kit após o jogo ter iniciado!");
				}
			}
		}
	}
	
	@Completer(name = "kit", aliases= {"kit2"})
	public List<String> kitcompleter(BukkitCommandSender sender, String label, String[] args) {
		List<String> list = new ArrayList<>();
		Player p = sender.getPlayer();
		if (args.length == 1) {
			String search = args[0].toLowerCase();
			for (Kit kit : KitManager.getAllKits()) {
				 if (kit.getNome().toLowerCase().startsWith(search)) {
					 list.add(kit.getNome());
				 }
			}
		}
		p = null;
		return list;
	}
	
	private BaseComponent buildKitComponent(Kit kit) {
		BaseComponent baseComponent = new TextComponent("§a" + kit.getNome());
		BaseComponent descComponent = new TextComponent("§eDescrição: \n");
		for (String desc : kit.getDescrição())
			descComponent.addExtra(desc.replaceAll("&", "§") + "\n");

		baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new BaseComponent[] { descComponent, new TextComponent("\n"), new TextComponent("§aClique para selecionar!") }));
		baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kit " + kit.getNome()));
		return baseComponent;
	}
}