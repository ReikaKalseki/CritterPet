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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class TameHeatScar extends EntitySpiderBase {

	public TameHeatScar(World par1World) {
		super(par1World, CritterType.HEATSCAR);
	}

	@Override
	protected void updateRider() {
		if (riddenByEntity instanceof EntityLivingBase) {
			EntityLivingBase rider = (EntityLivingBase)riddenByEntity;
			rider.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 20, 0));
		}
	}

	@Override
	protected void applyAttackEffects(EntityLivingBase e) {
		e.setFire(6);
	}

	@Override
	public boolean canBeHurtBy(DamageSource dsc) {
		return !dsc.isFireDamage();
	}

	@Override
	public int getAttackDamage() {
		return 4;
	}
}