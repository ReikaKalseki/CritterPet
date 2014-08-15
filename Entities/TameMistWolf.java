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


public class TameMistWolf extends TamedWolfBase {

	public TameMistWolf(World world) {
		super(world, CritterType.MISTWOLF);
	}

	@Override
	protected void updateRider() {

	}

	@Override
	protected void applyAttackEffects(EntityLivingBase e) {
		e.addPotionEffect(new PotionEffect(Potion.blindness.id, 300, 1));
	}

	@Override
	public boolean canBeHurtBy(DamageSource dsc) {
		return !dsc.isMagicDamage();
	}

	@Override
	public int getAttackDamage() {
		return 8;
	}

	@Override
	public float getBrightness(float par1)
	{
		return riddenByEntity != null ? 1 : entityToAttack != null ? 0 : super.getBrightness(par1);
	}

	@Override
	public boolean isRideable() {
		return true;
	}


}