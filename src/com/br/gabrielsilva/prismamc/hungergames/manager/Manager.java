package com.br.gabrielsilva.prismamc.hungergames.manager;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

import com.br.gabrielsilva.prismamc.hungergames.manager.game.GameManager;
import com.br.gabrielsilva.prismamc.hungergames.manager.gamer.Gamer;
import com.br.gabrielsilva.prismamc.hungergames.manager.scoreboard.ScoreBoardManager;
import com.br.gabrielsilva.prismamc.hungergames.manager.structures.StructureManager;
import com.br.gabrielsilva.prismamc.hungergames.manager.timer.TimerManager;

import lombok.Getter;

@Getter
public class Manager {

	private GameManager gameManager;
	private TimerManager timerManager;
	private ScoreBoardManager scoreboardManager;
	private ConcurrentHashMap<UUID, Gamer> gamers;
	private StructureManager structureManager;
	
	public Manager() {
		this.gamers = new ConcurrentHashMap<>();
		this.gameManager = new GameManager();
		
		this.timerManager = new TimerManager();
		this.scoreboardManager = new ScoreBoardManager();
		
		this.structureManager = new StructureManager();
	}

	public ConcurrentHashMap<UUID, Gamer> getGamers() {
		return gamers;
	}
	
	public void clearGamerData(Player gamer) {
		if (gamers.containsKey(gamer.getUniqueId())) {
			gamers.remove(gamer.getUniqueId());
		}
	}
	
	public void prepareData(Player gamer) {
		if (!gamers.containsKey(gamer.getUniqueId())) {
			gamers.put(gamer.getUniqueId(), new Gamer(gamer));
		} else {
			gamers.get(gamer.getUniqueId()).handleJoin(gamer);
		}
	}
	
	public Gamer getGamer(UUID uuid) {
		return gamers.get(uuid);
	}

	public void removeGamer(UUID uuid) {
		gamers.remove(uuid);
	}
}