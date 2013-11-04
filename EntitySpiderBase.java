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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaReflectionHelper;

public abstract class EntitySpiderBase extends EntitySpider {

	private SpiderType base;
	private String owner;
	private boolean isSitting;

	public EntitySpiderBase(World world, SpiderType sp) {
		super(world);
		base = sp;
		this.setSize(1.8F*sp.size, 1.1F*sp.size/2);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(this.getSpiderMaxHealth());
		this.setHealth(this.getSpiderMaxHealth());
		stepHeight = 1.25F;
	}

	public EntitySpiderBase setOwner(EntityPlayer ep) {
		owner = ep.getEntityName();
		return this;
	}

	public SpiderType getBaseSpider() {
		return base;
	}

	public boolean isVanillaSpider() {
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
		if (entityToAttack != null && entityToAttack.getEntityName().equals(owner))
			entityToAttack = null;
		if (riddenByEntity != null)
			this.followOwner();
		if (isSitting) {
			//rotationYaw = 0;
			//rotationYawHead = -45;
			//rotationPitch = 0;
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
		if (e.getEntityName().equals(owner))
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
		if (owner == null || owner.isEmpty()) {
			if (is != null && is.itemID == base.tamingItem.itemID);
			owner = ep.getEntityName();
			//ReikaChatHelper.writeString("This spider had no owner! You are now the owner.");
			return true;
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
					isSitting = !isSitting;
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
		}
		else {
			ReikaChatHelper.writeString("You do not own this spider.");
		}
		return true;
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
				if (entity != this && !entity.getEntityName().equals(owner))
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
	public String getEntityName() {
		return this.hasCustomNameTag() ? this.getCustomNameTag() : this.getDefaultName();
	}

	private String getDefaultName() {
		String sg = base.getName()+" Spider";
		if (owner == null)
			return sg;
		else
			return owner+"'s "+sg;
	}

	@Override
	protected final boolean isMovementBlocked()
	{
		return isSitting || riddenByEntity != null;
	}

	@Override
	public void moveEntityWithHeading(float par1, float par2)
	{
		if (riddenByEntity != null)
		{
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
						motionY += 0.7;
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
	public float getAIMoveSpeed()
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

	public float getScaleFactor() {
		return base.size;
	}

	public void bindTexture() {
		ReikaTextureHelper.bindTexture(SpiderPet.class, base.texture);
	}

	@Override
	public boolean getAlwaysRenderNameTagForRender()
	{
		return riddenByEntity == null;
	}

	@Override
	public int getTalkInterval()
	{
		return riddenByEntity != null ? 240 : 80;
	}

	@Override
	protected void playStepSound(int par1, int par2, int par3, int par4)
	{
		this.playSound("mob.spider.step", this.getStepSoundVolume(), 1.0F);
	}

	public float getStepSoundVolume() {
		return riddenByEntity != null ? 0.05F : 0.15F;
	}

	@Override
	public double getMountedYOffset()
	{
		return height*1.4;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass <= 1;
	}

	@Override
	public float getShadowSize()
	{
		return width*4;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	public boolean isSitting() {
		return isSitting;
	}
}
