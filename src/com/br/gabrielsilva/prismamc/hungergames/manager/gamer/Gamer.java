package com.br.gabrielsilva.prismamc.hungergames.manager.gamer;

import org.bukkit.entity.Player;

import com.br.gabrielsilva.prismamc.commons.core.utils.string.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Gamer {

	private Player gamer;
	private String kit1;
	private Long cooldown1;
	private boolean jogando, eliminado, relogar, online;
	private int kills, secondsToRelog;
	
	public Gamer(Player gamer) {
		setGamer(gamer);
		setKit1("Nenhum");
		
		setCooldown1(0L);
		setKills(0);
		setSecondsToRelog(0);
		setEliminado(false);
		setJogando(true);
		setEliminado(false);
		setRelogar(false);
		setOnline(true);
	}
	
	public void addSeconds() {
		this.secondsToRelog++;
	}
	
	public void addKill() {
		kills+=1;
	}
	
	public boolean containsKit(String kit) {
		if (kit1.equalsIgnoreCase(kit)) {
			return true;
		}
		return false;
	}
	
	public void addCooldown(String kit, int segundos) {
		cooldown1 = System.currentTimeMillis() + (segundos * 1000);
	}
	
	public boolean hasCooldown(String kit) {
		if (cooldown1 > System.currentTimeMillis()) {
			gamer.sendMessage("§cAguarde " + StringUtils.toMillis((double) (cooldown1 - System.currentTimeMillis()) / 10 / 100) + " segundos para usar a habilidade novamente.");
			return true;
		}
		return false;
	}

	public void handleJoin(Player gamer2) {
		setGamer(gamer2);
		setOnline(true);
	}
}