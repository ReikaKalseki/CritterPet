/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.CritterPet.Entities.TameFire;
import Reika.CritterPet.Entities.TameHeatScar;
import Reika.CritterPet.Entities.TameHedge;
import Reika.CritterPet.Entities.TameKing;
import Reika.CritterPet.Entities.TameMazeSlime;
import Reika.CritterPet.Entities.TameMistWolf;
import Reika.CritterPet.Entities.TameSlime;
import Reika.CritterPet.Entities.TameVanilla;
import Reika.CritterPet.Interfaces.TamedMob;
import Reika.CritterPet.Registry.CritterType;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class TamingController {

	public static boolean TameCritter(Entity e, EntityPlayer ep) {
		World world = ep.worldObj;
		CritterType s = getType(e);
		ItemStack is = ep.getCurrentEquippedItem();
		if (is == null)
			return false;
		if (s == null)
			return false;
		if (canTame(s, is)) {
			TamedMob es = null;
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
			case SLIME:
				es = new TameSlime(world);
				break;
			case FIRE:
				es = new TameFire(world);
				break;
			case MAZE:
				es = new TameMazeSlime(world);
				break;
			case MISTWOLF:
				es = new TameMistWolf(world);
				break;
				//case WISP:
				//	es = new TameWisp(world);
				//	break;
			default:
				return false;
			}
			((Entity)es).setLocationAndAngles(e.posX, e.posY, e.posZ, e.rotationYaw, e.rotationPitch);
			((EntityLivingBase)es).rotationYawHead = ((EntityLivingBase)e).rotationYawHead;
			es.setOwner(ep);
			ReikaJavaLibrary.pConsole(es);
			e.setDead();
			if (!world.isRemote) {
				world.spawnEntityInWorld((Entity)es);
			}
			es.spawnEffects();
			if (!ep.capabilities.isCreativeMode)
				ep.setCurrentItemOrArmor(0, new ItemStack(is.getItem(), 1, 0));
			return true;
		}
		return false;
	}

	private static boolean canTame(CritterType s, ItemStack is) {
		if (is == null)
			return false;
		if (is.getItem() != CritterPet.tool)
			return false;
		if (is.getItemDamage() <= 0)
			return false;
		return is.getItemDamage() == s.ordinal()+1;
	}

	public static CritterType getType(Entity e) {
		Class c = e.getClass();
		String n = c.getSimpleName();
		//ReikaJavaLibrary.pConsole(e+":"+c+":"+n);
		if (n.equalsIgnoreCase("HeatscarSpider"))
			return CritterType.HEATSCAR;
		if (n.equalsIgnoreCase("EntityTFKingSpider"))
			return CritterType.KING;
		if (n.equalsIgnoreCase("EntityTFHedgeSpider"))
			return CritterType.HEDGE;
		if (n.equalsIgnoreCase("EntitySpider"))
			return CritterType.VANILLA;
		if (n.equalsIgnoreCase("EntityLegacySpider")) //LegacyCraft
			return CritterType.VANILLA;
		if (n.equalsIgnoreCase("EntityTFSlimeBeetle"))
			return CritterType.SLIME;
		if (n.equalsIgnoreCase("EntityTFFireBeetle"))
			return CritterType.FIRE;
		if (n.equalsIgnoreCase("EntityTFMazeSlime"))
			return CritterType.MAZE;
		//if (n.equalsIgnoreCase("EntityWisp"))
		//	return CritterType.WISP;
		if (n.equalsIgnoreCase("EntityTFMistWolf"))
			return CritterType.MISTWOLF;
		return null;
	}

}
