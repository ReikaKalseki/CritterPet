/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet.Entities.Mod;

import java.util.Iterator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.CritterPet.Interfaces.TamedMob;
import Reika.CritterPet.Registry.CritterType;
import Reika.DragonAPI.Interfaces.Entity.TameHostile;
import Reika.DragonAPI.Interfaces.Item.EntityCapturingItem;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.Satisforestry.SFAux;
import Reika.Satisforestry.Blocks.BlockPowerSlug.TilePowerSlug;
import Reika.Satisforestry.Entity.EntitySpitter;
import Reika.Satisforestry.Entity.AI.EntityAISpitterFireball;
import Reika.Satisforestry.Entity.AI.EntityAISpitterReposition;
import Reika.Satisforestry.Registry.SFBlocks;

public class TameSpitter extends EntitySpitter implements TamedMob, TameHostile {

	public TameSpitter(World world) {
		super(world);

		Iterator<EntityAITaskEntry> it = tasks.taskEntries.iterator();
		while (it.hasNext()) {
			EntityAITaskEntry e = it.next();
			EntityAIBase ai = e.action;
			if (ai instanceof EntityAISpitterReposition || ai instanceof EntityAIWander)
				it.remove();
		}
	}

	@Override
	public int getTalkInterval() {
		return super.getTalkInterval()*2;
	}

	@Override
	public final String getCommandSenderName() {
		return this.hasCustomNameTag() ? this.getCustomNameTag() : this.getDefaultName();
	}

	private final String getDefaultName() {
		String owner = this.getMobOwner();
		String sg = super.getCommandSenderName();
		if (owner == null || owner.isEmpty())
			return sg;
		else
			return owner+"'s "+sg;
	}

	@Override
	public final boolean attackEntityFrom(DamageSource dsc, float par2) {
		return super.attackEntityFrom(dsc, par2);
	}

	@Override
	public void setAttackTarget(EntityLivingBase e) {
		if (e instanceof EntityPlayer && e.getCommandSenderName().equals(this.getMobOwner())) {
			return;
		}
		super.setAttackTarget(e);
	}

	@Override
	public void onUpdate()
	{
		boolean preventDespawn = false;
		if (!worldObj.isRemote && worldObj.difficultySetting == EnumDifficulty.PEACEFUL) { //the criteria for mob despawn in peaceful
			preventDespawn = true;
			worldObj.difficultySetting = EnumDifficulty.EASY;
		}
		super.onUpdate();
		if (preventDespawn)
			worldObj.difficultySetting = EnumDifficulty.PEACEFUL;

		if (riddenByEntity != null)
			;//this.updateRider();
		if (entityToAttack != null && entityToAttack.getCommandSenderName().equals(this.getMobOwner()))
			entityToAttack = null;
		this.teleportAsNecessary();
		if (riddenByEntity != null)
			this.followOwner();
		else if (rand.nextInt(20) == 0)
			isJumping = false;

		if (!worldObj.isRemote) {
			EntityLivingBase target = this.findNearTarget();
			if (target != null) {
				if (target.getDistanceSqToEntity(this) <= 6.25)
					this.doKnockbackBlast(null);
				else
					this.fireFireballAt(target);
			}

			//ReikaJavaLibrary.pConsole("TT W "+this.getEquipmentInSlot(4));
		}
		else if (riddenByEntity == null) {
			int tier = this.getSlugTier();
			if (tier >= 0) {
				TilePowerSlug.doFX(worldObj, posX, posY+0.675+this.getMountedYOffset(), posZ, tier, ForgeDirection.DOWN, this);
			}
		}
	}

	public final EntityPlayer findOwner() {
		String s = this.getMobOwner();
		if (s != null && !s.isEmpty())
			return worldObj.getPlayerEntityByName(s);
		return null;
	}

	private void teleportAsNecessary() {
		EntityPlayer owner = this.findOwner();
		if (owner != null && !this.isSitting()) {
			if (!this.getLeashed()) {
				double dd = this.getDistanceSqToEntity(owner);
				if (dd >= 256.0D) {
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
				else if (dd >= 16) {
					this.getNavigator().tryMoveToEntityLiving(owner, dd >= 144 ? 4.5 : (dd >= 36 ? 2 : 1));
				}
			}
		}
	}

	@Override
	protected void updateEntityActionState()
	{
		super.updateEntityActionState();
	}

	@Override
	public final boolean getAlwaysRenderNameTagForRender()
	{
		return true;
	}

	@Override
	public final boolean canBePushed() {
		return false;
	}

	@Override
	public final double getMountedYOffset()
	{
		return this.getSpitterType().isAlpha() ? 1.7 : 1.2;
	}

	@Override
	public boolean shouldDismountInWater(Entity rider) {
		return false;
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
			if (((EntityLivingBase)riddenByEntity).isJumping) {
				if (onGround) {
					motionY += 0.7;
					this.setJumping(true);
					ForgeHooks.onLivingJump(this);
					//this.jump();
				}
			}
		}
		super.moveEntityWithHeading(par1, par2);
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

		NBT.setBoolean("Sitting", this.isSitting());
	}

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
	protected Item getDropItem()
	{
		return null;
	}

