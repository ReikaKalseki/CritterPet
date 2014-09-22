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

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import Reika.CritterPet.Registry.CritterType;

public class TameSlime extends EntitySpiderBase {

	public TameSlime(World world) {
		super(world, CritterType.SLIME);
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
