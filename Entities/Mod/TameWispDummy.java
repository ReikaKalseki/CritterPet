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

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import Reika.CritterPet.Interfaces.TamedMob;
import Reika.CritterPet.Registry.CritterType;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Interfaces.Entity.TameHostile;
import Reika.DragonAPI.Interfaces.Item.EntityCapturingItem;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.monster.EntityWisp;

public class TameWispDummy extends EntityMob implements TamedMob, TameHostile { //maybe change extension with ASM?

	private static Field target;

	static {
		try {
			target = EntityWisp.class.getDeclaredField("targetedEntity");
			target.setAccessible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TameWispDummy(World world) {
		super(world);
		this.setSize(1, 1);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getCritterMaxHealth());
		this.setHealth(this.getCritterMaxHealth());
		experienceValue = 0;
		height = 1.25F;
		this.func_110163_bv();

		//this.setType(this.getAspect().getTag());
	}

	@ModDependent(ModList.THAUMCRAFT)
	private Aspect getAspect() {
		/*
		switch(rand.nextInt(8)) {
			case 0:
				return Aspect.PLANT;
			case 1:
				return Aspect.WATER;
			case 2:
				return Aspect.FIRE;
			case 3:
				return Aspect.MAGIC;
			case 4:
				return Aspect.ORDER;
			case 5:
				return Aspect.TOOL;
			case 6:
				return Aspect.HUNGER;
			default:
				return Aspect.LIGHT;
		}
		 */
		return ReikaJavaLibrary.getRandomCollectionEntry(rand, Aspect.aspects.values());
	}

	@Override
	public final String getCommandSenderName() {
		return this.hasCustomNameTag() ? this.getCustomNameTag() : this.getDefaultName();
	}

	private final String getDefaultName() {
		String owner = this.getMobOwner();
		String sg = this.getBaseCritter().name;
		if (owner == null || owner.isEmpty())
			return sg;
		else
			return owner+"'s "+sg;
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
		else if (dsc.isMagicDamage()) {
			return false;
		}
		else if (super.attackEntityFrom(dsc, par2)) {
			return true;
		}
		else
			return false;
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

		EntityPlayer ep = worldObj.getPlayerEntityByName(this.getMobOwner());
		if (ep != null && ep.getDistanceSqToEntity(this) <= 16384) {
			double vx = Math.signum(ep.posX-posX)/64D;
			double vy = Math.signum(ep.posY-posY)/64D;
			double vz = Math.signum(ep.posZ-posZ)/64D;
			if (vx < 0.05)
				motionX = vx;
			else
				motionX += vx;
			if (vx < 0.05)
				motionY = vy;
			else
				motionY += vy;
			if (vx < 0.05)
				motionZ = vz;
			else
				motionZ += vz;
			motionX = Math.signum(motionX)*Math.min(Math.abs(motionX), 0.25);
			motionY = Math.signum(motionY)*Math.min(Math.abs(motionY), 0.25);
			motionZ = Math.signum(motionZ)*Math.min(Math.abs(motionZ), 0.25);
			velocityChanged = true;
		}
	}

	private Entity getTarget() {
		try {
			return (Entity)target.get(this);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/*
	@Override
	private void setTarget(Entity entity) {
		try {
			target.set(this, entity);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	@Override
	protected void updateEntityActionState()
	{
		Entity e = this.getTarget();
		if (e != null && e.getCommandSenderName().equals(this.getMobOwner()))
			this.setTarget(null);
		super.updateEntityActionState();
	}

	@Override
	public final boolean getAlwaysRenderNameTagForRender()
	{
		return true;
	}

	@Override
	public final int getTalkInterval()
	{
		return 960;
	}

	@Override
	protected float getSoundVolume()
	{
		return 0.0625F;
	}

	@Override
	public final boolean canBePushed() {
		return false;
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
				e.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttackStrength());
				this.applyAttackEffects((EntityLivingBase)e);
			}
		}
	}

	private float getAttackStrength() {
		return 4;
	}

	private void applyAttackEffects(EntityLivingBase e) {

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
		return CritterType.WISP;
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
		return this.getBaseCritter().maxHealth*4;
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

	@Override
	protected final boolean interact(EntityPlayer ep)
	{
		ItemStack is = ep.getCurrentEquippedItem();
		String owner = this.getMobOwner();
		if (owner == null || owner.isEmpty()) {
			if (is != null && is.getItem() == CritterType.WISP.tamingItem) {
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
			return is == null || !(is.getItem() instanceof EntityCapturingItem);
		}
		else {
			ReikaChatHelper.writeString("You do not own this critter.");
			return false;
		}
	}

}