	@Override
	protected void dropFewItems(boolean flag, int i)
	{

	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	protected final void entityInit() {
		super.entityInit();

		//dataWatcher.addObject(16, new Byte((byte)0));

		dataWatcher.addObject(30, ""); //Set empty owner
		dataWatcher.addObject(31, Byte.valueOf((byte)0)); //Set not sitting
	}

	public int getSlugTier() {
		return SFAux.getSlugHelmetTier(this)-1;
		//return dataWatcher.getWatchableObjectByte(16)-1;
	}

	private void setSlugTier(ItemStack slug) {
		this.setCurrentItemOrArmor(4, ReikaItemHelper.getSizedItemStack(slug, 1));
		//dataWatcher.updateObject(16, (byte)(1+slug.getItemDamage()%3));
	}

	private void dropCurrentSlug() {
		int tier = this.getSlugTier();
		if (tier >= 0) {
			ReikaItemHelper.dropItem(this, SFBlocks.SLUG.getStackOfMetadata(tier));
		}
	}

	@Override
	public float getDamageScale(EntityLivingBase tgt) {
		return (float)(super.getDamageScale(tgt)+0.125*Math.pow(2, this.getSlugTier()+1)); //x1.25, x1.5, x2.0
	}

	@Override
	public float getFireRateScale(EntityAISpitterFireball ai) {
		return (float)(super.getFireRateScale(ai)+0.25*Math.pow(2, this.getSlugTier()+1)); //x1.5, x2.0, x3.0
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

	public final boolean hasOwner() {
		String s = this.getMobOwner();
		return s != null && !s.isEmpty();
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
			ReikaParticleHelper.HEART.spawnAt(world, rx, y+0.8, rz, 0, 0, 0);
			ReikaWorldHelper.splitAndSpawnXP(world, rx, y+0.5, rz, 1+rand.nextInt(5));
		}
		this.playSound("random.levelup", 1, 1);
	}

	@Override
	public final CritterType getBaseCritter() {
		return CritterType.SPITTER;
	}

	@Override
	public final boolean isVanillaCritter() {
		return false;
	}

	@Override
	public final boolean isModCritter() {
		return true;
	}

	@Override
	public final int getCritterMaxHealth() {
		return this.getSpitterType().health;
	}

	@Override
	public boolean isInRangeToRenderDist(double dist) {
		return true;
	}

	public final void setSitting(boolean sit) {
		byte s = (byte)(sit ? 1 : 0);
		dataWatcher.updateObject(31, s);
	}

	public final boolean isSitting() {
		return dataWatcher.getWatchableObjectByte(31) == 1;
	}

	private void followOwner() {
		World world = worldObj;
		double x = posX;
		double y = posY;
		double z = posZ;
		Vec3 v = riddenByEntity.getLookVec();
		rotationPitch = 0;
		if (riddenByEntity instanceof EntityPlayer) {
			isJumping = ((EntityPlayer)riddenByEntity).isJumping;
		}
	}

	@Override
	protected final boolean interact(EntityPlayer ep)
	{
		ItemStack is = ep.getCurrentEquippedItem();
		String owner = this.getMobOwner();
		if (owner == null || owner.isEmpty()) {
			if (is != null && is.getItem() == CritterType.SPITTER.tamingItem) {
				owner = ep.getCommandSenderName();
				//ReikaChatHelper.writeString("This critter had no owner! You are now the owner.");
				return true;
			}
			return false;
		}
		if (ep.getCommandSenderName().equals(owner)) {
			if (is != null) {
				if (ReikaItemHelper.matchStackWithBlock(is, Blocks.brown_mushroom) && this.getHealth() < this.getMaxHealth()) {
					this.heal(3);
					if (!ep.capabilities.isCreativeMode)
						is.stackSize--;
					return true;
				}
				if (is.getItem() == Items.bone) {
					this.setSitting(!this.isSitting());
					return true;
				}
				if (!worldObj.isRemote && SFBlocks.SLUG.matchWith(is)) {
					this.dropCurrentSlug();
					if (ep.isSneaking()) {
						this.setSlugTier(null);
					}
					else {
						this.setSlugTier(is);
						is.stackSize--;
					}
					return true;
				}
				if (is.getItem() == Items.name_tag) {
					return false;
				}
				boolean flag = super.interact(ep);
				if (flag)
					return true;
			}
			if (!worldObj.isRemote) {
				if (riddenByEntity != null && riddenByEntity.equals(ep)) {
					ep.dismountEntity(this);
				}
				else if (this.getSpitterType().isAlpha() && ep.getCurrentEquippedItem() == null) {
					ep.mountEntity(this);
					this.setSitting(false);
				}
			}
			return is == null || !(is.getItem() instanceof EntityCapturingItem);
		}
		else {
			ReikaChatHelper.writeString("You do not own this critter.");
			return false;
		}
	}

}
