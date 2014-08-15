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

import Reika.CritterPet.Registry.CritterType;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class TameFire extends EntitySpiderBase {

	public TameFire(World world) {
		super(world, CritterType.FIRE);
	}

	@Override
	protected void updateRider() {

	}

	@Override
	protected void applyAttackEffects(EntityLivingBase e) {
		e.setFire(4);
	}

	@Override
	public boolean canBeHurtBy(DamageSource dsc) {
		return true;
	}

	@Override
	public int getAttackDamage() {
		return 4;
	}

}