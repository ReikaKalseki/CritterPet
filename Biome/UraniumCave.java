package Reika.CritterPet.Biome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class UraniumCave {

	public static final UraniumCave instance = new UraniumCave();

	private UraniumCave() {

	}

	public CentralCave generate(World world, Random rand, int x, int z, Collection<Coordinate> rivers) {
		int top = DecoratorPinkForest.getTrueTopAt(world, x, z);
		CentralCave cc = new CentralCave(x, ReikaRandomHelper.getRandomBetween(40, top-50, rand), z);
		cc.calculate(world, rand);

		Collection<Tunnel> tunnels = new ArrayList();
		for (Coordinate c : rivers) {
			if (tunnels.size() >= 5)
				continue;
			double dx = c.xCoord-cc.center.xCoord;
			double dz = c.zCoord-cc.center.zCoord;
			double dd = ReikaMathLibrary.py3d(dx, 0, dz);
			double dr = 9;
			Coordinate endpoint = c.offset((int)(dx/dd*dr), 0, (int)(dz/dd*dr));
			double rootAngle = (Math.toDegrees(Math.atan2(endpoint.yCoord-cc.center.yCoord, endpoint.xCoord-cc.center.xCoord))%360+360)%360;
			double nearestLow = -99999;
			double nearestHigh = 99999;
			boolean flag = false;
			for (Tunnel t : tunnels) {
				double da = (Math.abs(t.startingAngle-rootAngle)%360+360)%360;
				if (rootAngle-da > nearestLow)
					nearestLow = t.startingAngle;
				if (rootAngle+da < nearestHigh)
					nearestHigh = t.startingAngle;
				flag |= da < 30;
			}
			if (flag) {
				if (nearestHigh-nearestLow < 45)
					continue;
				rootAngle = (nearestHigh+nearestLow)/2D;
			}
			Tunnel add = new Tunnel(cc, endpoint, rootAngle);
			tunnels.add(add);
		}

		ReikaJavaLibrary.pConsole(cc.center+" > "+tunnels);

		HashSet<Coordinate> carveSet = new HashSet();
		carveSet.addAll(cc.carve.keySet());

		for (Tunnel t : tunnels) {
			t.calculate(world, rand);
			carveSet.addAll(t.carve.keySet());
		}

		cc.generate(world);

		for (Tunnel t : tunnels) {
			t.generate(world);
		}

		for (Coordinate c : carveSet) {
			for (Coordinate c2 : c.getAdjacentCoordinates()) {
				if (carveSet.contains(c2))
					continue;
				if (c2.yCoord <= 58 && (c2.softBlock(world) || (c2.yCoord-c.yCoord == 1 && c2.getBlock(world) == Blocks.gravel))) {
					c2.setBlock(world, Blocks.stone);
				}
			}
		}

		return cc;
	}

	private static class Tunnel extends UraniumCavePiece {

		private final Coordinate endpoint;
		private final CentralCave cave;
		private final double startingAngle;

		private Tunnel(CentralCave cc, Coordinate c, double ang) {
			super(cc.center);
			cave = cc;
			cave.tunnels.add(this);
			endpoint = c;
			startingAngle = ang;
		}

		private void calculate(World world, Random rand) {
			int y = DecoratorPinkForest.getTrueTopAt(world, endpoint.xCoord, endpoint.zCoord);
			while (world.getBlock(endpoint.xCoord, y+1, endpoint.zCoord) == Blocks.water) {
				y++;
			}
			Coordinate end = new Coordinate(endpoint.xCoord, y+5, endpoint.zCoord);
			double dd = ReikaMathLibrary.py3d(endpoint.xCoord-center.xCoord, 0, endpoint.zCoord-center.zCoord);
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
			int last = 0;
			for (int i = 0; i < li.size(); i++) {
				DecimalPosition p = li.get(i);
				int px = MathHelper.floor_double(p.xCoord);
				int pz = MathHelper.floor_double(p.zCoord);
				double w = 0;//2.5;
				double r = 2.25;
				if (this.carveAt(world, p, r, w, /*this.getAngleAt(li, i)*/0)) {
					last = i;
				}
			}
			DecimalPosition p = li.get(last);
			int r = 3;
			for (double i = -r; i <= r; i++) {
				for (double j = 1; j <= r; j++) {
					for (double k = -r; k <= r; k++) {
						if (ReikaMathLibrary.isPointInsideEllipse(i, j, k, r+0.5, r*0.75, r+0.5)) {
							int dx = MathHelper.floor_double(p.xCoord+i);
							int dy = MathHelper.floor_double(p.yCoord+j);
							int dz = MathHelper.floor_double(p.zCoord+k);
							Coordinate c = new Coordinate(dx, dy, dz);
							if (world.getBlock(dx, dy, dz) == Blocks.dirt) {
								Block above = world.getBlock(dx, dy+1, dz);
								if (above == Blocks.water || above == Blocks.flowing_water || above.isAir(world, dx, dy+1, dz) || DecoratorPinkForest.isTerrain(world, dx, dy+1, dz)) {
									world.setBlock(dx, dy, dz, Blocks.water);
								}
							}
						}
					}
				}
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
				/*
				for (Coordinate c2 : c.getAdjacentCoordinates()) {
					if (this.skipCarve(c2))
						continue;
					if (c2.yCoord <= 58 && c2.softBlock(world) && !carve.containsKey(c2)) {
						c2.setBlock(world, Blocks.stone);
					}
				}
				 */
			}
		}

		@Override
		protected boolean skipCarve(Coordinate c) {
			return cave.footprint.contains(c.to2D());
		}

		@Override
		public String toString() {
			return "Tunnel "+cave.center+" > "+endpoint;
		}

	}

	static class CentralCave extends UraniumCavePiece {

		private static final int MIN_TUNNEL_WIDTH = 3;

		//private final int rmax = 40; //was 24 then 36
		//private final LobulatedCurve outer = LobulatedCurve.fromMinMaxRadii(18, rmax, 5, true); //was 16

		private final HashSet<Coordinate> footprint = new HashSet();

		private final ArrayList<Tunnel> tunnels = new ArrayList();

		private SimplexNoiseGenerator floorHeightNoise;
		private SimplexNoiseGenerator ceilingHeightNoise;

		private DecimalPosition innerCircleOffset;
		private DecimalPosition innerCircleCenter;
		private double innerCircleRadius;
		private double outerCircleRadius;

		public CentralCave(int x, int y, int z) {
			super(new DecimalPosition(x+0.5, y+0.5, z+0.5));
		}

		private void calculate(World world, Random rand) {

			floorHeightNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(rand.nextLong()).setFrequency(1/16D).addOctave(2.6, 0.17);
			ceilingHeightNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(rand.nextLong()).setFrequency(1/8D).addOctave(1.34, 0.41);

			int dr = 10;
			//outerCircleOffset = new DecimalPosition(ReikaRandomHelper.getRandomPlusMinus(0, dr, rand), 0, ReikaRandomHelper.getRandomPlusMinus(0, dr, rand));
			/*do {
				outerCircleRadius = ReikaRandomHelper.getRandomBetween(24D, 36D, rand);
				innerCircleRadius = ReikaRandomHelper.getRandomBetween(6D, 15D, rand);
			} while(outerCircleRadius-innerCircleRadius >= 9);
			 */
			outerCircleRadius = ReikaRandomHelper.getRandomBetween(24D, 36D, rand);
			innerCircleRadius = ReikaRandomHelper.getRandomBetween(Math.max(6D, outerCircleRadius-MIN_TUNNEL_WIDTH*6), outerCircleRadius-MIN_TUNNEL_WIDTH*4, rand);
			double maxr = outerCircleRadius-innerCircleRadius-MIN_TUNNEL_WIDTH;
			double offr = ReikaRandomHelper.getRandomBetween(maxr*0.75, maxr, rand);
			double offa = rand.nextDouble()*360;
			double offX = offr*Math.cos(Math.toRadians(offa));
			double offZ = offr*Math.sin(Math.toRadians(offa));
			//innerCircleCenter = center.offset(ReikaRandomHelper.getRandomPlusMinus(0, maxr, rand), 0, ReikaRandomHelper.getRandomPlusMinus(0, maxr, rand));
			innerCircleOffset = new DecimalPosition(offX, 0, offZ);

			innerCircleCenter = center.offset(innerCircleOffset);
			//outerCircleCenter = center.offset(outerCircleOffset);

			double r = outerCircleRadius;//+outerCircleOffset.xCoord+outerCircleOffset.zCoord;

			for (double i = -r; i <= r; i++) {
				for (double k = -r; k <= r; k++) {
					if (ReikaMathLibrary.py3d(i, 0, k) <= outerCircleRadius) {
						double di = i-innerCircleOffset.xCoord;
						double dk = k-innerCircleOffset.zCoord;
						int x = MathHelper.floor_double(center.xCoord+i);
						int z = MathHelper.floor_double(center.zCoord+k);
						Coordinate c = new Coordinate(x, 0, z);
						footprint.add(c);
						if (ReikaMathLibrary.py3d(di, 0, dk) >= innerCircleRadius) {
							int min = (int)(-6+MathHelper.clamp_double(floorHeightNoise.getValue(x, z)*3.2, -2, 2));
							int max = (int)(6+MathHelper.clamp_double(ceilingHeightNoise.getValue(x, z)*2, -2, 2));
							for (int h = min; h <= max; h++) {
								double d1 = 1;
								double d2 = 1; not working
								int diff = Math.min(h-min, max-h);
								if (diff <= 2) {
									d1 *= diff/3D;
									d2 += (3-diff)*2;
								}
								double r1 = outerCircleRadius*d1;
								double r2 = innerCircleRadius*d2;
								add noise to edges
								if (ReikaMathLibrary.py3d(i, 0, k) <= r1 && ReikaMathLibrary.py3d(di, 0, dk) >= r2) {
									carve.put(c.setY((int)(center.yCoord+h)), (int)center.yCoord);
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
							int dx = MathHelper.floor_double(center.xCoord+i);
							int dy = MathHelper.floor_double(center.yCoord+j);
							int dz = MathHelper.floor_double(center.zCoord+k);
							Coordinate c = new Coordinate(dx, dy, dz);
							c.setBlock(world, Blocks.air);
						}
					}
				}
			}
			 */
			int y = -1;
			for (Coordinate c : carve.keySet()) {
				c.setBlock(world, Blocks.air);
				y = Math.max(y, c.yCoord);

				/*
				for (Coordinate c2 : c.getAdjacentCoordinates()) {
					if (this.skipCarve(c2))
						continue;
					if (c2.softBlock(world) && !carve.containsKey(c2)) {
						c2.setBlock(world, Blocks.stone);
					}
				}
				 */
			}

			/*
			for (Coordinate c : footprint) {
				for (int i = 0; i < 4; i++)
					c.offset(0, y+i, 0).setBlock(world, Blocks.air);
			}
			 */
		}

		@Override
		protected boolean skipCarve(Coordinate c) {
			return false;
		}

	}

	private static abstract class UraniumCavePiece {

		protected final DecimalPosition center;

		protected final HashMap<Coordinate, Integer> carve = new HashMap();

		protected UraniumCavePiece(DecimalPosition p) {
			center = p;
		}

		protected final boolean carveAt(World world, DecimalPosition p, double r, double w, double angle) {
			return this.carveAt(world, p, r, r, w, angle);
		}

		protected final boolean carveAt(World world, DecimalPosition p, double r, double h, double w, double angle) {
			angle += 90;
			boolean flag = false;
			double ax = w > 0 ? Math.abs(Math.cos(Math.toRadians(angle))) : 0;
			double az = w > 0 ? Math.abs(Math.sin(Math.toRadians(angle))) : 0;
			for (double i = -r; i <= r; i++) {
				for (double j = -r; j <= r; j++) {
					for (double k = -r; k <= r; k++) {
						if (ReikaMathLibrary.isPointInsideEllipse(i, j, k, r+0.5+ax*w, h+0.5, r+0.5+az*w)) {
							int dx = MathHelper.floor_double(p.xCoord+i);
							int dy = MathHelper.floor_double(p.yCoord+j);
							int dz = MathHelper.floor_double(p.zCoord+k);
							Coordinate c = new Coordinate(dx, dy, dz);
							if (this.skipCarve(c))
								continue;
							if (c.isEmpty(world) || DecoratorPinkForest.isTerrain(world, c.xCoord, c.yCoord, c.zCoord)) {
								if (!carve.containsKey(c)) {
									flag = true;
								}
								carve.put(c, MathHelper.floor_double(p.yCoord));
							}
						}
					}
				}
			}
			return flag;
		}

		protected boolean skipCarve(Coordinate c) {
			return false;
		}
	}
}
