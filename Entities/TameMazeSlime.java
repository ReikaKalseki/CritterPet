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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import Reika.CritterPet.Registry.CritterType;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;

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
			if (riddenByEntity instanceof EntityPlayer) {
				if (KeyWatcher.instance.isKeyDown((EntityPlayer)riddenByEntity, Key.LEFT)) {
					rotationYaw -= 5;
				}
				else if (KeyWatcher.instance.isKeyDown((EntityPlayer)riddenByEntity, Key.RIGHT)) {
					rotationYaw += 5;
				}
				else if (KeyWatcher.instance.isKeyDown((EntityPlayer)riddenByEntity, Key.FORWARD)) {
					float par1 = rider.moveStrafing * 0.5F;
					float par2 = rider.moveForward;
					this.moveEntityWithHeading(par1, par2);
				}
			}
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
