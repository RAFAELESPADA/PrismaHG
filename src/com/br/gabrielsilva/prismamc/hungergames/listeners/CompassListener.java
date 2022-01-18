package com.br.gabrielsilva.prismamc.hungergames.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.VanishManager;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.menus.AliveGamers;

public class CompassListener implements Listener {

	@EventHandler
    public void onCompass(PlayerInteractEvent event) {
		if (HungerGames.getManager().getGameManager().isPreGame()) {
			return;
		}
		
		if ((event.hasItem()) && (event.getItem().getType() == Material.COMPASS) && (event.getAction() != Action.PHYSICAL)) {
	         Player player = event.getPlayer();
	         if (!HungerGames.getManager().getGamer(player.getUniqueId()).isJogando()) {
		         new AliveGamers().open(player);
	 	         return;
	         }
	         if (VanishManager.inAdmin(player)) {
	        	 new AliveGamers().open(player);
	 	         return;
	         }
	         
	         Player alvo = getRandomPlayer(player);
	         
	         if (alvo == null) {
	             player.sendMessage("§fNenhum jogador por perto, bússola apontando para o §e§lSPAWN§f.");
	             player.setCompassTarget(player.getWorld().getSpawnLocation());
	         } else {
	           	 player.sendMessage("§eSua bússola est§ apontando para §7" + alvo.getName());
	           	 player.setCompassTarget(alvo.getLocation());
	         }
		}
	}

	public Player getRandomPlayer(Player player) {
		Player target = null;
        for (Player playerTarget : HungerGames.getManager().getGameManager().getAliveGamers()) {
             if (playerTarget != null) {
                 if (!playerTarget.equals(player)) {
              	     if (playerTarget.getLocation().distance(player.getLocation()) >= 15.0D) {
              	    	 if (target == null) {
              				 target = playerTarget;
              	    	 } else {
              				  double distanciaAtual = target.getLocation().distance(player.getLocation());
              				  double novaDistancia = playerTarget.getLocation().distance(player.getLocation());
              				  if (novaDistancia < distanciaAtual) {
              					  target = playerTarget;
              					  if (novaDistancia <= 50) {
              						  break;
              					  }
              				  }
              	    	 }
              	     }
                 }
             }
        }
		return target;
	}
}