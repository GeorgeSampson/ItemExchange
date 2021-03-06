package com.untamedears.ItemExchange.metadata;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.untamedears.ItemExchange.DeprecatedMethods;
import com.untamedears.ItemExchange.ItemExchangePlugin;
import com.untamedears.ItemExchange.utility.ExchangeRule;

public class EnchantmentStorageMetadata implements AdditionalMetadata {
	private static final String[] numerals = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
	
	private Map<Enchantment, Integer> enchantments;
	
	public EnchantmentStorageMetadata(EnchantmentStorageMeta meta) {
		enchantments = meta.getStoredEnchants();
	}
	
	private EnchantmentStorageMetadata() {
		
	}
	
	@Override
	public String serialize() {
		StringBuilder serialized = new StringBuilder();
		
		Iterator<Enchantment> iterator = enchantments.keySet().iterator();
		
		while(iterator.hasNext()) {
			Enchantment enchantment = iterator.next();
			int enchantmentId = DeprecatedMethods.getEnchantmentId(enchantment);
			
			serialized.append(enchantmentId).append(ExchangeRule.tertiarySpacer).append(enchantments.get(enchantment));
			
			if(iterator.hasNext())
				serialized.append(ExchangeRule.secondarySpacer);
		}
		
		return serialized.toString();
	}

	@Override
	public boolean matches(ItemStack item) {
		if(item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			
			if(meta instanceof EnchantmentStorageMeta) {
				EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) meta;
				
				return storageMeta.getStoredEnchants().equals(enchantments);
			}
		}
		
		return false;
	}

	@Override
	public String getDisplayedInfo() {
		StringBuilder info = new StringBuilder();
		
		info.append(ChatColor.DARK_AQUA).append("Stored enchantments: ");
		
		if(enchantments.size() > 0) {
			Iterator<Enchantment> iterator = enchantments.keySet().iterator();
			
			while(iterator.hasNext()) {
				Enchantment enchantment = iterator.next();
				
				String name = ItemExchangePlugin.ENCHANTMENT_NAME.containsKey(enchantment.getName()) ? ItemExchangePlugin.ENCHANTMENT_NAME.get(enchantment.getName()) : enchantment.getName();
				
				info.append(name).append(" ");
				
				int level = enchantments.get(enchantment);
				
				if(level - 1 < numerals.length)
					info.append(numerals[level - 1]);
				else
					info.append(level);
				
				if(iterator.hasNext())
					info.append('\n');
			}
		}
		else {
			info.append(ChatColor.AQUA).append("<none>");
		}
		
		return info.toString();
	}
	
	public static EnchantmentStorageMetadata deserialize(String s) {
		EnchantmentStorageMetadata metadata = new EnchantmentStorageMetadata();
		
		metadata.enchantments = new HashMap<Enchantment, Integer>();
		
		String[] enchantments = s.split(ExchangeRule.secondarySpacer);
		
		for(String enchantment : enchantments) {
			String[] parts = enchantment.split(ExchangeRule.tertiarySpacer);
			
			int id = Integer.parseInt(parts[0]);
			int level = Integer.parseInt(parts[1]);
			
			metadata.enchantments.put(DeprecatedMethods.getEnchantmentById(id), level);
		}
		
		return metadata;
	}
}
