/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet.Entities;

import Reika.CritterPet.Interfaces.TamedMob;
import Reika.CritterPet.Registry.CritterType;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.monster.EntityWisp;

public class TameWisp extends EntityWisp implements TamedMob {

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

	public TameWisp(World world) {
		super(world);
		this.setSize(1, 1);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getCritterMaxHealth());
		this.setHealth(this.getCritterMaxHealth());
		experienceValue = 0;
		height = 1.25F;
		this.func_110163_bv();

		this.setType(this.getAspect().getTag());
	}

	private Aspect getAspect() {
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
	public boolean attackEntityFrom(DamageSource dsc, float i)
	{
		if (dsc.isMagicDamage())
			return false;
		return super.attackEntityFrom(dsc, i);
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

	private void setTarget(Entity entity) {
		try {
			target.set(this, entity);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

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

}