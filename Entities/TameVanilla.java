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
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import Reika.SpiderPet.EntitySpiderBase;
import Reika.SpiderPet.Registry.SpiderType;

public class TameVanilla extends EntitySpiderBase {

	public TameVanilla(World par1World) {
		super(par1World, SpiderType.VANILLA);
	}

	@Override
	protected void updateRider() {
		if (riddenByEntity instanceof EntityLivingBase) {
			EntityLivingBase rider = (EntityLivingBase)riddenByEntity;
		}
	}

	@Override
	protected void applyAttackEffects(EntityLivingBase e) {

	}

	@Override
	public boolean canBeHurtBy(DamageSource dsc) {
		return true;
	}
}
