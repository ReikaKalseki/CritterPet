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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import Reika.CritterPet.Registry.CritterType;

public class TameHedge extends EntitySpiderBase {

	public TameHedge(World par1World) {
		super(par1World, CritterType.HEDGE);
	}

	@Override
	protected void updateRider() {
		this.addPotionEffect(new PotionEffect(Potion.jump.id, 20, 4));
		this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 20, 4));
	}

	@Override
	protected void applyAttackEffects(EntityLivingBase e) {
		e.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 1));
	}

	@Override
	public boolean canBeHurtBy(DamageSource dsc) {
		if (dsc == DamageSource.fall)
			return false;
		return true;
	}

	@Override
	public int getAttackDamage() {
		return 3;
	}

	@Override
	public boolean isRideable() {
		return true;
	}
}
