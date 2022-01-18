package com.br.gabrielsilva.prismamc.hungergames.manager.kit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.core.utils.ClassGetter;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;
import com.br.gabrielsilva.prismamc.hungergames.ability.Kit;

public class KitLoader {

	private static List<String> kitsAtivados = new ArrayList<>();
	
	public static void init() {
		List<String> kitsExistentes = new ArrayList<>();
		
		for (Class<?> classes : ClassGetter.getClassesForPackage(HungerGames.getInstance(),
				"com.br.gabrielsilva.prismamc.hungergames.ability.register")) {
		
			try {
				 if (Kit.class.isAssignableFrom(classes) && (classes != Kit.class)) {
				     kitsExistentes.add(classes.getSimpleName());
				 }
			 } catch (Exception e) {}
		}
		
		for (String kit : kitsExistentes) {
			
			
			 
		     

		    	 kitsAtivados.add(kit);
		     
		}
		
		kitsExistentes.clear();
		HungerGames.console("Total de: ("+kitsAtivados.size()+") kits ativados.");
		
		addKits();
	}
	
	private static void addKits() {
		for (Class<?> classes : ClassGetter.getClassesForPackage(HungerGames.getInstance(), "com.br.gabrielsilva.prismamc.hungergames.ability.register")) {
			 if (!kitsAtivados.contains(classes.getSimpleName())) {
				 continue;
			 }
			 
			 try {
				 if (Kit.class.isAssignableFrom(classes) && (classes != Kit.class)) {
					 Kit kit = (Kit) classes.newInstance();
					 
					 KitManager.getKits().put(kit.getNome().toLowerCase(), kit);
				 }
			 } catch (Exception ex) {
				 HungerGames.console("Ocorreu um erro ao tentar adicionar um Kit. ->" + ex.getLocalizedMessage());
			 }
		}
	}
}