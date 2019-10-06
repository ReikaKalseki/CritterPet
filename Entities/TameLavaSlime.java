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
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.CritterPet.Entities.Base.EntitySlimeBase;
import Reika.CritterPet.Registry.CritterType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TameLavaSlime extends EntitySlimeBase {

	public TameLavaSlime(World world) {
		super(world, CritterType.LAVASLIME);
		isImmuneToFire = true;
	}

	@Override
	protected boolean followLook() {
		return true;
	}

	@Override
	protected void applyAttackEffects(EntityLivingBase e) {
		e.setFire(1);
	}

	@Override
	public boolean canBeHurtBy(DamageSource dsc) {
		return !dsc.isFireDamage() && dsc != DamageSource.fall;
	}

	@Override
	public int getTotalArmorValue() {
		return this.getSlimeSize() * 3;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float p_70070_1_)
	{
		return 15728880;
	}

	@Override
	public float getBrightness(float p_70013_1_)
	{
		return 1.0F;
	}

	@Override
	protected String getSlimeParticle()
	{
		return "flame";
	}

	@Override
	public boolean isBurning()
	{
		return false;
	}

	@Override
	protected void alterSquishAmount()
	{
		squishAmount *= 0.9F;
	}

	@Override
	protected void jump()
	{
		motionY = 0.42F + this.getSlimeSize() * 0.1F;
		isAirBorne = true;
		net.minecraftforge.common.ForgeHooks.onLivingJump(this);
	}

	@Override
	protected void fall(float p_70069_1_) {}

	@Override
	protected int getAttackStrength()
	{
		return super.getAttackStrength() + 2;
	}

	@Override
	protected String getJumpSound()
	{
		return this.getSlimeSize() > 1 ? "mob.magmacube.big" : "mob.magmacube.small";
	}

	@Override
	public boolean handleLavaMovement()
	{
		return false;
	}

	@Override
	protected boolean makesSoundOnLand()
	{
		return true;
	}

}
