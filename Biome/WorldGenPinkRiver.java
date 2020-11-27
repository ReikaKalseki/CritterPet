package Reika.CritterPet.Biome;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.CritterPet.CritterPet;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public class WorldGenPinkRiver extends WorldGenerator {

	private static final int MAX_DIST = 192;
	private static final int MIN_DIST = 32;

	private static final int MAX_LAKE_DEPTH = 6;//8;

	private static final HashSet<Coordinate> usedLocations = new HashSet();

	private SimplexNoiseGenerator lakeShapeNoise;

	private CubeDirections edgeDirection;
	private int edgeDistance;
	private Coordinate edgeLocation;

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		this.reset();

		x += rand.nextInt(16)+8;
		z += rand.nextInt(16)+8;

		Coordinate loc = new Coordinate(x, y, z);

		for (Coordinate c : usedLocations) {
			if (c.getTaxicabDistanceTo(loc) < 64)
				return false;
		}

		this.getNearestBiomeEdge(world, x, z);
		if (edgeDirection == null)
			return false;
		if (edgeDistance < MIN_DIST || edgeDistance > MAX_DIST)
			return false;

		usedLocations.add(loc);

		this.setupNoise(rand);

		Lake l = new Lake();
		l.calculate(world, x, y, z, rand);

		boolean flag = false;

		if (l.isValid()) {
			flag |= l.generate(world);
		}
		if (flag) {
			ReikaJavaLibrary.pConsole(loc);
			River r = new River(l, edgeLocation);
			r.calculate(world, x, y, z, rand);
			r.generate(world);
		}

		return flag;
	}

	private void setupNoise(Random rand) {
		lakeShapeNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(rand.nextLong()).setFrequency(1/3D).addOctave(3.1, 0.18);
		lakeShapeNoise.clampEdge = true;
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

	private class River {

		private final Lake lake;
		private final Coordinate endpoint;

		private final HashMap<Coordinate, Integer> carve = new HashMap();

		private Spline path;
		private int riverY;

		private River(Lake l, Coordinate c) {
			lake = l;
			endpoint = c;
		}

		public void calculate(World world, int x, int y, int z, Random rand) {
			int n = Math.max(4, edgeDistance/24);
			LightningBolt b = new LightningBolt(new DecimalPosition(lake.lakeCenter), new DecimalPosition(endpoint), n);

			b.setRandom(rand);

			b.variance = 6;
			b.velocity = b.variance;
			b.update();

			path = new Spline(SplineType.CENTRIPETAL);

			for (int i = 0; i <= b.nsteps; i++) {
				path.addPoint(new BasicSplinePoint(b.getPosition(i)));
			}

			List<DecimalPosition> li = path.get(16, false);
			riverY = MathHelper.floor_double(li.get(0).yCoord);//lake.lakeCenter.yCoord;
			int lastDrop = 0;
			for (int i = 0; i < li.size(); i++) {
				DecimalPosition p = li.get(i);
				int px = MathHelper.floor_double(p.xCoord);
				int pz = MathHelper.floor_double(p.zCoord);
				int top = DecoratorPinkForest.getTrueTopAt(world, px, pz);
				if (riverY > top-8 && i-lastDrop > 6 && rand.nextInt(Math.max(1, 12-(i-lastDrop))) == 0) {
					riverY--;
					lastDrop = i;
				}
				int limit = Math.min(top, riverY);
				riverY = Math.max(limit, (int)p.yCoord);
				this.carveAt(world, p, top, i/(double)li.size());
			}
		}

		private void carveAt(World world, DecimalPosition p, int surface, double f) {
			double r = 3.25;
			int maxY = MathHelper.floor_double(riverY+r);
			int maxvalley = 6;
			if (f > 0.75) {
				double distance = edgeDistance*(1-f);
				if (distance < 24) {
					maxvalley += (24D-distance)/4D;
				}
				r *= (1-f)*4;
			}
			if (maxY < surface && surface-riverY <= maxvalley) {
				maxY = surface;
			}
			for (double i = -r; i <= r; i++) {
				for (double dy = riverY-r; dy <= maxY; dy++) {
					for (double k = -r; k <= r; k++) {
						double j = riverY-dy;
						if (maxY == surface && j < 0)
							j = 0;
						if (i*i+j*j+k*k <= (r+0.5)*(r+0.5)) {
							int dx = MathHelper.floor_double(p.xCoord+i);
							//int dy = MathHelper.floor_double(p.yCoord*0+riverY+j);
							int dz = MathHelper.floor_double(p.zCoord+k);
							Coordinate c = new Coordinate(dx, dy, dz);
							Block b = c.getBlock(world);
							if (this.isTerrain(world, c, b)) {
								carve.put(c, riverY);
							}
						}
					}
				}
			}
		}

		private boolean isTerrain(World world, Coordinate c, Block b) {
			return b.isReplaceableOreGen(world, c.xCoord, c.yCoord, c.zCoord, Blocks.stone) || b.getMaterial() == Material.ground || b.getMaterial() == Material.clay || b.getMaterial() == Material.sand || b.isReplaceableOreGen(world, c.xCoord, c.yCoord, c.zCoord, Blocks.grass) || ReikaBlockHelper.isOre(b, c.getBlockMetadata(world));
		}

		private boolean generate(World world) {
			for (Entry<Coordinate, Integer> e : carve.entrySet()) {
				Coordinate c = e.getKey();
				boolean water = c.yCoord < e.getValue();
				c.setBlock(world, water ? Blocks.water : Blocks.air);
			}
			return true;
		}

	}

	private class Lake {

		private final HashMap<Coordinate, Integer> columns = new HashMap();
		private final HashSet<Coordinate> colCoords = new HashSet();

		private int lowestRim = 255;
		private int lowestFloor = 255;

		private Coordinate lakeCenter;

		public void calculate(World world, int x, int y, int z, Random rand) {
			int rx = ReikaRandomHelper.getRandomBetween(8, 20, rand);
			int rz = ReikaRandomHelper.getRandomBetween(8, 20, rand);

			int cx = 0;
			int cz = 0;

			for (int i = -rx; i <= rx; i++) {
				for (int k = -rz; k <= rz; k++) {
					int dx = x+i;
					int dz = z+k;
					double dd = i*i+k*k;
					double dr = rx*rx+rz*rz;
					if (dd <= dr) {
						double dn = ReikaMathLibrary.normalizeToBounds(lakeShapeNoise.getValue(x, z), -0.5, 1.5);
						dn = MathHelper.clamp_double(dn*1.5, 0, 1);
						//dn = 0.75+0.25*dn;
						double df = 1D-dd/dr;
						//df = df >= 0.5 ? 1 : Math.sqrt(2*df);
						//df = Math.pow(df, 0.75);
						df = Math.min(1, 1.25*df*df);
						int depth = (int)(MAX_LAKE_DEPTH*df*dn);
						if (depth > 0) {
							this.addColumn(world, dx, dz, depth);
							cx += dx;
							cz += dz;
						}
					}
				}
			}

			if (!columns.isEmpty()) {

				cx /= columns.size();
				cz /= columns.size();

				lakeCenter = new Coordinate(cx, DecoratorPinkForest.getTrueTopAt(world, cx, cz)*0+lowestRim+2-1, cz);
			}
		}

		private void addColumn(World world, int dx, int dz, int depth) {
			int yb = DecoratorPinkForest.getTrueTopAt(world, dx, dz);
			columns.put(new Coordinate(dx, yb, dz), depth);
			colCoords.add(new Coordinate(dx, 0, dz));

			int rim = this.getRimAt(world, dx, yb, dz);
			lowestRim = Math.min(lowestRim, rim);
			lowestFloor = Math.min(lowestFloor, yb-depth);
		}

		private boolean isValid() {
			if (lowestFloor >= lowestRim-1) {
				return false;
			}
			Iterator<Coordinate> it = columns.keySet().iterator();
			while (it.hasNext()) {
				Coordinate c = it.next();
				if (!this.isColumnValid(c))
					it.remove();
			}
			return columns.size() >= 40;//20;
		}

		private boolean isColumnValid(Coordinate at) {
			return true;//at.yCoord <= lowestRim;
		}

		private int getRimAt(World world, int x, int y, int z) {
			for (int d = -4; d <= y; d++) {
				int dy = y-d;
				for (int i = 0; i < 4; i++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i+2];
					int dx = x+dir.offsetX;
					int dz = z+dir.offsetZ;
					Block at = world.getBlock(dx, dy, dz);
					boolean soft = at != Blocks.water && ReikaWorldHelper.softBlocks(world, dx, dy, dz);//at != Blocks.sand && at != Blocks.stone && at != Blocks.dirt && at != Blocks.clay;
					if (!soft)
						return dy;
				}
			}
			return 255;
		}

		private boolean generate(World world) {
			boolean flag = false;
			for (Entry<Coordinate, Integer> e : columns.entrySet()) {
				boolean flag2 = false;
				Coordinate at = e.getKey();
				double df = this.hasNeighbors(at) ? 1 : 0.5;
				int depth = (int)(e.getValue()*df);
				int dh = Math.max(0, at.yCoord-lowestRim);
				for (int h = -dh; h <= depth; h++) {
					int dy = at.yCoord-h;
					if (dy < lowestRim) {
						world.setBlock(at.xCoord, dy, at.zCoord, Blocks.water);
						flag2 = true;
					}
					else {
						world.setBlock(at.xCoord, dy, at.zCoord, Blocks.air);
					}
				}
				if (flag2) {
					world.setBlock(at.xCoord, at.yCoord-depth-1, at.zCoord, lakeShapeNoise.getValue(at.xCoord, at.zCoord) > 0 ? Blocks.clay : Blocks.sand);

					world.setBlock(at.xCoord, at.yCoord-depth-2, at.zCoord, Blocks.dirt);
					flag = true;
				}
				else {
					world.setBlock(at.xCoord, at.yCoord-depth-1, at.zCoord, depth > 1 ? Blocks.sand : Blocks.grass);
					world.setBlock(at.xCoord, at.yCoord-depth-2, at.zCoord, Blocks.dirt);
				}
			}
			return flag;
		}

		private boolean hasNeighbors(Coordinate at) {
			at = at.to2D();
			for (int i = 0; i < 4; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i+2];
				if (!colCoords.contains(at.offset(dir, 1)))
					return false;
			}
			return true;
		}
	}

	public static void clearLakeCache() {
		usedLocations.clear();
	}

}
