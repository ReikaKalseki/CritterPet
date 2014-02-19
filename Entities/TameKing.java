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
import Reika.SpiderPet.Registry.SpiderType;

public class TameKing extends EntitySpiderBase {

	public TameKing(World par1World) {
		super(par1World, SpiderType.KING);
	}

	@Override
	protected void updateRider() {
		if (riddenByEntity instanceof EntityLivingBase) {
			EntityLivingBase rider = (EntityLivingBase)riddenByEntity;
			rider.removePotionEffect(Potion.poison.id);
		}
	}

	@Override
	protected void applyAttackEffects(EntityLivingBase e) {
		e.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));
	}

	@Override
	public boolean canBeHurtBy(DamageSource dsc) {
		if (dsc.isMagicDamage())
			return false;
		return true;
	}

	@Override
	public int getAttackDamage() {
		return 4;
	}
}
