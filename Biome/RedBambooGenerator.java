package Reika.CritterPet.Biome;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class RedBambooGenerator extends WorldGenAbstractTree {

	public RedBambooGenerator() {
		super(false);
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		return false;
	}

}
