/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet.Entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import Reika.CritterPet.Registry.CritterType;

public class TameVanilla extends EntitySpiderBase {

	public TameVanilla(World par1World) {
		super(par1World, CritterType.VANILLA);
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

	@Override
	public int getAttackDamage() {
		return 3;
	}
}
