/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.CritterPet.Registry.CritterType;
import Reika.DragonAPI.Interfaces.Item.IndexedItemSprites;

public class ItemTaming extends Item implements IndexedItemSprites {

	public ItemTaming() {
		super();
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
			CritterType type = CritterType.critterList[i-1];
			String name = type.name;
			Item item = type.tamingItem;
			li.add("Loaded with "+item.getItemStackDisplayName(is));
			li.add("Ready to tame a "+name+".");
		}
	}

	@Override
	public void getSubItems(Item id, CreativeTabs tab, List li) {
		li.add(new ItemStack(id, 1, 0));
		for (int i = 0; i < CritterType.critterList.length; i++) {
			CritterType s = CritterType.critterList[i];
			if (s.isAvailable())
				li.add(new ItemStack(id, 1, i+1));
		}
	}

	@Override
	public void registerIcons(IIconRegister ico) {

	}

	@Override
	public boolean itemInteractionForEntity(ItemStack is, EntityPlayer ep, EntityLivingBase elb)
	{
		return TamingController.TameCritter(elb, ep);
	}

	@Override
	public int getItemSpriteIndex(ItemStack is) {
		return is.getItemDamage()+1;
	}

	@Override
	public String getTexture(ItemStack is) {
		return "Textures/items.png";
	}

	@Override
	public Class getTextureReferenceClass() {
		return CritterPet.class;
	}

}
