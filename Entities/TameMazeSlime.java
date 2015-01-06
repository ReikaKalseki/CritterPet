/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet.Entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import Reika.CritterPet.Registry.CritterType;

public class TameMazeSlime extends EntitySlimeBase {

	public TameMazeSlime(World world) {
		super(world, CritterType.MAZE);
	}

	@Override
	protected void updateRider() {
		riddenByEntity.fallDistance = 0;
		if (riddenByEntity instanceof EntityLivingBase) {
			EntityLivingBase rider = (EntityLivingBase)riddenByEntity;
			rider.addPotionEffect(new PotionEffect(Potion.jump.id, 5, 10));
		}
	}

	@Override
	protected void applyAttackEffects(EntityLivingBase e) {

	}

	@Override
	public boolean canBeHurtBy(DamageSource dsc) {
		return dsc != DamageSource.fall && !dsc.isExplosion() && dsc != DamageSource.drown;
	}

}
