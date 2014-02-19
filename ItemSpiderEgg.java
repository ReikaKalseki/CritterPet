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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.SpiderPet.Entities.EntitySpiderBase;
import Reika.SpiderPet.Registry.SpiderType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSpiderEgg extends ItemMonsterPlacer {

	private static EntitySpiderBase entity;

	public ItemSpiderEgg(int par1) {
		super(par1);
	}

	@Override
	public String getItemDisplayName(ItemStack is)
	{
		SpiderType type = this.getType(is);
		if (type != null) {
			String name = type.getName();
			return "Tame "+name+" Spider Spawn Egg";
		}
		else {
			return "Null-Type Spider Egg";
		}
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float par8, float par9, float par10)
	{
		boolean flag = this.superonItemUse(is, ep, world, x, y, z, side, par8, par9, par10);
		ReikaJavaLibrary.pConsole(entity, Side.SERVER);
		if (entity != null) {
			entity.setOwner(ep);
			entity = null;
		}
		return flag;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep)
	{
		ItemStack stack = this.superonItemRightClick(is, world, ep);
		if (entity != null) {
			entity.setOwner(ep);
			entity = null;
		}
		return stack;
	}

	private boolean superonItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		if (!world.isRemote) {
			int i1 = world.getBlockId(x, y, z);
			x += Facing.offsetsXForSide[side];
			y += Facing.offsetsYForSide[side];
			z += Facing.offsetsZForSide[side];
			double d0 = 0.0D;

			if (side == 1 && Block.blocksList[i1] != null && Block.blocksList[i1].getRenderType() == 11) //on top of fence
				d0 = 0.5D;

			Entity entity = spawnCreature(world, is.getItemDamage(), x + 0.5D, y + d0, z + 0.5D);

			if (entity != null) {
				if (entity instanceof EntityLivingBase && is.hasDisplayName())
					((EntityLiving)entity).setCustomNameTag(is.getDisplayName());

				if (!ep.capabilities.isCreativeMode)
					--is.stackSize;
			}

		}
		return true;
	}

	private ItemStack superonItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (!world.isRemote) {
			MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, ep, true);

			if (movingobjectposition == null)
				return is;
			else {
				if (movingobjectposition.typeOfHit == EnumMovingObjectType.TILE) {
					int i = movingobjectposition.blockX;
					int j = movingobjectposition.blockY;
					int k = movingobjectposition.blockZ;

					if (!world.canMineBlock(ep, i, j, k))
						return is;

					if (!ep.canPlayerEdit(i, j, k, movingobjectposition.sideHit, is))
						return is;

					if (world.getBlockMaterial(i, j, k) == Material.water) {
						Entity entity = spawnCreature(world, is.getItemDamage(), i, j, k);

						if (entity != null) {
							if (entity instanceof EntityLivingBase && is.hasDisplayName())
								((EntityLiving)entity).setCustomNameTag(is.getDisplayName());

							if (!ep.capabilities.isCreativeMode)
								--is.stackSize;
						}
					}
				}
			}
		}
		return is;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int pass)
	{
		SpiderType type = this.getType(is);
		if (type == null) {
			return 0xffffff;
		}
		else {
			return pass == 0 ? type.eggColor1 : type.eggColor2;
		}
	}

	public static Entity spawnCreature(World world, int ordinal, double x, double y, double z)
	{
		int id = SpiderType.spiderList[ordinal].getEntityID();

		Entity e = EntityList.createEntityByID(id, world);

		if (e != null) {
			EntityLiving entityliving = (EntityLiving)e;
			e.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
			entityliving.rotationYawHead = entityliving.rotationYaw;
			entityliving.renderYawOffset = entityliving.rotationYaw;
			entityliving.onSpawnWithEgg((EntityLivingData)null);
			world.spawnEntityInWorld(e);
			entityliving.playLivingSound();
		}

		entity = (EntitySpiderBase)e;
		return e;
	}

	public SpiderType getType(ItemStack is) {
		return SpiderType.spiderList[is.getItemDamage()];
	}

	@Override
	public Icon getIconFromDamageForRenderPass(int dmg, int pass)
	{
		return Item.monsterPlacer.getIconFromDamageForRenderPass(dmg, pass);
	}

	@Override
	public Icon getIconFromDamage(int dmg) {
		return Item.monsterPlacer.getIconFromDamage(dmg);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean verbose) {
		int i = is.getItemDamage();
		SpiderType type = SpiderType.spiderList[i];
		String name = type.getName();
		li.add("Spawns a tamed-by-"+ep.getEntityName()+" "+name+" Spider.");
	}

	@Override
	public void getSubItems(int id, CreativeTabs tab, List li) {
		int num = SpiderType.spiderList.length;
		for (int i = 0; i < num; i++) {
			li.add(new ItemStack(id, 1, i));
		}
	}

}
