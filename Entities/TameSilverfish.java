package Reika.CritterPet.Entities;

import java.util.List;

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
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import Reika.CritterPet.CritterPet;
import Reika.CritterPet.Interfaces.TamedMob;
import Reika.CritterPet.Registry.CritterType;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class TameSilverfish extends EntitySilverfish implements TamedMob {

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
	public final void onUpdate() {
		boolean preventDespawn = false;
		if (!worldObj.isRemote && worldObj.difficultySetting == EnumDifficulty.PEACEFUL) { //the criteria for mob despawn in peaceful
			preventDespawn = true;
			worldObj.difficultySetting = EnumDifficulty.EASY;
		}
		super.onUpdate();
		if (preventDespawn)
			worldObj.difficultySetting = EnumDifficulty.PEACEFUL;
		if (entityToAttack != null && entityToAttack.getCommandSenderName().equals(this.getMobOwner()))
			entityToAttack = null;
	}

	@Override
	protected final Entity findPlayerToAttack()
	{
		return null;
	}

	@Override
	protected final boolean interact(EntityPlayer ep)
	{
		ItemStack is = ep.getCurrentEquippedItem();
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
			ReikaParticleHelper.HEART.spawnAt(worldObj, rx, y+0.8, rz, 0, 0, 0);
			//ReikaWorldHelper.splitAndSpawnXP(worldObj, rx, y+0.5, rz, 1+rand.nextInt(5));
		}
	}

	@Override
	public final boolean attackEntityFrom(DamageSource dsc, float par2)
	{
		return super.attackEntityFrom(dsc, par2);
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

	public final void bindTexture() {
		ReikaTextureHelper.bindTexture(CritterPet.class, CritterType.SILVERFISH.texture);
	}

	@Override
	public final boolean getAlwaysRenderNameTagForRender()
	{
		return true;
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

}
