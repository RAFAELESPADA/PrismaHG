package com.br.gabrielsilva.prismamc.hungergames.menus.enums;

import lombok.Getter;

public enum InventoryMode {

    SEUS_KITS("Seus Kits"),
    TODOS_KITS("Todos os Kits"),
    LOJA("Loja de Kits");
	
	@Getter
	private String inventoryName;
	
	InventoryMode(String inventoryName) {
		this.inventoryName = inventoryName;
	}
}