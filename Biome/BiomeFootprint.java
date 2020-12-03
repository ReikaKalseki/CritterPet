package Reika.CritterPet.Biome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import Reika.CritterPet.CritterPet;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class BiomeFootprint {

	private final HashSet<Coordinate> coords = new HashSet();
	private final HashSet<Coordinate> edgeCoords = new HashSet();

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
				if (!this.searchFrom(world, c, newNext)) { //c was not in the biome, its neighbors are edges
					for (Coordinate c2 : c.getAdjacentCoordinates())
						edgeCoords.add(c2.to2D());
				}
			}
			next = newNext;
		}
		edgeCoords.retainAll(coords);
		if (coords.isEmpty())
			return false;
		center.xCoord /= coords.size();
		center.zCoord /= coords.size();
		return true;
	}

	public Vec3 getCenter() {
		return Vec3.createVectorHelper(center.xCoord, center.yCoord, center.zCoord);
	}

	private boolean searchFrom(World world, Coordinate loc, HashSet<Coordinate> newNext) {
		int x = loc.xCoord;
		int z = loc.zCoord;
		boolean biome = CritterPet.isPinkForest(world, x, z);
		if (!coords.contains(loc) && biome) {
			coords.add(loc);
			center.xCoord += x+0.5;
			center.zCoord += z+0.5;
			newNext.add(new Coordinate(x-1, 0, z));
			newNext.add(new Coordinate(x+1, 0, z));
			newNext.add(new Coordinate(x, 0, z-1));
			newNext.add(new Coordinate(x, 0, z+1));
		}
		return biome;
	}

	public Set<Coordinate> getCoords() {
		return Collections.unmodifiableSet(coords);
	}

	public Set<Coordinate> getEdges	() {
		return Collections.unmodifiableSet(edgeCoords);
	}

	public double getAngleAt(Coordinate c, int searchRadius) { //linear regression https://i.imgur.com/La8ge8z.png
		ArrayList<Coordinate> li = new ArrayList();

		double sumX = c.xCoord;
		double sumY = c.zCoord;
		double sumX2 = c.xCoord*c.xCoord;
		double sumXY = c.xCoord*c.zCoord;

		for (int i = -searchRadius; i <= searchRadius; i++) {
			for (int k = -searchRadius; k <= searchRadius; k++) {
				Coordinate c2 = c.offset(i, 0, k);
				if (!c2.equals(c) && edgeCoords.contains(c2)) {
					li.add(c2);
					sumX += c2.xCoord;
					sumY += c2.zCoord;
					sumX2 += c2.xCoord*c2.xCoord;
					sumXY += c2.xCoord*c2.zCoord;
				}
			}
		}
		if (li.isEmpty()) { //cannot determine angle for isolated cell
			return 0;//Double.NaN;
		}

		li.add(c);

		double num = li.size()*sumXY-sumX*sumY;
		double denom = li.size()*sumX2-sumX*sumX;
		double slope = num/denom;

		/*
		ArrayList<Point> li2 = new ArrayList();
		for (Coordinate c2 : li) {
			li2.add(new Point(c2.xCoord-c.xCoord, c2.zCoord-c.zCoord));
		}
		ReikaJavaLibrary.pConsole(c+" > "+li2+" > "+slope+" > "+Math.toDegrees(Math.atan(slope)));
		 */

		return Math.toDegrees(Math.atan(slope));
	}

}
