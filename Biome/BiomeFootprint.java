package Reika.CritterPet.Biome;

import java.util.HashSet;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import Reika.CritterPet.CritterPet;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class BiomeFootprint {

	private final HashSet<Coordinate> coords = new HashSet();

	private Vec3 center = Vec3.createVectorHelper(0, 0, 0);

	public BiomeFootprint() {

	}

	/* recursive version
	private void calculate(World world, int x, int z) {
		Coordinate loc = new Coordinate(x, 0, z);
		if (!coords.contains(loc) && CritterPet.isPinkForest(world, x, z)) {
			coords.add(loc);
			center.xCoord += x+0.5;
			center.zCoord += z+0.5;
			this.calculate(world, x-1, z);
			this.calculate(world, x+1, z);
			this.calculate(world, x, z-1);
			this.calculate(world, x, z+1);
		}
	}*/

	public boolean calculate(World world, int x, int z) {
		HashSet<Coordinate> next = new HashSet();
		Coordinate loc = new Coordinate(x, 0, z);
		next.add(loc);
		while (!next.isEmpty()) {
			HashSet<Coordinate> newNext = new HashSet();
			for (Coordinate c : next) {
				this.searchFrom(world, c, newNext);
			}
			next = newNext;
		}
		if (coords.isEmpty())
			return false;
		center.xCoord /= coords.size();
		center.zCoord /= coords.size();
		return true;
	}

	public Vec3 getCenter() {
		return Vec3.createVectorHelper(center.xCoord, center.yCoord, center.zCoord);
	}

	private void searchFrom(World world, Coordinate loc, HashSet<Coordinate> newNext) {
		int x = loc.xCoord;
		int z = loc.zCoord;
		if (!coords.contains(loc) && CritterPet.isPinkForest(world, x, z)) {
			coords.add(loc);
			center.xCoord += x+0.5;
			center.zCoord += z+0.5;
			newNext.add(new Coordinate(x-1, 0, z));
			newNext.add(new Coordinate(x+1, 0, z));
			newNext.add(new Coordinate(x, 0, z-1));
			newNext.add(new Coordinate(x, 0, z+1));
		}
	}

}
