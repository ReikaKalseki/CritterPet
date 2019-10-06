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

import Reika.CritterPet.Entities.Base.EntitySlimeBase;
import Reika.CritterPet.Registry.CritterType;

public class TameMazeSlime extends EntitySlimeBase {

	public TameMazeSlime(World world) {
		super(world, CritterType.MAZESLIME);
	}

	@Override
	protected void applyAttackEffects(EntityLivingBase e) {

	}

	@Override
	public boolean canBeHurtBy(DamageSource dsc) {
		return dsc != DamageSource.fall && !dsc.isExplosion() && dsc != DamageSource.drown;
	}

}
