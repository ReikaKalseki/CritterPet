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

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Java.ReikaReflectionHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.SpiderPet.Registry.SpiderType;

public abstract class EntitySpiderBase extends EntitySpider {

	private SpiderType base;

	public EntitySpiderBase(World world, SpiderType sp) {
		super(world);
		base = sp;
		this.setSize(1.8F*sp.size, 1.1F*sp.size/2);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(this.getSpiderMaxHealth());
		this.setHealth(this.getSpiderMaxHealth());
		stepHeight = 1.25F;
		experienceValue = 0;
		height = 1.25F*base.size;
	}

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

	@Override
	protected final void entityInit() {
		super.entityInit();
		dataWatcher.addObject(31, Byte.valueOf((byte)0)); //Set not sitting
		dataWatcher.addObject(30, ""); //Set empty owner
	}

	private void setOwner(String owner) {
		dataWatcher.updateObject(30, owner);
	}

	public final EntitySpiderBase setOwner(EntityPlayer ep) {
		String owner = ep.getEntityName();
		this.setOwner(owner);
		return this;
	}

	public final String getOwner() {
		return dataWatcher.getWatchableObjectString(30);
	}

	public final boolean hasOwner() {
		String s = this.getOwner();
		return s == null || s.isEmpty();
	}

	public final SpiderType getBaseSpider() {
		return base;
	}

	public final boolean isSitting() {
		return dataWatcher.getWatchableObjectByte(31) == 1;
	}

	public final void setSitting(boolean sit) {
		byte s = (byte)(sit ? 1 : 0);
		dataWatcher.updateObject(31, s);
	}

	public final boolean isVanillaSpider() {
		return !this.isModSpider();
	}

	private boolean isModSpider() {
		return base != null;
	}

	public final int getSpiderMaxHealth() {
		if (base == null)
			return 100;
		return base.maxHealth;
	}

	@Override
	public final boolean shouldRiderFaceForward(EntityPlayer player) {
		return true;
	}

	protected abstract void updateRider();

	@Override
	public final void onUpdate() {
		boolean preventDespawn = false;
		if (!worldObj.isRemote && worldObj.difficultySetting == 0) { //the criteria for mob despawn in peaceful
			preventDespawn = true;
			worldObj.difficultySetting = 1;
		}
		super.onUpdate();
		if (preventDespawn)
			worldObj.difficultySetting = 0;
		this.updateRider();
		if (entityToAttack != null && entityToAttack.getEntityName().equals(this.getOwner()))
			entityToAttack = null;
		if (riddenByEntity != null)
			this.followOwner();
		if (this.isSitting()) {

		}
	}

	private void followOwner() {
		World world = worldObj;
		double x = posX;
		double y = posY;
		double z = posZ;
		Vec3 v = riddenByEntity.getLookVec();
		rotationPitch = 0;
	}

	@Override
	protected final Entity findPlayerToAttack()
	{
		return null;
	}

	@Override
	protected final void attackEntity(Entity e, float par2)
	{
		if (e.getEntityName().equals(this.getOwner()))
			return;
		super.attackEntity(e, par2);
		if (e instanceof EntityLivingBase)
			this.applyAttackEffects((EntityLivingBase)e);
	}

	protected abstract void applyAttackEffects(EntityLivingBase e);

	@Override
	protected final boolean interact(EntityPlayer ep)
	{
		ItemStack is = ep.getCurrentEquippedItem();
		String owner = this.getOwner();
		if (owner == null || owner.isEmpty()) {
			if (is != null && is.itemID == base.tamingItem.itemID) {
				owner = ep.getEntityName();
				//ReikaChatHelper.writeString("This spider had no owner! You are now the owner.");
				return true;
			}
			return false;
		}
		if (ep.getEntityName().equals(owner)) {
			if (is != null) {
				if (is.itemID == Item.beefCooked.itemID && this.getHealth() < this.getMaxHealth()) {
					this.heal(8);
					if (!ep.capabilities.isCreativeMode)
						is.stackSize--;
					return true;
				}
				if (is.itemID == Item.bone.itemID) {
					this.setSitting(!this.isSitting());
					return true;
				}
				boolean flag = super.interact(ep);
				if (flag)
					return true;
			}
			if (!worldObj.isRemote) {
				if (riddenByEntity != null && riddenByEntity.equals(ep)) {
					ep.dismountEntity(this);
				}
				else {
					ep.mountEntity(this);
				}
			}
			return true;
		}
		else {
			ReikaChatHelper.writeString("You do not own this spider.");
			return false;
		}
	}

	@Override
	public final boolean attackEntityFrom(DamageSource dsc, float par2)
	{
		if (this.isEntityInvulnerable())
		{
			return false;
		}
		else if (!this.canBeHurtBy(dsc)) {
			return false;
		}
		else if (super.attackEntityFrom(dsc, par2))
		{
			Entity entity = dsc.getEntity();

			if (riddenByEntity != entity && ridingEntity != entity)
			{
				if (entity != this && !entity.getEntityName().equals(this.getOwner()))
				{
					entityToAttack = entity;
				}

				return true;
			}
			else
			{
				return true;
			}
		}
		else
		{
			return false;
		}
	}

