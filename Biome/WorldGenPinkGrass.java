package Reika.CritterPet.Biome;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import Reika.CritterPet.CritterPet;
import Reika.CritterPet.Biome.BlockPinkGrass.GrassTypes;
import Reika.DragonAPI.Instantiable.Interpolation;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom.DynamicWeight;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class WorldGenPinkGrass extends WorldGenerator {

	private static final GrassType BASE = new GrassType(Blocks.tallgrass, 1, new Interpolation(false).addPoint(60, 50).addPoint(90, 25).addPoint(105, 0));

	private final WeightedRandom<GrassType> grassTypes = new WeightedRandom();

	WorldGenPinkGrass() {
		grassTypes.addDynamicEntry(BASE);
		grassTypes.addDynamicEntry(new GrassType(CritterPet.grass, GrassTypes.PEACH_FRINGE.ordinal(), new Interpolation(false).addPoint(60, 0).addPoint(80, 10).addPoint(100, 40).addPoint(128, 20)));
		grassTypes.addDynamicEntry(new GrassType(CritterPet.grass, GrassTypes.RED_STRANDS_1.ordinal(), new Interpolation(false).addPoint(55, 0).addPoint(70, 20).addPoint(96, 40).addPoint(144, 25)));
		grassTypes.addDynamicEntry(new GrassType(CritterPet.grass, GrassTypes.RED_STRANDS_2.ordinal(), new Interpolation(false).addPoint(55, 0).addPoint(60, 20).addPoint(90, 30).addPoint(112, 10)));
		grassTypes.addDynamicEntry(new GrassType(CritterPet.grass, GrassTypes.TINY_PINK_LUMPS.ordinal(), new Interpolation(false).addPoint(72, 0).addPoint(80, 10).addPoint(144, 50)));
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		do {
			Block at = world.getBlock(x, y, z);
			if (!(at.isLeaves(world, x, y, z) || at.isAir(world, x, y, z))) {
				break;
			}
			y--;
		} while (y > 0);

		for (int i = 0; i < 128; i++) {
			int dx = x + rand.nextInt(8) - rand.nextInt(8);
			int dy = y + rand.nextInt(4) - rand.nextInt(4);
			int dz = z + rand.nextInt(8) - rand.nextInt(8);

			BlockKey place = this.getBlockToPlace(world, dx, dy, dz, rand);

			if (world.isAirBlock(dx, dy, dz) && place.blockID.canBlockStay(world, dx, dy, dz)) {
				if (CritterPet.pinkforest.isRoad(world, dx, dz) && rand.nextBoolean())
					continue;
				world.setBlock(dx, dy, dz, place.blockID, place.metadata, 2);
			}
		}

		return true;
	}

	private BlockKey getBlockToPlace(World world, int dx, int dy, int dz, Random rand) {
		if (dy < 62 || !CritterPet.isPinkForest(world, dx, dz))
			return BASE.block;
		grassTypes.setRNG(rand);
		for (GrassType gr : grassTypes.getValues()) {
			gr.calcWeight(world, dx, dy, dz);
		}
		GrassType type =  grassTypes.getRandomEntry();
		if (type == null) {
			CritterPet.logger.logError("Null grass type calculated @ "+new Coordinate(dx, dy, dz)+"="+grassTypes.getTotalWeight());
			type = BASE;
		}
		return type.block;
	}

	private static class GrassType implements DynamicWeight {

		private final BlockKey block;
		private final Interpolation weightCurve;

		private double weight = 1;

		private GrassType(Block b, int meta, Interpolation curve) {
			block = new BlockKey(b, meta);
			weightCurve = curve;
			weight = weightCurve.getInitialValue();
		}

		public void calcWeight(World world, int x, int y, int z) {
			weight = weightCurve.getValue(y);
		}

		@Override
		public double getWeight() {
			return weight;
		}

	}
}