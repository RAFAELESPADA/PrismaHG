package com.br.gabrielsilva.prismamc.hungergames.api;

import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class InventoryStore {

	private String nome;
	private ItemStack[] armor, inv;
	private List<PotionEffect> potions;
	
	public InventoryStore(String nome, ItemStack[] armor, ItemStack[] inv, List<PotionEffect> potions) {
		this.nome = nome;
		this.armor = armor;
		this.inv = inv;
		this.potions = potions;
	}
	
	public ItemStack[] getArmor() {
		return armor;
	}

	public ItemStack[] getInv() {
		return inv;
	}

	public String getNome() {
		return nome;
	}

	public List<PotionEffect> getPotions() {
		return potions;
	}

	public void setArmor(ItemStack[] armor) {
		this.armor = armor;
	}

	public void setInv(ItemStack[] inv) {
		this.inv = inv;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setPotions(List<PotionEffect> potions) {
		this.potions = potions;
	}
}