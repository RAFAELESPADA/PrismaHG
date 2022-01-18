package com.br.gabrielsilva.prismamc.hungergames.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.menu.ClickType;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.menu.MenuClickHandler;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.menu.MenuInventory;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.menu.MenuItem;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.manager.gamer.Gamer;

public class AliveGamers extends MenuInventory {

    private static final int ITEMS_PER_PAGE = 28;
    private static final int PREVIOUS_PAGE_SLOT = 27;
    private static final int NEXT_PAGE_SLOT = 35;
    private static final int CENTER = 31;
    private static final int HEADS_PER_ROW = 7;
	
	public AliveGamers() {
		this(1);
	}
	
    public AliveGamers(int page) {
        this(page, (3 / ITEMS_PER_PAGE) + 1);
    }
    
    public AliveGamers(int page, int maxPages) {
    	super("Jogadores Vivos", 6);
    	
    	List<MenuItem> items = new ArrayList<>();
    	
		for (Player onlines : Bukkit.getOnlinePlayers()) {
			 final Gamer gamer = HungerGames.getManager().getGamer(onlines.getUniqueId());
			 if (!gamer.isJogando()) {
				 continue;
			 }
			 items.add(new MenuItem(new ItemBuilder().material(Material.SKULL_ITEM).durability(3).name("§b" + onlines.getName()).headName(onlines.getName())
	                 .lore(new String[]{"", "§bKills: §7" + gamer.getKills(), "§bKit: §7" + gamer.getKit1()}).build(), new ClickHandler()));
		}
		
        int pageStart = 0;
        int pageEnd = ITEMS_PER_PAGE;
        if (page > 1) {
            pageStart = ((page - 1) * ITEMS_PER_PAGE);
            pageEnd = (page * ITEMS_PER_PAGE);
        }
        if (pageEnd > items.size()) {
            pageEnd = items.size();
        }

        if (page == 1) {
        	setItem(PREVIOUS_PAGE_SLOT, new ItemBuilder().material(Material.AIR).build());
        } else {
        	setItem(new MenuItem(new ItemBuilder().material(Material.ARROW).name("§aP§gina Anterior").build(),
        			(player, arg1, arg2, arg3, arg4) -> new AliveGamers(page - 1).open(player)), PREVIOUS_PAGE_SLOT);
        }

        if ((items.size() / ITEMS_PER_PAGE) + 1 <= page) {
        	setItem(NEXT_PAGE_SLOT, new ItemBuilder().material(Material.AIR).build());
        } else {
        	setItem(new MenuItem(new ItemBuilder().material(Material.ARROW).name("§aPr§xima P§gina").build(),
        			(player, arg1, arg2, arg3, arg4) -> new AliveGamers(page + 1).open(player)), NEXT_PAGE_SLOT);
        }

        int kitSlot = 10;

        for (int i = pageStart; i < pageEnd; i++) {
             MenuItem item = items.get(i);
             setItem(item, kitSlot);
             if (kitSlot % 9 == HEADS_PER_ROW) {
                 kitSlot += 3;
                 continue;
             }
             kitSlot += 1;
        }
        
        if (items.size() == 0) {
        	setItem(CENTER, new ItemBuilder().material(Material.REDSTONE_BLOCK).name("§cNenhum jogador vivo.").build());
        }
        
        if (HungerGames.getManager().getStructureManager().getFeast().getFeastLocation() != null) {
        	setItem(4, new MenuItem(new ItemBuilder().material(Material.CAKE).name("§aFeast").build(), new ClickHandler()));
        }
    }
    
    private static class ClickHandler implements MenuClickHandler {
    	
        @Override
        public void onClick(Player player, Inventory inventory, ClickType clickType, ItemStack item, int slot) {
        	if (clickType != ClickType.LEFT) {
        		return;
        	}
        	
            player.closeInventory();
            
            if (item.getType() == Material.CAKE) {
            	player.sendMessage("§aVoce foi para o feast!");
            	player.teleport(HungerGames.getManager().getStructureManager().getFeast().getFeastLocation().clone().add(0.5, 3, 0.5));
            } else {
            	String name = item.getItemMeta().getDisplayName().replace("§b", "");
            	Player target = Bukkit.getPlayer(name);
            	if (target == null) {
            		player.sendMessage("§cJogador offline!");
            		return;
            	}
            	player.teleport(target.getLocation().clone().add(0, 1.2, 0));
            	player.sendMessage("§aVoce se teleportou para o §7" + target.getName());
            }
        }
    }
}