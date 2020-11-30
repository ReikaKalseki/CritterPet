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
import Reika.DragonAPI.Instantiable.Math.LobulatedCurve;
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

			CentralCave cc = new CentralCave(x, ReikaRandomHelper.getRandomBetween(40, top-30, rand), z);
			cc.calculate(world, rand);

			ReikaJavaLibrary.pConsole(cc.center);

			double a1 = rand.nextDouble()*360;
			double a2 = rand.nextDouble()*360;
			while (Math.abs((a1-a2)%360) <= 90) { //was 60
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

		private final CentralCave cave;

		private final HashMap<Coordinate, Integer> carve = new HashMap();

		private Tunnel(CentralCave c, double dx, double dz) {
			cave = c;
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
			//b.variance = 10;//15;
			b.setVariance(10, 8, 10);
			b.maximize();
			Spline path = new Spline(SplineType.CENTRIPETAL);

			for (int i = 0; i <= b.nsteps; i++) {
				DecimalPosition pos = b.getPosition(i);
				if (i <= 1) {
					pos = new DecimalPosition(pos.xCoord, center.yCoord, pos.zCoord);
				}
				path.addPoint(new BasicSplinePoint(pos));
			}

			List<DecimalPosition> li = path.get(16, false);
			for (int i = 0; i < li.size(); i++) {
				DecimalPosition p = li.get(i);
				int px = MathHelper.floor_double(p.xCoord);
				int pz = MathHelper.floor_double(p.zCoord);
				this.carveAt(world, p, /*this.getAngleAt(li, i)*/0);
			}
		}

		private double getAngleAt(List<DecimalPosition> li, int i) {
			DecimalPosition pre = i == 0 ? null : li.get(i-1);
			DecimalPosition at = li.get(i);
			DecimalPosition post = i == li.size()-1 ? null : li.get(i+1);
			double ang1 = pre == null ? -1 : Math.toDegrees(Math.atan2(at.zCoord-pre.zCoord, at.xCoord-pre.xCoord));
			double ang2 = post == null ? -1 : Math.toDegrees(Math.atan2(post.zCoord-at.zCoord, post.xCoord-at.xCoord));
			if (pre == null)
				return ang2;
			else if (post == null)
				return ang1;
			return (ang1+ang2)/2D;
		}

		private void generate(World world) {
			for (Coordinate c : carve.keySet()) {
				c.setBlock(world, Blocks.air);
				for (Coordinate c2 : c.getAdjacentCoordinates()) {
					if (cave.footprint.contains(c2.to2D()))
						continue;
					if (c2.yCoord <= 58 && c2.softBlock(world) && !carve.containsKey(c2)) {
						c2.setBlock(world, Blocks.stone);
					}
				}
			}
		}

		private void carveAt(World world, DecimalPosition p, double angle) {
			angle += 90;
			double ax = 0;//Math.abs(Math.cos(Math.toRadians(angle)));
			double az = 0;//Math.abs(Math.sin(Math.toRadians(angle)));
			double w = 2.5;
			double r = 2.25;
			for (double i = -r; i <= r; i++) {
				for (double j = -r; j <= r; j++) {
					for (double k = -r; k <= r; k++) {
						if (ReikaMathLibrary.isPointInsideEllipse(i, j, k, r+0.5+ax*w, r+0.5, r+0.5+az*w)) {
							int dx = MathHelper.floor_double(p.xCoord+i);
							int dy = MathHelper.floor_double(p.yCoord+j);
							int dz = MathHelper.floor_double(p.zCoord+k);
							Coordinate c = new Coordinate(dx, dy, dz);
							if (cave.footprint.contains(c.to2D()))
								continue;
							Block b = c.getBlock(world);
							if (this.isTerrain(world, c, b) ) {
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

		private final int rmax = 40; //was 24 then 36
		private final LobulatedCurve outer = LobulatedCurve.fromMinMaxRadii(18, rmax, 5, true); //was 16

		private final HashSet<Coordinate> carve = new HashSet();
		private final HashSet<Coordinate> footprint = new HashSet();

		public CentralCave(int x, int y, int z) {
			center = new Coordinate(x, y, z);
		}

		private void calculate(World world, Random rand) {
			LobulatedCurve inner = LobulatedCurve.fromMinMaxRadii(9, 15, 3, true); //was 6,10,3
			outer.generate(rand);
			inner.generate(rand);
			int h0 = 3;
			int h = 8;
			for (int i = -rmax; i <= rmax; i++) {
				for (int k = -rmax; k <= rmax; k++) {
					if (outer.isPointInsideCurve(i, k)) {
						footprint.add(center.offset(i, 0, k).to2D());
						if (!inner.isPointInsideCurve(i, k)) {
							for (int j = -h0; j <= h; j++) {
								double ry = 1;
								if (j < 0) {
									ry *= 1+j*0.4/h0;
								}
								else if (h-j <= 4) {
									ry *= Math.pow((h-j)/5D, 0.4);
								}/*
							double dr = Math.sqrt(i*i+k*k);
							double ang = Math.toDegrees(Math.atan2(k, i));
							double ar = outer.getRadius(ang)-inner.getRadius(ang);
							double line = (outer.getRadius(ang)+inner.getRadius(ang))/2D;
							if (Math.abs(dr-line) <= ar*ry) {
								carve.add(center.offset(i, j, k));
							}
								 */
								double i1 = i*ry;
								double k1 = k*ry;
								double i2 = i/ry;
								double k2 = k/ry;
								if (outer.isPointInsideCurve(i1, k1) && !inner.isPointInsideCurve(i2, k2)) {
									carve.add(center.offset(i, j, k));
								}
							}
						}
					}
				}
			}
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
			int y = -1;
			for (Coordinate c : carve) {
				c.setBlock(world, Blocks.air);
				y = Math.max(y, c.yCoord);
			}

			for (Coordinate c : footprint) {
				for (int i = 0; i < 4; i++)
					c.offset(0, y+i, 0).setBlock(world, Blocks.air);
			}
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
