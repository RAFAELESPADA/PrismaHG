package com.br.gabrielsilva.prismamc.hungergames.ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.br.gabrielsilva.prismamc.commons.bukkit.api.cooldown.CooldownAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.cooldown.types.ItemCooldown;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.manager.kit.KitManager;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class Kit implements Listener {
	
	private String nome;
	private List<String> descrição;
	private ItemStack icone;
	private int preço, cooldownSegundos;
	private boolean usarInvencibilidade;
	private ItemStack[] itens;
	
	//new
	private boolean listenerRegistred = false;

	public void setDescrição(List<String> list) {
		this.descrição = list;
		
		ItemMeta meta = this.icone.getItemMeta();

		List<String> lore = new ArrayList<String>();

		for (String descriptionL : this.descrição) {
			 lore.add(descriptionL.replaceAll("&", "§").replace("%cooldown%", "" + cooldownSegundos));
		}

		meta.setDisplayName("§a" + nome);
		meta.setLore(lore);

		this.icone.setItemMeta(meta);
	}
	
	public void setItens(ItemStack... itens) {
		this.itens = itens;
	}
	
	private final String itemColor = "§b";
	
	protected boolean containsHability(Player player) {
		return HungerGames.getManager().getGamer(player.getUniqueId()).getKit1().equalsIgnoreCase(getNome());
	}
	
    protected boolean useAbility(Player player) {
    	if (HungerGames.getManager().getGameManager().isPreGame()) {
    		return false;
    	}
    	
    	if (KitManager.isAllKitsDesativados()) {
    		return false;
    	}
    	
    	if (KitManager.getKitsDesativados().contains(getNome().toLowerCase())) {
    		return false;
    	}
    	
    	if (HungerGames.getManager().getGameManager().isInvencibilidade()) {
    		if (!isUsarInvencibilidade()) {
    			return false;
    		}
    	}
    	
    	if (!HungerGames.getManager().getGamer(player.getUniqueId()).getKit1().equalsIgnoreCase(getNome())) {
    		return false;
    	}
    	
        return true;
    }
    
    public void registerListener() {
    	if (!listenerRegistred) {
            listenerRegistred = true;
            Bukkit.getPluginManager().registerEvents(this, HungerGames.getInstance());
        }
    }
    
    public void unregisterPlayer(Player player) {
        CooldownAPI.removeAllCooldowns(player);
    }

    protected boolean hasCooldown(Player player) {
        return CooldownAPI.hasCooldown(player, "Kit");
    }
    
    protected boolean hasCooldown(Player player, String cooldown) {
        return CooldownAPI.hasCooldown(player, cooldown);
    }
    
    protected boolean inCooldown(Player player, String cooldown) {
        return CooldownAPI.inCooldown(player, cooldown);
    }
    
    protected boolean inCooldown(Player player) {
        return CooldownAPI.inCooldown(player, "Kit");
    }
    
    protected void sendMessageCooldown(Player player) {
    	CooldownAPI.sendMessage(player, "Kit");
    }
    
    protected void sendMessageCooldown(Player player, String cooldown) {
    	CooldownAPI.sendMessage(player, cooldown);
    }
    
    protected void addCooldown(Player player, String cooldownName, long time) {
        if (CooldownAPI.hasCooldown(player, cooldownName)) {
            CooldownAPI.removeCooldown(player, cooldownName);
        }
        CooldownAPI.addCooldown(player, new com.br.gabrielsilva.prismamc.commons.bukkit.api.cooldown.types.Cooldown(cooldownName, time));
    }
    protected void setCooldown(Player player, String cooldownName, long time) {
        CooldownAPI.addCooldown(player, new com.br.gabrielsilva.prismamc.commons.bukkit.api.cooldown.types.Cooldown(cooldownName, time));
    }
    
    protected void addCooldown(Player player, long time) {
        if (CooldownAPI.hasCooldown(player, "Kit")) {
            CooldownAPI.removeCooldown(player, "Kit");
        }
        CooldownAPI.addCooldown(player, new com.br.gabrielsilva.prismamc.commons.bukkit.api.cooldown.types.Cooldown("Kit", time));
    }

    protected void addItemCooldown(Player player, ItemStack item, String cooldownName, long time) {
        if (CooldownAPI.hasCooldown(player, cooldownName)) {
            CooldownAPI.removeCooldown(player, cooldownName);
        }
        CooldownAPI.addCooldown(player, new ItemCooldown(item, cooldownName, time));
    }
}