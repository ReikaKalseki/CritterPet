package Reika.CritterPet.Biome;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import Reika.CritterPet.CritterPet;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;

public class WorldGenUraniumCave extends WorldGenerator {

	private static final int MAX_DIST = 512;
	private static final int MIN_DIST = 64;

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

		int top = DecoratorPinkForest.getTrueTopAt(world, x, z);
		BiomeFootprint bf = new BiomeFootprint();
		if (bf.calculate(world, x, z)) {
			usedLocations.add(loc);

			ReikaJavaLibrary.pConsole(loc);

			CentralCave cc = new CentralCave(x, ReikaRandomHelper.getRandomBetween(40, top-30, rand), z);
			cc.calculate(world, rand);

			double a1 = rand.nextDouble()*360;
			double a2 = rand.nextDouble()*360;
			while (Math.abs((a1-a2)%360) < 60) {
				a2 = rand.nextDouble()*360;
			}

			double c1 = Math.cos(Math.toRadians(a1));
			double s1 = Math.sin(Math.toRadians(a1));
			double c2 = Math.cos(Math.toRadians(a2));
			double s2 = Math.sin(Math.toRadians(a2));

			double d = 4;
			double dx = bf.center.xCoord;
			double dz = bf.center.zCoord;
			while (CritterPet.isPinkForest(world, MathHelper.floor_double(dx), MathHelper.floor_double(dz))) {
				dx += c1*d;
				dz += s1*d;
			}
			Tunnel t1 = new Tunnel(cc, dx, dz);

			dx = bf.center.xCoord;
			dz = bf.center.zCoord;
			while (CritterPet.isPinkForest(world, MathHelper.floor_double(dx), MathHelper.floor_double(dz))) {
				dx += c2*d;
				dz += s2*d;
			}
			Tunnel t2 = new Tunnel(cc, dx, dz);

			t1.calculate(world, rand);
			t2.calculate(world, rand);

			cc.generate(world);
			t1.generate(world);
			t2.generate(world);
		}

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

	private static class Tunnel {

		private final DecimalPosition center;
		private final double endX;
		private final double endZ;

		private final HashMap<Coordinate, Integer> carve = new HashMap();

		private Tunnel(CentralCave c, double dx, double dz) {
			center = new DecimalPosition(c.center);
			endX = dx;
			endZ = dz;
		}

		private void calculate(World world, Random rand) {
			int ex = MathHelper.floor_double(endX);
			int ez = MathHelper.floor_double(endZ);
			Coordinate end = new Coordinate(ex, DecoratorPinkForest.getTrueTopAt(world, ex, ez)+5, ez);
			double dd = ReikaMathLibrary.py3d(endX-center.xCoord, 0, endZ-center.zCoord);
			int n = (int)Math.max(4, dd/16);
			LightningBolt b = new LightningBolt(new DecimalPosition(center.xCoord, center.yCoord, center.zCoord), new DecimalPosition(end), n);
			b.setRandom(rand);
			b.variance = 10;//15;
			b.maximize();
			Spline path = new Spline(SplineType.CENTRIPETAL);

			for (int i = 0; i <= b.nsteps; i++) {
				path.addPoint(new BasicSplinePoint(b.getPosition(i)));
			}

			List<DecimalPosition> li = path.get(16, false);
			for (int i = 0; i < li.size(); i++) {
				DecimalPosition p = li.get(i);
				int px = MathHelper.floor_double(p.xCoord);
				int pz = MathHelper.floor_double(p.zCoord);
				this.carveAt(world, p);
			}
		}

		private void generate(World world) {
			for (Coordinate c : carve.keySet()) {
				c.setBlock(world, Blocks.air);
			}
		}

		private void carveAt(World world, DecimalPosition p) {
			double r = 2.5;
			for (double i = -r; i <= r; i++) {
				for (double j = -r; j <= r; j++) {
					for (double k = -r; k <= r; k++) {
						if (i*i+j*j+k*k <= (r+0.5)*(r+0.5)) {
							int dx = MathHelper.floor_double(p.xCoord+i);
							int dy = MathHelper.floor_double(p.yCoord+j);
							int dz = MathHelper.floor_double(p.zCoord+k);
							Coordinate c = new Coordinate(dx, dy, dz);
							Block b = c.getBlock(world);
							if (this.isTerrain(world, c, b)) {
								carve.put(c, MathHelper.floor_double(p.yCoord));
							}
						}
					}
				}
			}
		}

		private boolean isTerrain(World world, Coordinate c, Block b) {
			return b.isReplaceableOreGen(world, c.xCoord, c.yCoord, c.zCoord, Blocks.stone) || b.getMaterial() == Material.ground || b.getMaterial() == Material.clay || b.getMaterial() == Material.sand || b.isReplaceableOreGen(world, c.xCoord, c.yCoord, c.zCoord, Blocks.grass) || ReikaBlockHelper.isOre(b, c.getBlockMetadata(world));
		}

	}

	private static class CentralCave {

		private final Coordinate center;

		public CentralCave(int x, int y, int z) {
			center = new Coordinate(x, y, z);
		}

		private void calculate(World world, Random rand) {

		}

		private void generate(World world) {
			/*
			int r = 16;
			int ry = 5;
			for (int i = -r; i <= r; i++) {
				for (int j = -ry; j <= ry; j++) {
					for (int k = -r; k <= r; k++) {
						if (ReikaMathLibrary.isPointInsideEllipse(i, j, k, r, ry, r)) {
							int dx = center.xCoord+i;
							int dy = center.yCoord+j;
							int dz = center.zCoord+k;
							Coordinate c = new Coordinate(dx, dy, dz);
							c.setBlock(world, Blocks.air);
						}
					}
				}
			}
			 */
		}

	}

	private static class BiomeFootprint {

		private final HashSet<Coordinate> coords = new HashSet();

		private Vec3 center = Vec3.createVectorHelper(0, 0, 0);

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

		private boolean calculate(World world, int x, int z) {
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
}
