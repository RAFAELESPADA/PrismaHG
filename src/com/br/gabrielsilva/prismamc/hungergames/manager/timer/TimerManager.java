package com.br.gabrielsilva.prismamc.hungergames.manager.timer;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.UpdateEvent;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.UpdateEvent.UpdateType;
import com.br.gabrielsilva.prismamc.commons.core.server.ServerType;
import com.br.gabrielsilva.prismamc.commons.core.server.types.Stages;
import com.br.gabrielsilva.prismamc.commons.core.utils.system.DateUtils;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TimerManager {

	private int tempo = 300, jogadoresMinimos = 3;
	private String lastFormatted = "";
	
	public void init() {
		if (BukkitMain.getServerType() == ServerType.EVENTO) {
			jogadoresMinimos = 15;
			tempo = 120;
		}
		
		Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
			
			@EventHandler
			public void update(UpdateEvent event) {
				if (event.getType() != UpdateType.SEGUNDO) {
					return;
				}
				onSecond();
			}
			
		}, HungerGames.getInstance());
	}
	
	private void onSecond() {
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			preGame();
			HungerGames.getManager().getGameManager().updateHGStats();
		} else if (HungerGames.getManager().getGameManager().isInvencibilidade()) {
			invencibilidade();
			HungerGames.getManager().getGameManager().updateHGStats();
		} else if (HungerGames.getManager().getGameManager().isGaming()) {
			gaming();
			HungerGames.getManager().getGameManager().updateHGStats();
		}
		
		lastFormatted = DateUtils.formatarSegundos2(getTempo());
	}
	
	private void preGame() {
		Bukkit.getWorlds().get(0).setTime(0);
		Bukkit.getWorlds().get(0).setStorm(false);
		Bukkit.getWorlds().get(0).setThundering(false);
		
		for (Player ons : Bukkit.getOnlinePlayers()) {
			 HungerGames.getManager().getScoreboardManager().updateScoreboard(ons);
			 HungerGames.getManager().getGameManager().checkBorder(ons, 100);
		}
		
		if (HungerGames.getManager().getGameManager().getAliveGamers().size() < jogadoresMinimos) {
			if (BukkitMain.getServerType() == ServerType.EVENTO) {
				tempo = 120;
			} else {
				tempo = 300;
			}
			return;
		}
		if (tempo == 0) {
			HungerGames.getManager().getGameManager().startGame();
			return;
		}
		checkMSG();
		tempo--;
	}
	
	private void invencibilidade() {
		if (tempo == 0) {
			HungerGames.getManager().getGameManager().setEstagio(Stages.JOGANDO);
			
			lastFormatted = DateUtils.formatarSegundos2(getTempo());
			
			for (Player ons : Bukkit.getOnlinePlayers()) {
				 HungerGames.getManager().getScoreboardManager().createSideBar(ons);
			     ons.playSound(ons.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
			}
			Bukkit.broadcastMessage("§aA invencibilidade acabou, que vença o melhor!");
			HungerGames.getManager().getGameManager().checkWin();
			tempo = 120;
			return;
		}
		
		checkMSG();
		for (Player ons : Bukkit.getOnlinePlayers()) {
			 HungerGames.getManager().getScoreboardManager().updateScoreboard(ons);
		}
		tempo--;
	}
	
	private void gaming() {
		tempo++;
		
		if (tempo > 5 && tempo % 360 == 0) {
			if (HungerGames.getManager().getStructureManager().getMiniFeast().isUseMinifeast()) {
				HungerGames.getManager().getStructureManager().getMiniFeast().createMinifeast();
			}
		}
		
		if (tempo == 900) {
			if (HungerGames.getManager().getStructureManager().getFeast().isUseFeast()) {
				HungerGames.getManager().getStructureManager().getFeast().createFeast();
			}
		}

		if (tempo >= 2400 && tempo < 2700 && tempo % 60 == 0) {
			Bukkit.broadcastMessage("§aA arena final irá spawnar em " + DateUtils.formatarSegundos(((2700) - tempo)));
		} else if (tempo == 2700) {
			HungerGames.getManager().getGameManager().spawnarArenaFinal();
		} else if (tempo == 50 * 60) {
			Bukkit.broadcastMessage("§aA partida irá acabar em: 10 minutos.");
		} else if (tempo == 55 * 60) {
			Bukkit.broadcastMessage("§aA partida irá acabar em: 5 minutos.");
		} else if (tempo == 57 * 60) {
			Bukkit.broadcastMessage("§aA partida irá acabar em: 3 minutos.");
		} else if (tempo == 58 * 60) {
			Bukkit.broadcastMessage("§aA partida irá acabar em: 2 minutos.");
		} else if (tempo == 59 * 60) {
			Bukkit.broadcastMessage("§aA partida irá acabar em: 1 minuto.");
		} else if (tempo == 60 * 60) {
			Bukkit.broadcastMessage("§aEscolhendo o jogador com mais kills...");
			escolherVencedor();
		    return;
		}
		
		for (Player ons : Bukkit.getOnlinePlayers()) {
			 if (ons != null && ons.isOnline()) {
				 HungerGames.getManager().getGameManager().checkBorder(ons, 501);
				 
				 HungerGames.getManager().getScoreboardManager().updateScoreboard(ons);
			 }
		}
	}
	
	private void escolherVencedor() {
		UUID ganhador = null;
		int kills = -1;
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			 int killsPlayer = HungerGames.getManager().getGamer(player.getUniqueId()).getKills();
			 if (killsPlayer > kills) {
				 ganhador = player.getUniqueId();
				 kills = killsPlayer;
			 }
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			 if (!player.getUniqueId().equals(ganhador)) {
				 HungerGames.getManager().getGameManager().setEspectador(player);
			 }
		}
		
		HungerGames.runLater(() -> {
			HungerGames.getManager().getGameManager().checkWin();
		}, 20);
	}
	
	private void checkMSG() {
		if (((tempo >= 10 ? 1 : 0) & (tempo % 60 == 0 ? 1 : 0)) != 0) {
			  Bukkit.broadcastMessage(getMensagem(tempo));
			  Som(Sound.CLICK);
		} else if (tempo == 30) {
			Bukkit.broadcastMessage(getMensagem(tempo));
		    Som(Sound.CLICK);
		} else if (tempo == 15) {
		    Bukkit.broadcastMessage(getMensagem(tempo));
		    Som(Sound.CLICK);
		} else if (tempo == 10) {
			Bukkit.broadcastMessage(getMensagem(tempo));
		    Som(Sound.CLICK);
		} else if (tempo <= 5) {
			Bukkit.broadcastMessage(getMensagem(tempo));
		    Som(Sound.NOTE_PLING);
		}
	}
	
	private String getMensagem(int tempo) {
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			return "§aPartida iniciando em §7" + DateUtils.formatarTempo(tempo);
		} else if (HungerGames.getManager().getGameManager().isInvencibilidade()) {
			return "§aInvencibilidade acabando em: §7" + DateUtils.formatarTempo(tempo);
		}
		return "";
	}
	
	public void Som(Sound som) {
    	for (Player p : Bukkit.getOnlinePlayers()) {
		     p.playSound(p.getLocation(), som, 1.0F, 1.0F);
    	}
    }
}