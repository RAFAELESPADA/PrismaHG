package com.br.gabrielsilva.prismamc.hungergames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import com.br.gabrielsilva.prismamc.hungergames.manager.combatlog.CombatLogManager;
import com.br.gabrielsilva.prismamc.hungergames.manager.combatlog.CombatLogManager.CombatLog;

public class CombatLogListener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if (e.isCancelled()) {
			return;
		}
		
 		if ((e.getEntity() instanceof Player) && (e.getDamager() instanceof Player)) {
			 Player p = (Player) e.getEntity();
			 Player d = (Player) e.getDamager();
			 CombatLogManager.newCombatLog(p, d);
 		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent e) {
	    CombatLogManager.removeCombatLog(e.getEntity());
	}
	
	public static Player getLastHit(Player p) {
		Player finded = null;
		
	    CombatLog log = CombatLogManager.getCombatLog(p);
	    
	    if (log.isFighting()) {
	        Player combatLogger = log.getCombatLogged();
	        if (combatLogger != null) {
	            if (combatLogger.isOnline()) {
	        	    CombatLogManager.removeCombatLog(p);
	        	    finded = combatLogger;
	            }
	        }
	    }
	    
	    return finded;
	}
}