/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.SpiderPet;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import Reika.SpiderPet.Registry.SpiderType;

public class ItemTaming extends Item {

	private Icon[] icons = new Icon[1+SpiderType.spiderList.length];

	public ItemTaming(int par1) {
		super(par1);
		this.setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
	{
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep)
	{
		return is;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean verbose) {
		int i = is.getItemDamage();
		if (i == 0) {
			li.add("Missing a taming item.");
		}
		else {
			SpiderType type = SpiderType.spiderList[i-1];
			String name = type.getName();
			Item item = type.tamingItem;
			li.add("Loaded with "+item.getItemDisplayName(is));
			li.add("Ready to tame a "+name+" Spider.");
		}
	}

	@Override
	public void getSubItems(int id, CreativeTabs tab, List li) {
		int num = 1+SpiderType.spiderList.length;
		for (int i = 0; i < num; i++) {
			li.add(new ItemStack(id, 1, i));
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		return TamingController.TameSpider(entity, player);
	}

	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < icons.length; i++)
			icons[i] = ico.registerIcon("spiderpet:tool_"+i);
	}

	@Override
	public Icon getIconFromDamage(int dmg) {
		return icons[dmg];
	}

}
