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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import Reika.DragonAPI.ModRegistry.ModSpiderList;
import Reika.SpiderPet.EntitySpiderBase;

public class TameHeatScar extends EntitySpiderBase {

	public TameHeatScar(World par1World) {
		super(par1World, ModSpiderList.HEATSCAR);
	}

	@Override
	public void updateRider() {
		if (riddenByEntity instanceof EntityLiving) {
			EntityLiving rider = (EntityLiving)riddenByEntity;
			rider.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 20, 0));
		}
	}
}
