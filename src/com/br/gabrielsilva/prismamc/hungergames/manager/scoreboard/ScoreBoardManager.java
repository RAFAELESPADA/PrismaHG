package com.br.gabrielsilva.prismamc.hungergames.manager.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.scoreboard.Sidebar;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.scoreboard.animation.WaveAnimation;
import com.br.gabrielsilva.prismamc.commons.core.data.type.DataType;
import com.br.gabrielsilva.prismamc.commons.core.server.ServerType;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.manager.gamer.Gamer;

public class ScoreBoardManager {
	
	private WaveAnimation waveAnimation;
	private String text = "";
	
	public void init() {
		String title = "EVENTO";
		if (BukkitMain.getServerType() != ServerType.EVENTO) {
			title = "HG - #" + BukkitMain.getServerID();
		}
		
		waveAnimation = new WaveAnimation(" "+title+" ", "§f§l", "§b§l", "§3§l", 3);
		text = waveAnimation.next();
		
		Bukkit.getScheduler().runTaskTimer(HungerGames.getInstance(), new Runnable() {
			public void run() {
				text = waveAnimation.next();
				
				for (Player onlines : Bukkit.getOnlinePlayers()) {
					 if (onlines == null) {
						 continue;
					 }
					 if (!onlines.isOnline()) {
						 continue;
					 }
					 if (onlines.isDead()) {
						 continue;
					 }
				 	 Scoreboard score = onlines.getScoreboard();
					 if (score == null) {
						 continue;
					 }
					 Objective objective = score.getObjective(DisplaySlot.SIDEBAR);
					 if (objective == null) {
						 continue;
					 }
					 objective.setDisplayName(text);
				}
			}
		}, 40, 2L);
	}
	
	public void createSideBar(Player player) {
		BukkitPlayer bukkitPlayer = BukkitMain.getManager().getDataManager().getBukkitPlayer(player.getUniqueId());
		
		Sidebar sideBar = bukkitPlayer.getSideBar();
		if (sideBar == null) {
			bukkitPlayer.setSideBar(sideBar = new Sidebar(player.getScoreboard()));
			sideBar.show();
		}
		if (sideBar.isHided())
			return;
		
		sideBar.hide();
		sideBar.show();
		
		sideBar.setTitle("§b§l" + text);
		
		if (!HungerGames.getManager().getGameManager().isGaming()) {
			sideBar.setText(10, "");
			sideBar.setText(9, "");
			sideBar.setText(8, "§fJogadores: §b0");
			sideBar.setText(7, "");
			sideBar.setText(6, "§fClan: §cN/A");
			sideBar.setText(5, "§fHabilidade: §aNenhuma");
		} else {
			sideBar.setText(11, "");
			sideBar.setText(10, "§fTempo: §c0m 0s");
			sideBar.setText(9, "§fJogadores: §b0");
			sideBar.setText(8, "");
			sideBar.setText(7, "§fClan: §cN/A");
			sideBar.setText(6, "§fHabilidade: §aNenhuma");
			sideBar.setText(5, "§fKills: §e0");
		}
		sideBar.setText(4, "");
		sideBar.setText(3, "§fStatus: §7...");
		sideBar.setText(2, "");
		sideBar.setText(1, "§bkombopvp.net");
		updateScoreboard(player);
	}
	
	public void updateScoreboard(final Player player) {
		final BukkitPlayer bukkitPlayer = BukkitMain.getManager().getDataManager().getBukkitPlayer(player.getUniqueId());
		if (bukkitPlayer.getSideBar() == null) {
			return;
		}
		
		Sidebar sideBar = bukkitPlayer.getSideBar();
		
		if (sideBar == null) {
			return;
		}
		if (sideBar.isHided()) {
			return;
		}
		
		final Gamer gamer = HungerGames.getManager().getGamer(player.getUniqueId());
		
		if (!HungerGames.getManager().getGameManager().isGaming()) {
			sideBar.setText(9, getTimer());
			sideBar.setText(8, "§fJogadores: §6" + HungerGames.getManager().getGameManager().getAliveGamers().size());
			
			sideBar.setText(6, "Clan: " + (bukkitPlayer.getString(DataType.CLAN).equalsIgnoreCase("Nenhum") ? "§cN/A" 
					: "§a" + bukkitPlayer.getString(DataType.CLAN)));
			sideBar.setText(5, "§fHabilidade: §a" + gamer.getKit1());
			
		} else {
			sideBar.setText(10, "§fTempo: §c" + HungerGames.getManager().getTimerManager().getLastFormatted());
			sideBar.setText(9, "§fJogadores: §b" + HungerGames.getManager().getGameManager().getAliveGamers().size());
			
			sideBar.setText(7, "Clan: " + (bukkitPlayer.getString(DataType.CLAN).equalsIgnoreCase("Nenhum") ? "§cN/A" 
					: "§a" + bukkitPlayer.getString(DataType.CLAN)));
			sideBar.setText(6, "§fHabilidade: §a" + gamer.getKit1());
			sideBar.setText(5, "§fKills: §e" + gamer.getKills());
		}
		
		sideBar.setText(3, "§fStatus: " + getState());
	}

	private String getTimer() {
		String time = "";
		
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			time = "§fIniciando em: §b";
		} else if (HungerGames.getManager().getGameManager().isInvencibilidade()) {
			time = "§fInvencibilidade: §b";
		}
		
		return time + HungerGames.getManager().getTimerManager().getLastFormatted();
	}

	private String getState() {
		String state = "";
		
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			state = "§ePré-Jogo";
		} else if (HungerGames.getManager().getGameManager().isInvencibilidade()) {
			state = "§cInvencível";
		} else if (HungerGames.getManager().getGameManager().isGaming()) {
			state = "§aEm-jogo";
		}
		
		return state;
	}
}