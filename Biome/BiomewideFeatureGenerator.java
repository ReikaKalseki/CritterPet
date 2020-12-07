package Reika.CritterPet.Biome;

import java.util.Collection;
import java.util.Random;

import net.minecraft.world.World;

import Reika.CritterPet.Biome.UraniumCave.CentralCave;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class BiomewideFeatureGenerator {

	public static final BiomewideFeatureGenerator instance = new BiomewideFeatureGenerator();

	private BiomewideFeatureGenerator() {

	}

	public void generateUniqueCenterFeatures(World world, int x, int z, Random rand, BiomeFootprint bf) {
		Collection<Coordinate> rivers = PinkRivers.instance.generateRivers(world, x, z, rand, bf);
		if (!rivers.isEmpty()) {
			CentralCave cc = UraniumCave.instance.generate(world, rand, x, z, rivers);
			if (cc != null) {

			}
		}
	}
}
