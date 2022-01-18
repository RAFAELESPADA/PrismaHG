package com.br.gabrielsilva.prismamc.hungergames.manager.combatlog;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.br.gabrielsilva.prismamc.hungergames.HungerGames;

import lombok.Data;
import lombok.RequiredArgsConstructor;

public class CombatLogManager {

    private final static String COMBATLOG_PLAYER = "combatlog.player";
    private final static String COMBATLOG_EXPIRE = "combatlog.time";

    private final static long COMBATLOG_TIME = 5000L; // Miliseconds

    public static void newCombatLog(Player damager, Player damaged) {
        setCombatLog(damager, damaged);
        setCombatLog(damaged, damager);
    }

    public static void removeCombatLog(Player player) {
        HungerGames plugin = HungerGames.getInstance();

        if (player.hasMetadata(COMBATLOG_PLAYER))
            player.removeMetadata(COMBATLOG_PLAYER, plugin);
        
        if (player.hasMetadata(COMBATLOG_EXPIRE))
            player.removeMetadata(COMBATLOG_EXPIRE, plugin);
    }

    private static void setCombatLog(Player player1, Player player2) {
    	HungerGames plugin = HungerGames.getInstance();

        removeCombatLog(player1);

        player1.setMetadata(COMBATLOG_PLAYER, new FixedMetadataValue(plugin, player2.getName()));
        player1.setMetadata(COMBATLOG_EXPIRE, new FixedMetadataValue(plugin, System.currentTimeMillis()));
    }

    public static CombatLog getCombatLog(Player player) {
        String playerName = "";
        
        long time = 0L;
        
        if (player.hasMetadata(COMBATLOG_PLAYER))
            playerName = player.getMetadata(COMBATLOG_PLAYER).get(0).asString();
        
        if (player.hasMetadata(COMBATLOG_EXPIRE))
            time = player.getMetadata(COMBATLOG_EXPIRE).get(0).asLong();
        
        Player combatLogged = Bukkit.getPlayer(playerName);
        return new CombatLog(combatLogged, time);
    }

    @Data
    @RequiredArgsConstructor
    public static class CombatLog {
    	
        private final Player combatLogged;
        private final long time;

        public boolean isFighting() {
            return System.currentTimeMillis() < time + COMBATLOG_TIME;
        }
    }
}