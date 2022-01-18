package com.br.gabrielsilva.prismamc.hungergames.menus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.menu.ClickType;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.menu.MenuClickHandler;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.menu.MenuInventory;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.menu.MenuItem;
import com.br.gabrielsilva.prismamc.commons.core.data.category.DataCategory;
import com.br.gabrielsilva.prismamc.commons.core.data.type.DataType;
import com.br.gabrielsilva.prismamc.commons.core.group.Groups;
import com.br.gabrielsilva.prismamc.commons.custompackets.BukkitClient;
import com.br.gabrielsilva.prismamc.commons.custompackets.bungee.packets.PacketBungeeUpdateField;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;
import com.br.gabrielsilva.prismamc.hungergames.manager.kit.KitManager;
import com.br.gabrielsilva.prismamc.hungergames.menus.enums.InventoryMode;

public class KitSelector extends MenuInventory {

    private static final int ITEMS_PER_PAGE = 21;
    private static final int PREVIOUS_PAGE_SLOT = 27;
    private static final int NEXT_PAGE_SLOT = 35;
    private static final int CENTER = 31;

    private static final int KITS_PER_ROW = 7;
    
    public KitSelector(Player player) {
        this(player, 1, InventoryMode.SEUS_KITS);
    }
    
    public KitSelector(Player player, InventoryMode inventoryMode) {
        this(player, 1, inventoryMode);
    }

    public KitSelector(Player player, int page, InventoryMode inventoryMode) {
        this(player, page, (3 / ITEMS_PER_PAGE) + 1, inventoryMode);
    }

    public KitSelector(Player player1, int page, int maxPages, InventoryMode inventoryMode) {
    	super(inventoryMode.getInventoryName(), 6);
    	
    	switch (inventoryMode) {
    	case SEUS_KITS:
      	   	setItem(3, new ItemBuilder().material(Material.INK_SACK).name("§aSeus Kits").durability(10).build());
    	   	
    	   	setItem(4, new ItemBuilder().material(Material.INK_SACK).name("§7Todos os Kits").durability(8).build(), 
        			(player, arg1, arg2, arg3, arg4) -> new KitSelector(player, 1, InventoryMode.TODOS_KITS).open(player));
    	   	
        	setItem(5, new ItemBuilder().material(Material.INK_SACK).name("§7Loja de Kits").durability(8).build(), 
        			(player, arg1, arg2, arg3, arg4) -> new KitSelector(player, 1, InventoryMode.LOJA).open(player));
    		break;
    	case TODOS_KITS:
    	   	setItem(3, new ItemBuilder().material(Material.INK_SACK).name("§7Seus Kits").durability(8).build(), 
        			(player, arg1, arg2, arg3, arg4) -> new KitSelector(player, 1, InventoryMode.SEUS_KITS).open(player));
    	   	
    	   	setItem(4, new ItemBuilder().material(Material.INK_SACK).name("§aTodos os Kits").durability(10).build());
    	   	
        	setItem(5, new ItemBuilder().material(Material.INK_SACK).name("§7Loja de Kits").durability(8).build(), 
        			(player, arg1, arg2, arg3, arg4) -> new KitSelector(player, 1, InventoryMode.LOJA).open(player));
    		break;
    		
    	case LOJA:
    	   	setItem(3, new ItemBuilder().material(Material.INK_SACK).name("§7Seus Kits").durability(8).build(), 
        			(player, arg1, arg2, arg3, arg4) -> new KitSelector(player, 1, InventoryMode.SEUS_KITS).open(player));
    	   	
    	   	setItem(4, new ItemBuilder().material(Material.INK_SACK).name("§7Todos os Kits").durability(8).build(), 
        			(player, arg1, arg2, arg3, arg4) -> new KitSelector(player, 1, InventoryMode.TODOS_KITS).open(player));
    	   	
    	   	setItem(5, new ItemBuilder().material(Material.INK_SACK).name("§aLoja de Kits").durability(10).build());
    		break;
    		default:
    			break;
    	}
    	
        List<Kit> kitList = new ArrayList<>(KitManager.getKits().values());

        kitList.sort(Comparator.comparing(Kit::getNome));
    	
    	List<MenuItem> items = new ArrayList<>();
    	
        for (Kit kit : kitList) {
             if (continuePlayer(player1, kit.getNome(), inventoryMode)) {
            	 if (inventoryMode == InventoryMode.LOJA) {
            		 items.add(new MenuItem(kit.getIcone(), new LojaKitsHandler(kit)));
            	 } else {
            		 items.add(new MenuItem(kit.getIcone(), new KitSelectHandler(kit)));
            	 }
             }
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
        	setItem(new MenuItem(new ItemBuilder().material(Material.ARROW).name("§aPágina Anterior").build(),
        			(player, arg1, arg2, arg3, arg4) -> new KitSelector(player, page - 1, inventoryMode).open(player)), PREVIOUS_PAGE_SLOT);
        }

        if ((items.size() / ITEMS_PER_PAGE) + 1 <= page) {
        	setItem(NEXT_PAGE_SLOT, new ItemBuilder().material(Material.AIR).build());
        } else {
        	setItem(new MenuItem(new ItemBuilder().material(Material.ARROW).name("§aPróxima Página").build(),
        			(player, arg1, arg2, arg3, arg4) -> new KitSelector(player, page + 1, inventoryMode).open(player)), NEXT_PAGE_SLOT);
        }

        int kitSlot = 19;

        for (int i = pageStart; i < pageEnd; i++) {
             MenuItem item = items.get(i);
             setItem(item, kitSlot);
             if (kitSlot % 9 == KITS_PER_ROW) {
                 kitSlot += 3;
                 continue;
             }
             kitSlot += 1;
        }
        
        if (items.size() == 0) {
        	switch (inventoryMode) {
        	case SEUS_KITS:
                setItem(new ItemBuilder().material(Material.REDSTONE_BLOCK).name("§cVocê não possui um kit!").build(), CENTER);
        		break;
        	case TODOS_KITS:
                setItem(new ItemBuilder().material(Material.REDSTONE_BLOCK).name("§cNenhum kit Disponível!").build(), CENTER);
        		break;
        	case LOJA:
                setItem(new ItemBuilder().material(Material.REDSTONE_BLOCK).name("§cNenhum kit para comprar!").build(), CENTER);
        		break;
        		default:
        			break;
        	}
        }
    }
    
