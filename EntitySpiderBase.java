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
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

public abstract class EntitySpiderBase extends EntitySpider {

	private SpiderType base;
	private String owner;

	public EntitySpiderBase(World world, SpiderType sp) {
		super(world);
		base = sp;
		this.setSize(sp.size, sp.size/2);
	}

	public EntitySpiderBase setOwner(String ep) {
		owner = ep;
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
		return base.maxHealth;
	}

	@Override
	public final boolean shouldRiderFaceForward(EntityPlayer player) {
		return true;
	}

	protected abstract void updateRider();

	@Override
	public final void onUpdate() {
		super.onUpdate();
		this.updateRider();
		if (riddenByEntity != null)
			this.followOwner();
	}

	private void followOwner() {
		World world = worldObj;
		double x = posX;
		double y = posY;
		double z = posZ;
		Vec3 v = riddenByEntity.getLookVec();
		motionX = v.xCoord/2;
		motionZ = v.zCoord/2;
		rotationYaw = 0;
		rotationYawHead = 0;
		this.getNavigator().clearPathEntity();
		//PathEntity path = this.getNavigator().getPathToXYZ(x+v.xCoord*4, y+v.yCoord+4, z+v.zCoord*4);
		//this.getNavigator().setPath(path, 0.5);
	}

	@Override
	protected final Entity findPlayerToAttack()
	{
		return null;
	}

	@Override
	protected final void attackEntity(Entity e, float par2)
	{
		super.attackEntity(e, par2);
		this.applyAttackEffects(e);
	}

	protected abstract void applyAttackEffects(Entity e);

	@Override
	protected final boolean interact(EntityPlayer ep)
	{
		if (ep.getEntityName().equals(owner)) {
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

	@Override
	public String getEntityName() {
		return this.hasCustomNameTag() ? this.getCustomNameTag() : base.getName()+" Spider";
	}

	protected void moveByPlayerCommand() {
		//horse code?
	}
}
