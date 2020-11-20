package Reika.CritterPet.Biome;

import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.CritterPet.CritterPet;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Worldgen.ModifiableBigTree;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class GiantPinkTreeGenerator extends ModifiableBigTree {

	public GiantPinkTreeGenerator() {
		super(false);
		trunkSize = 3;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		if (y < 108) //was 96
			return false;
		if (CritterPet.pinkforest.isRoad(world, x, z))
			return false;
		if (!ReikaPlantHelper.SAPLING.canPlantAt(world, x, y, z))
			return false;
		int h1 = ReikaRandomHelper.getRandomBetween(12, 24, rand); //was 20-30, then 18-25
		int h2 = ReikaRandomHelper.getRandomBetween(36, 64, rand); //was 15-30, then 40-72
		int h0 = ReikaRandomHelper.getRandomBetween(3, 6, rand); //was 2-5, then 3-6
		/*
		int y1 = h0+h1;
		int y2 = y1+h2;
		for (int i = 0; i <= y2; i++) {
			if (!world.getBlock(x-1, y+i, z-1).isAir(world, x-1, y+i, z-1))
				return false;
			if (!world.getBlock(x-1, y+i, z+2).isAir(world, x-1, y+i, z+2))
				return false;
			if (!world.getBlock(x+2, y+i, z-1).isAir(world, x+2, y+i, z-1))
				return false;
			if (!world.getBlock(x+2, y+i, z+2).isAir(world, x+2, y+i, z+2))
				return false;
		}
		/*
		for (int i = 0; i < h0; i++) {
			world.setBlock(x+2, y+i, z-1, CritterPet.log);
			world.setBlock(x-1, y+i, z-1, CritterPet.log);
			world.setBlock(x+2, y+i, z+2, CritterPet.log);
			world.setBlock(x-1, y+i, z+2, CritterPet.log);
		}
		 */
		int n = ReikaRandomHelper.getRandomBetween(5, 8, rand); //was 4-8
		double angsplit = 360D/n;
		for (int i = 0; i < n; i++) {
			double dx = x+0.5;
			double dz = z+0.5;
			double dy = y+h0+0.5;
			double phi = ReikaRandomHelper.getRandomPlusMinus(angsplit*i, 15, rand);//rand.nextDouble()*360;
			double theta = ReikaRandomHelper.getRandomBetween(-15, 5, rand);
			double[] xyz = ReikaPhysicsHelper.polarToCartesian(1.5, theta, phi);
			dx += xyz[0];
			dz += xyz[2];
			double dt = ReikaRandomHelper.getRandomBetween(5, 20, rand);
			double dp = ReikaRandomHelper.getRandomPlusMinus(0, 12, rand);
			double dpa = ReikaRandomHelper.getRandomPlusMinus(0, 4, rand);
			int ix = MathHelper.floor_double(dx);
			int iy = MathHelper.floor_double(dy);
			int iz = MathHelper.floor_double(dz);
			while (dy >= y-0.5 || ReikaWorldHelper.softBlocks(world, ix, iy-1, iz)) {
				ix = MathHelper.floor_double(dx);
				iy = MathHelper.floor_double(dy);
				iz = MathHelper.floor_double(dz);
				world.setBlock(ix, iy, iz, CritterPet.log);
				xyz = ReikaPhysicsHelper.polarToCartesian(0.5, theta, phi);
				dx += xyz[0];
				dy += xyz[1];
				dz += xyz[2];
				theta = Math.max(-90, theta-dt);
				phi += dp;
				dp += dpa;
			}
		}/*
		for (int i = h0; i < y1; i++) {
			world.setBlock(x, y+i, z, CritterPet.log);
			world.setBlock(x+1, y+i, z, CritterPet.log);
			world.setBlock(x, y+i, z+1, CritterPet.log);
			world.setBlock(x+1, y+i, z+1, CritterPet.log);
		}
		for (int i = y1; i < y2; i++) {
			world.setBlock(x, y+i, z, CritterPet.log);
			world.setBlock(x+1, y+i, z, CritterPet.log);
			world.setBlock(x, y+i, z+1, CritterPet.log);
			world.setBlock(x+1, y+i, z+1, CritterPet.log);

			world.setBlock(x+2, y+i, z, CritterPet.leaves);
			world.setBlock(x-1, y+i, z, CritterPet.leaves);
			world.setBlock(x, y+i, z+2, CritterPet.leaves);
			world.setBlock(x, y+i, z-1, CritterPet.leaves);
		}
		return true;*/
		leafDistanceLimit = rand.nextBoolean() ? 4 : 3;
		heightLimitLimit = h1+h2;
		branchSlope = ReikaRandomHelper.getRandomPlusMinus(0, BASE_SLOPE*2.5, rand);
		heightAttenuation = BASE_ATTENUATION*1.1;
		//minBranchHeight = hl*0+12;
		minHeight = h1+h2;
		globalOffset[1] = Math.max(h1+h0-4, 0);
		leafDensity = 0.625F; //was 0.75
		branchDensity = 0.4F; //was 0.67
		if (super.generate(world, rand, x, y, z)) {
			for (int dy = h0; dy < globalOffset[1]; dy++) {
				for (int i = -1; i <= 1; i++) {
					for (int k = -1; k <= 1; k++) {
						if (i == 0 || k == 0)
							world.setBlock(x+i, y+dy, z+k, CritterPet.log, 0, 2);
					}
				}
			}
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	protected BlockKey getLogBlock(int x, int y, int z) {
		return new BlockKey(CritterPet.log, 0);
	}

	@Override
	protected BlockKey getLeafBlock(int x, int y, int z) {
		return new BlockKey(CritterPet.leaves, BlockPinkLeaves.LeafTypes.GIANTTREE.ordinal());
	}

	@Override
	protected float layerSize(int layer) {
		return super.layerSize(layer)*0.5F; //was 1.3
	}

	@Override
	protected float leafSize(int r) {
		return super.leafSize(r);
	}

}
