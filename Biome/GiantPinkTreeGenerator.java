package Reika.CritterPet.Biome;

import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import Reika.CritterPet.CritterPet;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

public class GiantPinkTreeGenerator extends WorldGenAbstractTree {

	public GiantPinkTreeGenerator() {
		super(false);
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		if (true)
			return false;
		int h1 = ReikaRandomHelper.getRandomBetween(20, 30, rand);
		int h2 = ReikaRandomHelper.getRandomBetween(15, 30, rand);
		int h0 = ReikaRandomHelper.getRandomBetween(2, 5, rand);
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
		int n = ReikaRandomHelper.getRandomBetween(4, 8, rand);
		double angsplit = 360D/n;
		for (int i = 0; i < n; i++) {
			double dx = x+1;
			double dz = z+1;
			double dy = y+h0+0.5;
			double phi = ReikaRandomHelper.getRandomPlusMinus(angsplit*i, 15, rand);//rand.nextDouble()*360;
			double theta = ReikaRandomHelper.getRandomBetween(-15, 5, rand);
			double dt = ReikaRandomHelper.getRandomBetween(5, 20, rand);
			double dp = ReikaRandomHelper.getRandomPlusMinus(0, 12, rand);
			double dpa = ReikaRandomHelper.getRandomPlusMinus(0, 4, rand);
			while (dy >= y-0.5) {
				world.setBlock(MathHelper.floor_double(dx), MathHelper.floor_double(dy), MathHelper.floor_double(dz), CritterPet.log);
				double[] xyz = ReikaPhysicsHelper.polarToCartesian(0.5, theta, phi);
				dx += xyz[0];
				dy += xyz[1];
				dz += xyz[2];
				theta = Math.max(-90, theta-dt);
				phi += dp;
				dp += dpa;
			}
		}
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
		return true;
	}

}
