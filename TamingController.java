/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.CritterPet.Interfaces.TamedMob;
import Reika.CritterPet.Registry.CritterType;

public class TamingController {

	private static final HashMap<Class, CritterType> classMap = new HashMap();

	public static boolean TameCritter(Entity e, EntityPlayer ep) {
		World world = ep.worldObj;
		CritterType s = getType(e);
		ItemStack is = ep.getCurrentEquippedItem();
		if (is == null)
			return false;
		if (s == null)
			return false;
		if (canTame(s, is)) {
			TamedMob es = s.create(world, e);
			if (es == null)
				return false;
			es.setOwner(ep);
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
		if (classMap.containsKey(e.getClass()))
			return classMap.get(e.getClass());
		for (CritterType type : CritterType.critterList) {
			if (type.isAvailable() && type.isValid(e)) {
				classMap.put(e.getClass(), type);
				return type;
			}
		}
		classMap.put(e.getClass(), null);
		return null;
	}

}
