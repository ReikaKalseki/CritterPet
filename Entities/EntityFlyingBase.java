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

import java.util.List;

import Reika.CritterPet.Interfaces.TamedMob;
import Reika.CritterPet.Registry.CritterType;
import Reika.DragonAPI.Interfaces.Entity.TameHostile;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public abstract class EntityFlyingBase extends EntityFlying implements TamedMob, TameHostile {

	private CritterType base;
	private Entity entityToAttack;

	public EntityFlyingBase(World par1World, CritterType sp) {
		super(par1World);
		base = sp;
		this.setSize(1.8F*sp.size, 1.1F*sp.size/2);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getCritterMaxHealth());
		this.setHealth(this.getCritterMaxHealth());
		experienceValue = 0;
		height = 1.25F*sp.size;
		this.func_110163_bv();
	}

	@Override
	protected final void entityInit() {
		super.entityInit();
		dataWatcher.addObject(30, ""); //Set empty owner
		dataWatcher.addObject(31, Byte.valueOf((byte)0)); //Set not sitting
	}

	private void setOwner(String owner) {
		dataWatcher.updateObject(30, owner);
	}

	public final void setOwner(EntityPlayer ep) {
		String owner = ep.getCommandSenderName();
		this.setOwner(owner);
	}

	public final String getMobOwner() {
		return dataWatcher.getWatchableObjectString(30);
	}

	public final EntityPlayer findOwner() {
		String s = this.getMobOwner();
		if (s != null && !s.isEmpty())
			return worldObj.getPlayerEntityByName(s);
		return null;
	}

	public final boolean hasOwner() {
		String s = this.getMobOwner();
		return s != null && !s.isEmpty();
	}

	public final boolean isSitting() {
		return dataWatcher.getWatchableObjectByte(31) == 1;
	}

	public final void setSitting(boolean sit) {
		byte s = (byte)(sit ? 1 : 0);
		dataWatcher.updateObject(31, s);
	}

	@Override
	public void spawnEffects() {
		World world = worldObj;
		double x = posX;
		double y = posY;
		double z = posZ;
		for (int i = 0; i < 12; i++) {
			double rx = ReikaRandomHelper.getRandomPlusMinus(x, 1);
			double rz = ReikaRandomHelper.getRandomPlusMinus(z, 1);
			ReikaParticleHelper.HEART.spawnAt(world, rx, y+this.getScaleFactor()*0.8, rz, 0, 0, 0);
			ReikaWorldHelper.splitAndSpawnXP(world, rx, y+0.5, rz, 1+rand.nextInt(5));
		}
		this.playSound("random.levelup", 1, 1);
	}

	public final float getScaleFactor() {
		return base.size;
	}

	@Override
	public final CritterType getBaseCritter() {
		return base;
	}

	@Override
	public final boolean isVanillaCritter() {
		return !this.isModCritter();
	}

	@Override
	public final boolean isModCritter() {
		return this.getBaseCritter().sourceMod != null;
	}

	@Override
	public final int getCritterMaxHealth() {
		return this.getBaseCritter().maxHealth;
	}

	@Override
	public final void onUpdate() {
		boolean preventDespawn = false;
		if (!worldObj.isRemote && worldObj.difficultySetting == EnumDifficulty.PEACEFUL) { //the criteria for mob despawn in peaceful
			preventDespawn = true;
			worldObj.difficultySetting = EnumDifficulty.EASY;
		}
		super.onUpdate();
		int y = MathHelper.floor_double(posY-1.2);
		int x = MathHelper.floor_double(posX);
		int z = MathHelper.floor_double(posZ);
		if (worldObj.getBlock(x, y, z) != Blocks.air && worldObj.getBlock(x, y+2, z) == Blocks.air) {
			motionY += 0.015;
		}
		else {
			motionY -= 0.015;
		}
		if (this.isSitting()) {
			rotationPitch = 0;
		}
		else {
			EntityPlayer ep = this.findOwner();
			if (ep != null) {
				double[] angs = ReikaPhysicsHelper.cartesianToPolar(ep.posX-posX, ep.posY-posY-1.5, ep.posZ-posZ);
				rotationYaw = (float)-angs[2]+180;
				rotationYawHead = rotationYaw;
				rotationPitch = (float)angs[1]-90;
				if (angs[0] > 5.2) {
					motionX += 0.007*Math.signum(ep.posX-posX);
					motionZ += 0.007*Math.signum(ep.posZ-posZ);
					if (ep.posY > posY+2)
						motionY += 0.075;
				}
			}
		}
		velocityChanged = true;
		this.teleportAsNecessary();
		if (preventDespawn)
			worldObj.difficultySetting = EnumDifficulty.PEACEFUL;
	}

	@Override
	protected void updateEntityActionState()
	{
		if (entityToAttack != null)
			this.faceEntity(entityToAttack, 10.0F, 30.0F);
		super.updateEntityActionState();
	}

	protected abstract void applyAttackEffects(EntityLivingBase e);

	@Override
	public final void onCollideWithPlayer(EntityPlayer ep) {
		if (!ep.getCommandSenderName().equals(this.getMobOwner()))
			super.onCollideWithPlayer(ep);
	}

	@Override
	protected final boolean interact(EntityPlayer ep)
	{
		ItemStack is = ep.getCurrentEquippedItem();
		String owner = this.getMobOwner();
		if (owner == null || owner.isEmpty()) {
			if (is != null && is.getItem() == base.tamingItem) {
				owner = ep.getCommandSenderName();
				//ReikaChatHelper.writeString("This critter had no owner! You are now the owner.");
				return true;
			}
			return false;
		}
		if (ep.getCommandSenderName().equals(owner)) {
			if (is != null) {
				if (is.getItem() == Items.glowstone_dust && this.getHealth() < this.getMaxHealth()) {
					this.heal(8);
					if (!ep.capabilities.isCreativeMode)
						is.stackSize--;
					return true;
				}
				if (is.getItem() == Items.bone) {
					this.setSitting(!this.isSitting());
					return true;
				}
				if (is.getItem() == Items.name_tag) {
					return false;
				}
				boolean flag = super.interact(ep);
				if (flag)
					return true;
			}
			return true;
		}
		else {
			ReikaChatHelper.writeString("You do not own this critter.");
			return false;
		}
	}

	@Override
	public void heal(float par1)
	{
		super.heal(par1);

		this.playLivingSound();

		double x = posX;
		double y = posY;
		double z = posZ;
		for (int i = 0; i < 6; i++) {
			double rx = ReikaRandomHelper.getRandomPlusMinus(x, 1);
			double rz = ReikaRandomHelper.getRandomPlusMinus(z, 1);
			ReikaParticleHelper.HEART.spawnAt(worldObj, rx, y+this.getScaleFactor()*0.8, rz, 0, 0, 0);
			//ReikaWorldHelper.splitAndSpawnXP(worldObj, rx, y+0.5, rz, 1+rand.nextInt(5));
		}
	}

	@Override
	public final boolean attackEntityFrom(DamageSource dsc, float par2)
	{
		if (this.isEntityInvulnerable()) {
			return false;
		}
		else if (dsc.getEntity() != null && dsc.getEntity().getCommandSenderName().equals(this.getMobOwner())) {
			return false;
		}
		else if (!this.canBeHurtBy(dsc)) {
			return false;
		}
		else if (super.attackEntityFrom(dsc, par2)) {
			Entity entity = dsc.getEntity();
			if (riddenByEntity != entity && ridingEntity != entity) {
				if (entity != this && !entity.getCommandSenderName().equals(this.getMobOwner()))
					entityToAttack = entity;
				return true;
			}
			else
				return true;
		}
		else
			return false;
	}

	public abstract boolean canBeHurtBy(DamageSource dsc);

	@Override
	public final String getCommandSenderName() {
		return this.hasCustomNameTag() ? this.getCustomNameTag() : this.getDefaultName();
	}

	private final String getDefaultName() {
		String owner = this.getMobOwner();
		String sg = base.name;
		if (owner == null || owner.isEmpty())
			return sg;
		else
			return owner+"'s "+sg;
	}

	@Override
	protected final boolean isMovementBlocked() {
		return this.isSitting();
	}

	@Override
	public final boolean getAlwaysRenderNameTagForRender()
	{
		return true;
	}

	@Override
	public final int getTalkInterval()
	{
		return 320;
	}

	@Override
	protected final float getSoundVolume()
	{
		return 0.15F;
	}

	@Override
	public final boolean shouldRenderInPass(int pass) {
		return pass <= 1;
	}

	@Override
	public final float getShadowSize()
	{
		return width*2;
	}

	@Override
	public final boolean canBePushed() {
		return false;
	}

	@Override
	public final String toString() {
		return this.getCommandSenderName()+" @ "+String.format("%.1f, %.1f, %.1f", posX, posY, posZ);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound NBT)
	{
		super.writeEntityToNBT(NBT);

		String s = this.getMobOwner();
		if (s == null)
			NBT.setString("Owner", "");
		else
			NBT.setString("Owner", s);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound NBT)
	{
		super.readEntityFromNBT(NBT);
		String s = NBT.getString("Owner");

		if (s.length() > 0) {
			this.setOwner(s);
		}
	}

	@Override
	protected final Item getDropItem()
	{
		return null;
	}

	@Override
	protected final void dropFewItems(boolean par1, int par2)
	{

	}

	@Override
	protected void collideWithNearbyEntities()
	{
		List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.2, 0.0D, 0.2));

		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); ++i) {
				Entity entity = (Entity)list.get(i);

				if (entity.canBePushed()) {
					this.collideWithEntity(entity);
				}
			}
		}
	}

	@Override
	protected void collideWithEntity(Entity e)
	{
		e.applyEntityCollision(this);
		if (e instanceof EntityLivingBase && !(e instanceof TamedMob)) {
			if (ReikaEntityHelper.isHostile((EntityLivingBase)e)) {
				//this.attackEntity(e, this.getAttackDamage());
				e.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttackDamage());
				this.applyAttackEffects((EntityLivingBase)e);
			}
		}
	}

	protected abstract float getAttackDamage();

	private void teleportAsNecessary() {
		EntityPlayer owner = this.findOwner();
		if (owner != null && !this.getLeashed() && !this.isSitting()) {
			if (this.getDistanceSqToEntity(owner) >= 144.0D) {
				int x = MathHelper.floor_double(owner.posX)-2;
				int z = MathHelper.floor_double(owner.posZ)-2;
				int y = MathHelper.floor_double(owner.boundingBox.minY);

				for (int i = 0; i <= 4; ++i) {
					for (int k = 0; k <= 4; ++k) {
						if ((i < 1 || k < 1 || i > 3 || k > 3) && World.doesBlockHaveSolidTopSurface(worldObj, x+i, y-1, z+k) && !worldObj.getBlock(x+i, y, z+k).isNormalCube() && !worldObj.getBlock(x+i, y+1, z+k).isNormalCube()) {
							this.setLocationAndAngles(x+i+0.5F, y, z+k+0.5F, rotationYaw, rotationPitch);
							return;
						}
					}
				}
			}
		}
	}

	@Override
	public final boolean allowLeashing() {
		return true;
	}

	@Override
	public boolean isInRangeToRenderDist(double dist) {
		return true;
	}

}
