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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.CritterPet.Entities.Base.EntitySpiderBase;
import Reika.CritterPet.Registry.CritterType;

public class TameSlimeBeetle extends EntitySpiderBase {

	public TameSlimeBeetle(World world) {
		super(world, CritterType.SLIMEBEETLE);
	}

	@Override
	protected void updateRider() {
		this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 20, 2));
	}

	@Override
	protected void applyAttackEffects(EntityLivingBase e) {
		e.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 1));
	}

	@Override
	public boolean canBeHurtBy(DamageSource dsc) {
		return !dsc.isProjectile();
	}

	@Override
	public int getAttackDamage() {
		return 8;
	}

	@Override
	public boolean isRideable() {
		return true;
	}

}
