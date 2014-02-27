/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.SpiderPet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.SpiderPet.Entities.EntitySpiderBase;
import Reika.SpiderPet.Entities.TameHeatScar;
import Reika.SpiderPet.Entities.TameHedge;
import Reika.SpiderPet.Entities.TameKing;
import Reika.SpiderPet.Entities.TameVanilla;
import Reika.SpiderPet.Registry.SpiderType;

public class TamingController {

	public static boolean TameSpider(Entity e, EntityPlayer ep) {
		World world = ep.worldObj;
		SpiderType s = getType(e);
		ItemStack is = ep.getCurrentEquippedItem();
		if (is == null)
			return false;
		if (s == null)
			return false;
		if (canTame(s, is)) {
			EntitySpiderBase es = null;
			switch(s) {
			case HEATSCAR:
				es = new TameHeatScar(world);
				break;
			case HEDGE:
				es = new TameHedge(world);
				break;
			case KING:
				es = new TameKing(world);
				break;
			case VANILLA:
				es = new TameVanilla(world);
				break;
			default:
				return false;
			}
			es.setLocationAndAngles(e.posX, e.posY, e.posZ, e.rotationYaw, e.rotationPitch);
			es.rotationYawHead = ((EntityLivingBase)e).rotationYawHead;
			es.setOwner(ep);
			e.setDead();
			if (!world.isRemote) {
				world.spawnEntityInWorld(es);
			}
			es.spawnEffects();
			if (!ep.capabilities.isCreativeMode)
				ep.setCurrentItemOrArmor(0, new ItemStack(is.itemID, 1, 0));
			return true;
		}
		return false;
	}

	private static boolean canTame(SpiderType s, ItemStack is) {
		if (is == null)
			return false;
		if (is.itemID != SpiderPet.tool.itemID)
			return false;
		if (is.getItemDamage() <= 0)
			return false;
		return is.getItemDamage() == s.ordinal()+1;
	}

	public static SpiderType getType(Entity e) {
		Class c = e.getClass();
		String n = c.getSimpleName();
		//ReikaJavaLibrary.pConsole(e+":"+c+":"+n);
		if (n.equalsIgnoreCase("HeatscarSpider"))
			return SpiderType.HEATSCAR;
		if (n.equalsIgnoreCase("EntityTFKingSpider"))
			return SpiderType.KING;
		if (n.equalsIgnoreCase("EntityTFHedgeSpider"))
			return SpiderType.HEDGE;
		if (n.equalsIgnoreCase("EntitySpider"))
			return SpiderType.VANILLA;
		return null;
	}

}