	public static boolean continuePlayer(Player player, String kit, InventoryMode modo) {
		if (modo.equals(InventoryMode.TODOS_KITS)) {
			return true;
		}
		
		if (modo.equals(InventoryMode.SEUS_KITS)) {
			if (KitManager.hasPermissionKit(player, kit, false)) {
				return true;
			}
			return false;
		} else if (modo.equals(InventoryMode.LOJA)) {
			if (BukkitMain.getManager().getDataManager().getBukkitPlayer(player.getUniqueId()).getDataHandler().getData(DataType.GRUPO).getGrupo().getNivel() >= Groups.BETA.getNivel()) {
				return false;
			}
			if ((!player.hasPermission("kit." + kit.toLowerCase())) || (!player.hasPermission("kit.all"))) {
				return true;
			}
			return false;
		}
		return false;
	}
    
    private static class LojaKitsHandler implements MenuClickHandler {
    	
    	private Kit kit;
    	
        public LojaKitsHandler(Kit kit) {
            this.kit = kit;
        }
    	
        @Override
        public void onClick(Player player, Inventory arg1, ClickType clickType, ItemStack arg3, int arg4) {
        	if (clickType != ClickType.LEFT) {
        		return;
        	}
        	
        	player.closeInventory();
        	
        	BukkitPlayer bukkitPlayer = BukkitMain.getManager().getDataManager().getBukkitPlayer(player.getUniqueId());
			if (bukkitPlayer.getDataHandler().getInt(DataType.COINS) < kit.getPreço()) {
				player.sendMessage("§cVocê não possuí Coins o suficiente!" + (kit.getPreço() + " Coins necessarios"));
				return;
			}
			bukkitPlayer.getDataHandler().getData(DataType.COINS).remove(kit.getPreço());
			bukkitPlayer.getDataHandler().updateValues(DataCategory.PRISMA_PLAYER, true, DataType.COINS);
			  
			BukkitClient.sendPacket(player, new PacketBungeeUpdateField(bukkitPlayer.getNick(), "ProxyPlayer", "AddPerm", 
					"kit." + kit.getNome()));
				  
			player.sendMessage("§aVoce comprou o Kit: §7" + kit.getNome());
        }
    }
    
    private static class KitSelectHandler implements MenuClickHandler {
    	
    	private Kit kit;
    	
        public KitSelectHandler(Kit kit) {
            this.kit = kit;
        }
    	
        @Override
        public void onClick(Player player, Inventory arg1, ClickType clickType, ItemStack arg3, int arg4) {
        	if (clickType != ClickType.LEFT) {
        		return;
        	}
        	
            player.closeInventory();
            
            if (!KitManager.hasPermissionKit(player, kit.getNome(), true)) {
            	return;
            }
            
            player.performCommand("kit " + kit.getNome());
        }
    }
}