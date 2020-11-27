package Reika.CritterPet.Biome;

import java.util.HashSet;
import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import Reika.CritterPet.CritterPet;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;

public class WorldGenUraniumCave extends WorldGenerator {

	private static final int MAX_DIST = 512;
	private static final int MIN_DIST = 128;

	private static final HashSet<Coordinate> usedLocations = new HashSet();

	private CubeDirections edgeDirection;
	private int edgeDistance;
	private Coordinate edgeLocation;

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		this.reset();

		x += rand.nextInt(16)+8;
		z += rand.nextInt(16)+8;

		Coordinate loc = new Coordinate(x, 0, z);

		for (Coordinate c : usedLocations) {
			if (c.getTaxicabDistanceTo(loc) < 256)
				return false;
		}

		this.getNearestBiomeEdge(world, x, z);
		if (edgeDirection == null)
			return false;
		if (edgeDistance < MIN_DIST || edgeDistance > MAX_DIST)
			return false;

		usedLocations.add(loc);

		int top = DecoratorPinkForest.getTrueTopAt(world, x, z);

		return true;
	}

	private void reset() {
		edgeLocation = null;
		edgeDirection = null;
		edgeDistance = -1;
	}

	private void getNearestBiomeEdge(World world, int x, int z) {
		for (int d = 0; d <= MAX_DIST; d++) {
			for (CubeDirections f : CubeDirections.list) {
				int dx = MathHelper.floor_double(x+f.offsetX*d);
				int dz = MathHelper.floor_double(z+f.offsetZ*d);
				if (!CritterPet.isPinkForest(world, dx, dz)) {
					edgeDirection = f;
					edgeDistance = d;
					edgeLocation = new Coordinate(dx, DecoratorPinkForest.getTrueTopAt(world, dx, dz), dz);
					return;
				}
			}
		}
	}

	public static void clearLakeCache() {
		usedLocations.clear();
	}
}
