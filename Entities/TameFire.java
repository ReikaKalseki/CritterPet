/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
		return !dsc.isFireDamage();
	}

	@Override
	public int getAttackDamage() {
		return 4;
	}

	@Override
	public boolean isRideable() {
		return true;
	}

}
