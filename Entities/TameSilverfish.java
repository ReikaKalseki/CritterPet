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

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import Reika.CritterPet.CritterPet;
import Reika.CritterPet.Interfaces.TamedMob;
import Reika.CritterPet.Registry.CritterType;
import Reika.DragonAPI.Interfaces.Entity.TameHostile;
import Reika.DragonAPI.Interfaces.Item.EntityCapturingItem;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TameSilverfish extends EntitySilverfish implements TamedMob, TameHostile {

	public TameSilverfish(World world) {
		super(world);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getCritterMaxHealth());
		this.setHealth(this.getCritterMaxHealth());
		stepHeight = 1.25F;
		experienceValue = 0;
		this.func_110163_bv();
	}

	@Override
	public CritterType getBaseCritter() {
		return CritterType.SILVERFISH;
	}

	@Override
	public boolean isVanillaCritter() {
		return true;
	}

	@Override
	public boolean isModCritter() {
		return false;
	}

	@Override
	public int getCritterMaxHealth() {
		return 12; //8 on untame
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
	protected final void entityInit() {
		super.entityInit();
		dataWatcher.addObject(30, ""); //Set empty owner

		dataWatcher.addObject(20, 0.0F);
		dataWatcher.addObject(21, 0.0F);
		dataWatcher.addObject(22, 0.0F);
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

	@Override
	public final void onUpdate() {
		boolean preventDespawn = false;
		if (!worldObj.isRemote && worldObj.difficultySetting == EnumDifficulty.PEACEFUL) { //the criteria for mob despawn in peaceful
			preventDespawn = true;
			worldObj.difficultySetting = EnumDifficulty.EASY;
		}
		super.onUpdate();
		//if (motionX != 0 || motionY != 0 || motionZ != 0)
		//	velocityChanged = true;
		if (preventDespawn)
			worldObj.difficultySetting = EnumDifficulty.PEACEFUL;
		if (entityToAttack != null && entityToAttack.getCommandSenderName().equals(this.getMobOwner()))
			entityToAttack = null;

		this.teleportAsNecessary();

		if (worldObj.isRemote) {
			this.readVelocity();
		}
		else {
			this.updateVelocity();
		}
		if (ReikaEntityHelper.isInRain(this)) {
			if (rand.nextInt(40) == 0) {
				this.playSound(this.getHurtSound(), 1, 0.75F+0.5F*rand.nextFloat());
			}
		}
	}

	private void readVelocity() {
		motionX = dataWatcher.getWatchableObjectFloat(20);
		motionY = dataWatcher.getWatchableObjectFloat(21);
		motionZ = dataWatcher.getWatchableObjectFloat(22);
	}

	private void updateVelocity() {
		dataWatcher.updateObject(20, (float)motionX);
		dataWatcher.updateObject(21, (float)motionY);
		dataWatcher.updateObject(22, (float)motionZ);
	}

	@Override
	protected void updateWanderPath()
	{
		super.updateWanderPath();
	}

	@Override
	protected final Entity findPlayerToAttack()
	{
		return this.getMobOwner() != null ? worldObj.getPlayerEntityByName(this.getMobOwner()) : null; //follow owner
	}

	@Override
	protected final boolean interact(EntityPlayer ep)
	{
		ItemStack is = ep.getCurrentEquippedItem();
		if (is != null && is.getItem() instanceof EntityCapturingItem)
			return false;
		String owner = this.getMobOwner();
		if (owner == null || owner.isEmpty()) {
			if (is != null && is.getItem() == CritterType.SILVERFISH.tamingItem) {
				owner = ep.getCommandSenderName();
				//ReikaChatHelper.writeString("This critter had no owner! You are now the owner.");
				return true;
			}
			return false;
		}
		if (ep.getCommandSenderName().equals(owner)) {
			if (is != null) {
				if (is.getItem() == Items.cooked_beef && this.getHealth() < this.getMaxHealth()) {
					this.heal(8);
					if (!ep.capabilities.isCreativeMode)
						is.stackSize--;
					return true;
				}
				if (is.getItem() == Items.name_tag || is.getItem() == Items.lead) {
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
			ReikaParticleHelper.HEART.spawnAt(worldObj, rx, y+0.8, rz, 0, 0, 0);
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

	@Override
	public final String getCommandSenderName() {
		return this.hasCustomNameTag() ? this.getCustomNameTag() : this.getDefaultName();
	}

	private final String getDefaultName() {
		String owner = this.getMobOwner();
		String sg = CritterType.SILVERFISH.name;
		if (owner == null || owner.isEmpty())
			return sg;
		else
			return owner+"'s "+sg;
	}

	@Override
	public final float getAIMoveSpeed()
	{
		return super.getAIMoveSpeed();
	}

	@SideOnly(Side.CLIENT)
	public final void bindTexture() {
		ReikaTextureHelper.bindTexture(CritterPet.class, CritterType.SILVERFISH.texture);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean getAlwaysRenderNameTagForRender() {
		return this.getDistanceSqToEntity(Minecraft.getMinecraft().thePlayer) < 16;//true;
	}

	@Override
	public final int getTalkInterval()
	{
		return 80;
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
		if (e instanceof EntityLivingBase && !(e instanceof TamedMob)) {
			if (ReikaEntityHelper.isHostile((EntityLivingBase)e) || this.isNonOwnerPlayer(e)) {
				//this.attackEntity(e, this.getAttackDamage());
				e.attackEntityFrom(DamageSource.causeMobDamage(this), 1);
			}
		}
	}

	protected final boolean isNonOwnerPlayer(Entity e) {
		return e instanceof EntityPlayer && !e.getCommandSenderName().equals(this.getMobOwner());
	}

	private void teleportAsNecessary() {
		EntityPlayer owner = this.findOwner();
		if (owner != null && !this.getLeashed()) {
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
