/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.SpiderPet.Entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import Reika.SpiderPet.EntitySpiderBase;
import Reika.SpiderPet.Registry.SpiderType;

public class TameHeatScar extends EntitySpiderBase {

	public TameHeatScar(World par1World) {
		super(par1World, SpiderType.HEATSCAR);
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
		e.setFire(5);
	}

	@Override
	public boolean canBeHurtBy(DamageSource dsc) {
		return !dsc.isFireDamage();
	}
}
