/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.SpiderPet;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import Reika.DragonAPI.ModRegistry.ModSpiderList;

public abstract class EntitySpiderBase extends EntityLiving {

	private ModSpiderList base;

	public EntitySpiderBase(World world, ModSpiderList sp) {
		super(world);
		base = sp;
	}

	public ModSpiderList getBaseSpider() {
		return base;
	}

	public boolean isVanillaSpider() {
		return !this.isModSpider();
	}

	private boolean isModSpider() {
		return base != null;
	}

	@Override
	public final int getMaxHealth() {
		if (this.isVanillaSpider()) {
			return 16;
		}
		return base.getHealth();
	}

	@Override
	public final boolean shouldRiderFaceForward(EntityPlayer player) {
		return true;
	}

	public abstract void updateRider();

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.updateRider();
	}
}
