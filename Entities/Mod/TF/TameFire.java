/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet.Entities.Mod.TF;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.CritterPet.Entities.Base.EntitySpiderBase;
import Reika.CritterPet.Registry.CritterType;

public class TameFire extends EntitySpiderBase {

	public TameFire(World world) {
		super(world, CritterType.FIREBEETLE);
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
