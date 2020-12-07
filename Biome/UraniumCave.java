package Reika.CritterPet.Biome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
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
			double dr = 6;
			Tunnel add = new Tunnel(cc, c.offset((int)(dx/dd*dr), 0, (int)(dz/dd*dr)));
			for (Tunnel t : tunnels) {
				if (Math.abs(t.rootAngle-add.rootAngle) <= 30 || Math.abs(360-Math.abs(t.rootAngle-add.rootAngle)) <= 30) {
					add = null;
					break;
				}
			}
			if (add != null)
				tunnels.add(add);
		}

		ReikaJavaLibrary.pConsole(cc.center+" > "+tunnels);

		for (Tunnel t : tunnels) {
			t.calculate(world, rand);
		}

		cc.generate(world);

		for (Tunnel t : tunnels) {
			t.generate(world);
		}

		return cc;
	}

	private static class Tunnel extends UraniumCavePiece {

		private final Coordinate endpoint;
		private final CentralCave cave;
		private final double rootAngle;

		private Tunnel(CentralCave cc, Coordinate c) {
			super(cc.center);
			cave = cc;
			cave.tunnels.add(this);
			endpoint = c;
			rootAngle = (Math.toDegrees(Math.atan2(endpoint.yCoord-center.yCoord, endpoint.xCoord-center.xCoord))%360+360)%360;
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
			for (int i = 0; i < li.size(); i++) {
				DecimalPosition p = li.get(i);
				int px = MathHelper.floor_double(p.xCoord);
				int pz = MathHelper.floor_double(p.zCoord);
				double w = 0;//2.5;
				double r = 2.25;
				this.carveAt(world, p, r, w, /*this.getAngleAt(li, i)*/0);
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
					if (this.skipCarve(c2))
						continue;
					if (c2.yCoord <= 58 && c2.softBlock(world) && !carve.containsKey(c2)) {
						c2.setBlock(world, Blocks.stone);
					}
				}
			}
		}

		@Override
		protected boolean skipCarve(Coordinate c) {
			return cave.footprint.contains(c.to2D()) || cave.skipCarve(c);
		}

		@Override
		public String toString() {
			return "Tunnel "+cave.center+" > "+endpoint+" @ "+rootAngle;
		}

	}

	static class CentralCave extends UraniumCavePiece {

		//private final int rmax = 40; //was 24 then 36
		//private final LobulatedCurve outer = LobulatedCurve.fromMinMaxRadii(18, rmax, 5, true); //was 16

		private final HashSet<Coordinate> footprint = new HashSet();

		private final ArrayList<Tunnel> tunnels = new ArrayList();
		private final ArrayList<CaveRoom> rooms = new ArrayList();

		public CentralCave(int x, int y, int z) {
			super(new DecimalPosition(x+0.5, y+0.5, z+0.5));
		}

		private void calculate(World world, Random rand) {
			/*
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
			 *//*
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
			}*/

			Spline s = new Spline(SplineType.CENTRIPETAL);
			int n = 6;
			double da = 360D/n;
			for (double a = 0; a < 360; a += da) {
				double ra = Math.toRadians(ReikaRandomHelper.getRandomPlusMinus(a, da/3, rand));
				double r = ReikaRandomHelper.getRandomBetween(20, 32, rand);
				double dx = center.xCoord+r*Math.cos(ra);
				double dz = center.zCoord+r*Math.sin(ra);
				double dy = ReikaRandomHelper.getRandomPlusMinus(center.yCoord+0.5, 6, rand);
				double dd = 2;
				DecimalPosition p = new DecimalPosition(ReikaRandomHelper.getRandomPlusMinus(dx, dd), dy, ReikaRandomHelper.getRandomPlusMinus(dz, dd));
				s.addPoint(new BasicSplinePoint(p));
				dd = 5;//4;
				rooms.add(new CaveRoom(p.offset(ReikaRandomHelper.getRandomPlusMinus(0, dd), 0, ReikaRandomHelper.getRandomPlusMinus(0, dd))));
			}

			/*
			double dr0 = 1.2;
			double dr1 = 2.5;
			double dr = ReikaRandomHelper.getRandomBetween(dr0, dr1, rand);
			double vr = ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.15, rand);
			if (rand.nextBoolean())
				vr = -vr;
			 */

			List<DecimalPosition> li = s.get(24, true);
			for (int i = 0; i < li.size(); i++) {
				DecimalPosition p = li.get(i);
				double dr = ReikaRandomHelper.getRandomBetween(1.6, 2.4, rand);
				this.carveAt(world, p, dr, 6, 0, 0);
				/*
				dr = MathHelper.clamp_double(dr+vr, dr0, dr1);

				if (dr <= dr0 || dr >= dr1) {
					vr = ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.15, rand);
					if (dr >= dr1)
						vr = -vr;
				}*/
			}

			ReikaJavaLibrary.getRandomListEntry(rand, rooms).lootTier = 3;

			for (CaveRoom c : rooms) {
				c.calculate(world, rand);
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
			for (Coordinate c : carve.keySet()) {
				c.setBlock(world, Blocks.air);
				y = Math.max(y, c.yCoord);

				for (Coordinate c2 : c.getAdjacentCoordinates()) {
					if (this.skipCarve(c2))
						continue;
					if (c2.softBlock(world) && !carve.containsKey(c2)) {
						c2.setBlock(world, Blocks.stone);
					}
				}
			}

			/*
			for (Coordinate c : footprint) {
				for (int i = 0; i < 4; i++)
					c.offset(0, y+i, 0).setBlock(world, Blocks.air);
			}
			 */

			for (CaveRoom c : rooms) {
				c.generate(world);
			}
		}

		@Override
		protected boolean skipCarve(Coordinate c) {
			c = c.to2D();
			for (Tunnel cr : tunnels) {
				if (cr.carve.containsKey(c))
					return true;
			}
			for (CaveRoom cr : rooms) {
				if (cr.carve.containsKey(c))
					return true;
			}
			return false;
		}

	}

	private static class CaveRoom extends UraniumCavePiece {

		private int lootTier;

		public CaveRoom(DecimalPosition p) {
			super(p);
		}

		public void generate(World world) {
			for (Coordinate c : carve.keySet()) {
				c.setBlock(world, Blocks.air);
			}
			center.setBlock(world, Blocks.glowstone);
			center.offset(0, -1, 0).setBlock(world, Blocks.wool, lootTier);
		}

		public void calculate(World world, Random rand) {
			lootTier = Math.max(lootTier, rand.nextInt(3));

			int r = 6;
			int ry = 2;
			for (int i = -r; i <= r; i++) {
				for (int j = -ry; j <= ry; j++) {
					for (int k = -r; k <= r; k++) {
						if (ReikaMathLibrary.isPointInsideEllipse(i, j, k, r, ry, r)) {
							int dx = MathHelper.floor_double(center.xCoord+i);
							int dy = MathHelper.floor_double(center.yCoord+j);
							int dz = MathHelper.floor_double(center.zCoord+k);
							Coordinate c = new Coordinate(dx, dy, dz);
							carve.put(c, MathHelper.floor_double(center.yCoord));
						}
					}
				}
			}
		}

	}

	private static abstract class UraniumCavePiece {

		protected final DecimalPosition center;

		protected final HashMap<Coordinate, Integer> carve = new HashMap();

		protected UraniumCavePiece(DecimalPosition p) {
			center = p;
		}

		protected final void carveAt(World world, DecimalPosition p, double r, double w, double angle) {
			this.carveAt(world, p, r, r, w, angle);
		}

		protected final void carveAt(World world, DecimalPosition p, double r, double h, double w, double angle) {
			angle += 90;
			double ax = Math.abs(Math.cos(Math.toRadians(angle)));
			double az = Math.abs(Math.sin(Math.toRadians(angle)));
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
							if (DecoratorPinkForest.isTerrain(world, c.xCoord, c.yCoord, c.zCoord) ) {
								carve.put(c, MathHelper.floor_double(p.yCoord));
							}
						}
					}
				}
			}
		}

		protected boolean skipCarve(Coordinate c) {
			return false;
		}
	}
}
