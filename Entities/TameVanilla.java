/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.SpiderPet.Entities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import Reika.SpiderPet.EntitySpiderBase;

public class TameVanilla extends EntitySpiderBase {

	public TameVanilla(World par1World) {
		super(par1World, null);
	}

	@Override
	public void updateRider() {
		if (riddenByEntity instanceof EntityLiving) {
			EntityLiving rider = (EntityLiving)riddenByEntity;
		}
	}
}
