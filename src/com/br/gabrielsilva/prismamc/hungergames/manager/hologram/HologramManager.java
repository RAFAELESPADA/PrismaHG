package com.br.gabrielsilva.prismamc.hungergames.manager.hologram;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.UpdateEvent;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.UpdateEvent.UpdateType;
import com.br.gabrielsilva.prismamc.commons.bukkit.hologram.Hologram;
import com.br.gabrielsilva.prismamc.commons.bukkit.hologram.HologramAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.hologram.types.ClanHologram;
import com.br.gabrielsilva.prismamc.commons.bukkit.hologram.types.PlayerRankingHologram;
import com.br.gabrielsilva.prismamc.commons.bukkit.hologram.types.SimpleHologram;
import com.br.gabrielsilva.prismamc.commons.core.server.ServerType;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import lombok.Getter;
import lombok.Setter;

public class HologramManager {

	@Getter @Setter
	private static SimpleHologram topKills, topWins;
	
	@Getter @Setter
	private static PlayerRankingHologram playerRanking;
	
	@Getter @Setter
	private static ClanHologram clanHologram;
	
	public static void init() {
		if (BukkitMain.getServerType() == ServerType.EVENTO) {
			return;
		}
		setPlayerRanking(new PlayerRankingHologram(HungerGames.getInstance(), "ranking"));
		setTopKills(new SimpleHologram(HungerGames.getInstance(), "KILLS", "hungergames", "kills"));
		setTopWins(new SimpleHologram(HungerGames.getInstance(), "WINS", "hungergames", "wins"));
		setClanHologram(new ClanHologram(HungerGames.getInstance(), "clans"));
		
		getPlayerRanking().create();
		getTopKills().create();
		getTopWins().create();
		getClanHologram().create();
		
		registerListener();
	}

	private static void registerListener() {
		Bukkit.getPluginManager().registerEvents(new Listener() {
			
			int minutos = 0;
			
			@EventHandler
			public void onUpdate(UpdateEvent event) {
				if (event.getType() == UpdateType.MINUTO) {
					minutos++;
					
					if (!HungerGames.getManager().getGameManager().isPreGame()) {
						for (Hologram holo : HologramAPI.getHolograms()) {
							 holo.despawn();
						}
						unregisterListener();
						return;
					}
					
					if (minutos == 10) {
						synchronized(this) {
							getPlayerRanking().updateValues();
							getTopKills().updateValues();
							getTopWins().updateValues();
							getClanHologram().updateValues();
						}
						
						minutos = 0;
					}
				}
			}

			private void unregisterListener() {
				HandlerList.unregisterAll(this);
			}
		}, HungerGames.getInstance());
	}
}