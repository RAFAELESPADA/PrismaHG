package com.br.gabrielsilva.prismamc.hungergames.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SoupListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSoup(PlayerInteractEvent event) {
		if (event.hasItem() && event.getItem().getType() == Material.MUSHROOM_SOUP && event.getAction().name().contains("RIGHT")) {
			boolean tomou = false;
			Damageable d = event.getPlayer();
			if (d.getHealth() < d.getMaxHealth()) {
				event.setCancelled(true);
				double hp = d.getHealth() + 7;
				
				if (hp > d.getMaxHealth())
					hp = d.getMaxHealth();
				
				event.getPlayer().setHealth(hp);
				tomou = true;
			} else if (event.getPlayer().getFoodLevel() < 20) {
				event.setCancelled(true);
				event.getPlayer().setFoodLevel(event.getPlayer().getFoodLevel() + 7);
				tomou = true;
			}
			if (tomou) {
				event.getPlayer().setItemInHand(new ItemStack(Material.BOWL));
				event.getPlayer().updateInventory();
			}
		}
	}
}