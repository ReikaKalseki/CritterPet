package Reika.CritterPet.Biome;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.CritterPet.CritterPet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRedBamboo extends Block implements IPlantable {

	private IIcon stemIcon;
	private IIcon topIcon;

	private static final IIcon[] leaves = new IIcon[8];

	public BlockRedBamboo() {
		super(Material.plants);
		float s = 0.125F;
		this.setBlockBounds(0.5F-s, 0, 0.5F-s, 0.5F+s, 1, 0.5F+s);
		this.setTickRandomly(true);
	}

	public static IIcon getRandomLeaf(Random rand) {
		return leaves[rand.nextInt(leaves.length)];
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		return s <= 1 ? topIcon : stemIcon;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		stemIcon = ico.registerIcon("critterpet:bamboo/side");
		topIcon = ico.registerIcon("critterpet:bamboo/top");

		for (int i = 0; i < leaves.length; i++) {
			leaves[i] = ico.registerIcon("critterpet:bamboo/leaf_"+i);
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		/*
		if (world.getBlock(x, y-1, z) == Blocks.reeds || this.checkStability(world, x, y, z)) {
			if (world.isAirBlock(x, y+1, z)) {
				int l;

				for (l = 1; world.getBlock(x, y-l, z) == this; ++l) {
					;
				}

				if (l < 3) {
					int i1 = world.getBlockMetadata(x, y, z);

					if (i1 == 15) {
						world.setBlock(x, y+1, z, this);
						world.setBlockMetadataWithNotify(x, y, z, 0, 4);
					}
					else {
						world.setBlockMetadataWithNotify(x, y, z, i1+1, 4);
					}
				}
			}
		}*/
		this.checkStability(world, x, y, z);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y-1, z);
		return b == this || b.canSustainPlant(world, x, y-1, z, ForgeDirection.UP, this);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		this.checkStability(world, x, y, z);
	}

	protected final boolean checkStability(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return this.canPlaceBlockAt(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return CritterPet.proxy.bambooRender;//0;//1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return 0xffffff;//world.getBiomeGenForCoords(x, z).getBiomeGrassColor(x, y, z);
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return EnumPlantType.Plains;
	}

	@Override
	public Block getPlant(IBlockAccess world, int x, int y, int z) {
		return this;
	}

	@Override
	public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}
}