	public abstract boolean canBeHurtBy(DamageSource dsc);

	@Override
	public final String getEntityName() {
		return this.hasCustomNameTag() ? this.getCustomNameTag() : this.getDefaultName();
	}

	private final String getDefaultName() {
		String owner = this.getOwner();
		String sg = base.getName()+" Spider";
		if (owner == null || owner.isEmpty())
			return sg;
		else
			return owner+"'s "+sg;
	}

	@Override
	protected final boolean isMovementBlocked()
	{
		return this.isSitting() || riddenByEntity != null;
	}

	@Override
	public final void moveEntityWithHeading(float par1, float par2)
	{
		if (riddenByEntity != null) {
			prevRotationYaw = rotationYaw = riddenByEntity.rotationYaw;
			rotationPitch = riddenByEntity.rotationPitch * 0.5F;
			this.setRotation(rotationYaw, rotationPitch);
			rotationYawHead = renderYawOffset = rotationYaw;
			par1 = ((EntityLivingBase)riddenByEntity).moveStrafing * 0.5F;
			par2 = ((EntityLivingBase)riddenByEntity).moveForward;
			boolean jump = ReikaReflectionHelper.getPrivateBoolean(riddenByEntity, DragonAPICore.isDeObfEnvironment() ? "isJumping" : "field_70703_bu", SpiderPet.instance.getModLogger());
			if (jump) {
				if (onGround) {
					if (this.isBesideClimbableBlock()) {

					}
					else {
						motionY += 0.7*Math.pow(base.size, 0.25);
						this.setJumping(true);
						ForgeHooks.onLivingJump(this);
						//this.jump();
					}
				}
			}
			else
				this.setBesideClimbableBlock(false);
		}
		super.moveEntityWithHeading(par1, par2);
	}

	@Override
	public final float getAIMoveSpeed()
	{
		float sp = 0.11F;
		sp *= Math.sqrt(base.size);
		if (riddenByEntity != null) {
			sp *= riddenByEntity.isSprinting() ? 1.414 : 1;
		}
		PotionEffect pot = this.getActivePotionEffect(Potion.moveSpeed);
		if (pot != null) {
			sp += 0.01*(pot.getAmplifier()+1);
		}
		return sp;
	}

	public final float getScaleFactor() {
		return base.size;
	}

	public final void bindTexture() {
		ReikaTextureHelper.bindTexture(SpiderPet.class, base.texture);
	}

	@Override
	public final boolean getAlwaysRenderNameTagForRender()
	{
		return riddenByEntity == null;
	}

	@Override
	public final int getTalkInterval()
	{
		return (this.isSitting() ? 16 : 4)*(riddenByEntity != null ? 240 : 80);
	}

	@Override
	protected final void playStepSound(int par1, int par2, int par3, int par4)
	{
		this.playSound("mob.spider.step", this.getStepSoundVolume(), (float) (1.0F/Math.sqrt(base.size)));
	}

	public final float getStepSoundVolume() {
		return riddenByEntity != null ? 0.05F : 0.15F;
	}

	@Override
	public final double getMountedYOffset()
	{
		return base.size*0.85;
	}

	@Override
	public final boolean shouldRenderInPass(int pass) {
		return pass <= 1;
	}

	@Override
	public final float getShadowSize()
	{
		return width*4;
	}

	@Override
	public final boolean canBePushed() {
		return false;
	}

	@Override
	protected final void fall(float h) {
		h = Math.max(h-3, 0);
		super.fall(h);
	}

	@Override
	public final String toString() {
		return this.getEntityName()+" @ "+String.format("%.1f, %.1f, %.1f", posX, posY, posZ);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	public void writeEntityToNBT(NBTTagCompound NBT)
	{
		super.writeEntityToNBT(NBT);

		String s = this.getOwner();
		if (s == null)
			NBT.setString("Owner", "");
		else
			NBT.setString("Owner", s);

		NBT.setBoolean("Sitting", this.isSitting());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound NBT)
	{
		super.readEntityFromNBT(NBT);
		String s = NBT.getString("Owner");

		if (s.length() > 0) {
			this.setOwner(s);
		}
		this.setSitting(NBT.getBoolean("Sitting"));
	}

	@Override
	protected int getDropItemId()
	{
		return 0;
	}

	@Override
	protected void dropFewItems(boolean par1, int par2)
	{

	}

	@Override
	protected void collideWithNearbyEntities()
	{
		List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

		if (list != null && !list.isEmpty())
		{
			for (int i = 0; i < list.size(); ++i)
			{
				Entity entity = (Entity)list.get(i);

				if (entity.canBePushed())
				{
					this.collideWithEntity(entity);
				}
			}
		}
	}

	@Override
	protected void collideWithEntity(Entity e)
	{
		e.applyEntityCollision(this);
		if (e instanceof EntityLivingBase && !(e instanceof EntitySpiderBase)) {
			if (ReikaEntityHelper.isHostile((EntityLivingBase)e)) {
				//this.attackEntity(e, this.getAttackDamage());
				e.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttackDamage());
				this.applyAttackEffects((EntityLivingBase)e);
			}
		}
	}

	public abstract int getAttackDamage();
}
