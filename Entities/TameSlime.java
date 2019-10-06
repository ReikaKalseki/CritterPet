/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet.Entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.CritterPet.Entities.Base.EntitySlimeBase;
import Reika.CritterPet.Registry.CritterType;

public class TameSlime extends EntitySlimeBase {

	public TameSlime(World world) {
		super(world, CritterType.SLIME);
	}

	@Override
	protected void applyAttackEffects(EntityLivingBase e) {

	}

	@Override
	public boolean canBeHurtBy(DamageSource dsc) {
		return !dsc.isExplosion() && dsc != DamageSource.drown;
	}

}
