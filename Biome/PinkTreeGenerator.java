package Reika.CritterPet.Biome;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import Reika.CritterPet.CritterPet;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class PinkTreeGenerator extends WorldGenAbstractTree {

	public PinkTreeGenerator() {
		super(false);
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		if (CritterPet.pinkforest.isRoad(world, x, z) | true)
			return false;
		int h = ReikaRandomHelper.getRandomBetween(10, 16, rand);
		for (int i = 0; i <= h; i++) {
			if (!world.getBlock(x, y+i, z).isAir(world, x, y+i, z))
				return false;
		}
		int hl = Math.min(h-4, ReikaRandomHelper.getRandomBetween(6, 9, rand));
		for (int i = 0; i < h; i++) {
			world.setBlock(x, y+i, z, CritterPet.log);

			if (i >= hl) {
				world.setBlock(x+1, y+i, z, CritterPet.leaves);
				world.setBlock(x-1, y+i, z, CritterPet.leaves);
				world.setBlock(x, y+i, z+1, CritterPet.leaves);
				world.setBlock(x, y+i, z-1, CritterPet.leaves);
			}
		}
		return true;
	}

}
