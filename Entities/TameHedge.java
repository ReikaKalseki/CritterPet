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

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import Reika.DragonAPI.ModRegistry.ModSpiderList;
import Reika.SpiderPet.EntitySpiderBase;

public class TameHedge extends EntitySpiderBase {

	public TameHedge(World par1World) {
		super(par1World, ModSpiderList.HEDGE);
	}

	@Override
	public void updateRider() {
		if (riddenByEntity instanceof EntityLivingBase) {
			EntityLivingBase rider = (EntityLivingBase)riddenByEntity;
		}
	}
}
