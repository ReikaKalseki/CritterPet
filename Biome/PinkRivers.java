package Reika.CritterPet.Biome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.CritterPet.CritterPet;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BreadthFirstSearch;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class PinkRivers {

	public static final PinkRivers instance = new PinkRivers();

	private PinkRivers() {

	}

	public Collection<Coordinate> generateRivers(World world, int x, int z, Random rand, BiomeFootprint bf) {
		ArrayList<Coordinate> li = new ArrayList(bf.getEdges());
		if (li.isEmpty())
			return new ArrayList();
		HashMap<Coordinate, RiverMouth> valid = new HashMap();
		int idx = rand.nextInt(li.size());
		Coordinate c = li.remove(idx);
		while (!li.isEmpty()) {
			/*
			for (Coordinate c2 : valid.keySet()) {
				if (c.getTaxicabDistanceTo(c2) <= 64) {
					c = null;
					break;
				}
			}*/
			if (c != null) {
				RiverMouth ang = this.isValidRiverEndpoint(world, x, z, c, bf);
				if (ang != null) {
					valid.put(c, ang);
				}
			}
			idx = rand.nextInt(li.size());
			c = li.remove(idx);
		}
		ArrayList<River> riversToGenerate = new ArrayList();
		ArrayList<Coordinate> riversGenerated = new ArrayList();
		for (Entry<Coordinate, RiverMouth> e : valid.entrySet()) {
			River r = this.prepareRiverAt(world, e.getKey(), e.getValue());
			if (r != null) {
				riversToGenerate.add(r);
			}
		}
		//Collections.sort(riversToGenerate,);
		for (River r : riversToGenerate) {
			for (Coordinate r2 : riversGenerated) {
				if (!r.rootPosition.equals(r2) && r.rootPosition.getTaxicabDistanceTo(r2) <= 48) {
					r = null;
					break;
				}
			}
			if (r != null) {
				r.generate(world);
				riversGenerated.add(r.rootPosition);
			}
		}
		return riversGenerated;
	}

	private River prepareRiverAt(World world, Coordinate c, RiverMouth mouth) {
		/*
		for (double i = -r; i <= r; i += 0.5) {
			for (double k = -2; k <= 18; k += 0.5) {
				int dx = MathHelper.floor_double(c.xCoord-k*river.cosNormal+i*river.cosTangent);
				int dz = MathHelper.floor_double(c.zCoord-k*river.sinNormal+i*river.sinTangent);
				if (CritterPet.isPinkForest(world, dx, dz)) {
					//world.setBlock(dx, 140, dz, Blocks.wool, Math.abs(i), 2);

					int top = this.getTrueTopAt(world, dx, dz);
					double depth = d*Math.min(1, 1.125*Math.sqrt(1-Math.abs(i)/r));
					double target = river.averageHeight-depth;
					double dh = top-target;
					if (dh > 0) {
						for (int h = 0; h <= dh; h++) {
							int dy = top-h;
							world.setBlock(dx, dy, dz, Blocks.glass);
						}
					}
					world.setBlock(dx, (int)target-1, dz, Blocks.clay);
				}
			}
		}*/
		River river = new River(c, mouth);
		river.calculate(world);
		if (river.sandBlocks.size() < 25)
			return null;
		return river;
	}

	private RiverMouth isValidRiverEndpoint(World world, int x, int z, Coordinate c, BiomeFootprint bf) {
		double raw = bf.getAngleAt(c, 4);
		double ang = Math.toRadians(raw);
		double angn = Math.toRadians(raw+90);
		double dx = Math.cos(ang);
		double dz = Math.sin(ang);
		double dxn = Math.cos(angn);
		double dzn = Math.sin(angn);
		double w = 6.5;
		int dd = 3;
		int x0 = MathHelper.floor_double(c.xCoord-dxn*dd);
		int z0 = MathHelper.floor_double(c.zCoord-dzn*dd);
		if (!CritterPet.isPinkForest(world, x0, z0))
			return null;
		double h = DecoratorPinkForest.getAverageHeight(world, x0, z0, 3);
		for (int dl = -1; dl <= 6; dl += 2) {
			int ddl = dl+dd;
			int x1 = MathHelper.floor_double(c.xCoord+w*dx-dxn*ddl);
			int z1 = MathHelper.floor_double(c.zCoord+w*dz-dzn*ddl);
			int x2 = MathHelper.floor_double(c.xCoord-w*dx-dxn*ddl);
			int z2 = MathHelper.floor_double(c.zCoord-w*dz-dzn*ddl);
			if (!CritterPet.isPinkForest(world, x1, z1))
				return null;
			if (!CritterPet.isPinkForest(world, x2, z2))
				return null;
			double h1 = DecoratorPinkForest.getAverageHeight(world, x1, z1, 2);
			double h2 = DecoratorPinkForest.getAverageHeight(world, x1, z1, 2);
			if (Math.abs(h2-h1) >= 3 || h2 < h-2 || h1 < h-2 || h < h1-10 || h < h2-10)
				return null;
		}
		boolean flag = false;
		for (double dl = 2; dl <= 24; dl += 0.5) {
			int lx = MathHelper.floor_double(c.xCoord-dxn*dl);
			int lz = MathHelper.floor_double(c.zCoord-dzn*dl);
			int top = DecoratorPinkForest.getTrueTopAt(world, lx, lz);
			Block at = world.getBlock(lx, top, lz);
			if (at == Blocks.sand || at == Blocks.water) {
				flag = true;
				break;
			}
		}
		return flag ? new RiverMouth(c, raw, dx, dz, dxn, dzn, h) : null;
	}

	/*
	@Deprecated
	private ImmutablePair<Coordinate, Double> findNextTarget(World world, Coordinate root, double step, double pa, Coordinate from, RiverMouth river) {
		double ra = 30;
		double da = 2.5;
		double angmin = pa-ra;
		double angmax = pa+ra;
		final int h = from.yCoord;
		ArrayList<ImmutablePair<Coordinate, Double>> li = new ArrayList();
		for (double ang = angmin; ang <= angmax; ang += da) {
			int dx = MathHelper.floor_double(from.xCoord+step*Math.cos(Math.toRadians(ang)));
			int dz = MathHelper.floor_double(from.zCoord+step*Math.sin(Math.toRadians(ang)));
			if (CritterPet.isPinkForest(world, dx, dz)) {
				double top = DecoratorPinkForest.getAverageHeight(world, dx, dz, 4);
				if (true || top >= h-6) {
					/*
					double dd = c.getDistanceTo(root);
					double dw = dd*0.8;
					double wx = dw*river.cosTangent;
					double wz = dw*river.sinTangent;
					int x0 = MathHelper.floor_double(root.xCoord-wx);
					int x1 = MathHelper.floor_double(root.xCoord+wx);
					int z0 = MathHelper.floor_double(root.zCoord-wz);
					int z1 = MathHelper.floor_double(root.zCoord+wz);
	 *//*
	Coordinate c = new Coordinate(dx, (int)top, dz);
	li.add(new ImmutablePair(c, ang));
}
}
}
if (li.isEmpty())
	return null;
Collections.sort(li, new Comparator<ImmutablePair<Coordinate, Double>>() {
	@Override
	public int compare(ImmutablePair<Coordinate, Double> o1, ImmutablePair<Coordinate, Double> o2) {
		double dy1 = o1.left.yCoord-h;
		double dy2 = o2.left.yCoord-h;
		if ((dy1 > 0 && dy2 > 0) || (dy1 < 0 && dy2 < 0) || (dy1 == 0 && dy2 == 0)) {
			return Double.compare(dy1, dy2);
		}
		else if (dy1 > 0) {
			return -1;
		}
		else if (dy2 > 0) {
			return 1;
		}
		return 0; //impossible
	}
});
return li.get(0);
}
	  */
	private static class RiverMouth {

		private final Coordinate location;
		private final double angle;
		private final double cosTangent;
		private final double sinTangent;
		private final double cosNormal;
		private final double sinNormal;
		private final double averageHeight;

		public RiverMouth(Coordinate c, double raw, double dx, double dz, double dxn, double dzn, double h) {
			location = c;
			angle = raw;
			cosNormal = dxn;
			sinNormal = dzn;
			cosTangent = dx;
			sinTangent = dz;
			averageHeight = h;
		}

	}

	private static class River {

		private final RiverMouth mouth;
		private final Coordinate rootPosition;

		private final HashMap<Coordinate, BlockCandidate> riverBlocks = new HashMap();
		private final HashMap<Coordinate, BlockCandidate> sandBlocks = new HashMap();

		private final ArrayList<Coordinate> path = new ArrayList();
		private final ArrayList<BlockCandidate> unconverted = new ArrayList();

		public River(Coordinate cx, RiverMouth rm) {
			mouth = rm;
			rootPosition = cx;
		}

		private void calculate(World world) {
			int r = 3;
			int d = 4;

			double exitY = mouth.averageHeight;
			for (double k = 0; k <= 10; k += 0.5) {
				int dx = MathHelper.floor_double(rootPosition.xCoord-k*mouth.cosNormal);
				int dz = MathHelper.floor_double(rootPosition.zCoord-k*mouth.sinNormal);
				int top = DecoratorPinkForest.getTrueTopAt(world, dx, dz);
				exitY += top;
			}
			exitY /= 12;
			path.add(rootPosition);
			double pa = mouth.angle+90;
			double h = mouth.averageHeight;
			//double step = 9;
			Coordinate end = null;
			for (double step = 0; step < 40; step += 0.5) {
				int dx = MathHelper.floor_double(rootPosition.xCoord-step*mouth.cosNormal);
				int dz = MathHelper.floor_double(rootPosition.zCoord-step*mouth.sinNormal);
				int top = DecoratorPinkForest.getTrueTopAt(world, dx, dz);
				Coordinate c2 = new Coordinate(dx, top, dz);
				if (!path.contains(c2))
					path.add(c2);
				if (c2.getBlock(world) == Blocks.sand) {
					end = c2;
					break;
				}
			}
			/*
	Coordinate c2 = new Coordinate(dx, h, dz);
	while (c2 != null && path.size() < 4) {
		path.add(c2);
		ImmutablePair<Coordinate, Double> pair = this.findNextTarget(c, step, pa, c2, river);
		c2 = pair != null ? pair.left : null;
		pa = pair != null ? pair.right : pa;
	}*/
			Spline s = new Spline(SplineType.CENTRIPETAL);
			for (Coordinate c3 : path) {
				s.addPoint(new BasicSplinePoint(c3.xCoord+0.5, c3.yCoord+0.5, c3.zCoord+0.5));
			}
			List<DecimalPosition> li = s.get(8, false);
			for (DecimalPosition p : li) {
				for (int i = -r; i <= r; i++) {
					for (int k = -r; k <= r; k++) {
						if (i*i+k*k <= r*r) {
							int ddx = MathHelper.floor_double(p.xCoord+i);
							int ddz = MathHelper.floor_double(p.zCoord+k);
							int top = DecoratorPinkForest.getTrueTopAt(world, ddx, ddz);
							if (CritterPet.isPinkForest(world, ddx, ddz) && DecoratorPinkForest.isTerrain(world, ddx, top, ddz) && this.countSolidSides(world, ddx, top, ddz) >= 2) {
								Coordinate c2 = new Coordinate(ddx, top, ddz);
								riverBlocks.put(c2.to2D(), new BlockCandidate(c2, false));
							}
						}
					}
				}
			}

			SandFinder sf = new SandFinder(end.xCoord, end.yCoord, end.zCoord);
			sf.complete(world);
			Collection<Coordinate> ca = sf.getTotalSearchedCoords();

			/*
			BlockArray arr = new BlockArray();
			arr.extraSpread = true;
			arr.maxDepth = 2400;
			arr.recursiveAdd(world, end.xCoord, end.yCoord, end.zCoord, Blocks.sand);
			Collection<Coordinate> ca = arr.keySet();
			 */
			for (Coordinate c2 : ca) {
				sandBlocks.put(c2.to2D(), new BlockCandidate(c2, true));
			}

			for (BlockCandidate sb : riverBlocks.values()) {
				sb.calculate(world, this.getAverageAround(sb.location));
			}
			for (BlockCandidate sb : sandBlocks.values()) {
				sb.calculate(world, this.getAverageAround(sb.location));
			}
		}

		private int countSolidSides(World world, int x, int y, int z) {
			int solid = 0;
			for (int i = 0; i < 4; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i+2];
				int dx = x+dir.offsetX;
				int dz = z+dir.offsetZ;
				if (!ReikaWorldHelper.softBlocks(world, dx, y, dz))
					solid++;
			}
			return solid;
		}

		private void generate(World world) {
			this.carve(riverBlocks, world, Blocks.flowing_water, true);
			this.carve(sandBlocks, world, Blocks.water, false);

			for (BlockCandidate sb : unconverted) {
				if (sb.isLeftover(world)) {
					sb.location.setBlock(world, Blocks.flowing_water);
				}
			}
		}

		private void carve(HashMap<Coordinate, BlockCandidate> map, World world, Block put, boolean force) {
			for (Entry<Coordinate, BlockCandidate> e : map.entrySet()) {
				Coordinate c2 = e.getKey();
				BlockCandidate at = e.getValue();
				if (!force && !at.putWater) {
					unconverted.add(at);
					continue;
				}
				int y = at.averageY;
				if (force) {
					c2.setY(y).setBlock(world, Blocks.air);
					y--;
				}
				c2.setY(y).setBlock(world, put);
				for (int i = 0; i < 4; i++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i+2];
					Coordinate c3 = c2.offset(dir.offsetX, 0, dir.offsetZ);
					Block pos = c3.getBlock(world);
					if (pos == Blocks.grass || pos == Blocks.dirt || pos == Blocks.stone || pos == Blocks.gravel) {
						c3.setBlock(world, Blocks.sand);
					}
				}
				c2.setY(y-1).setBlock(world, Blocks.sand);
				int yat = at.location.yCoord;
				if (y < yat) {
					for (int dy = y+1; dy <= yat; dy++) {
						c2.setY(dy).setBlock(world, Blocks.air);
					}
				}
				int dy = y+2;
				if (world.getBlock(c2.xCoord, dy, c2.zCoord) == CritterPet.log && world.getBlockMetadata(c2.xCoord, dy, c2.zCoord) <= 3) {
					while (dy >= 0 && (ReikaWorldHelper.softBlocks(world, c2.xCoord, dy, c2.zCoord) || world.getBlock(c2.xCoord, dy, c2.zCoord) == CritterPet.log)) {
						world.setBlock(c2.xCoord, dy, c2.zCoord, CritterPet.log, 0, 2);
						dy--;
					}
					world.setBlock(c2.xCoord, dy, c2.zCoord, Blocks.dirt);
				}
			}
		}

		private int getAverageAround(Coordinate c) {
			int r = 6;//4;//3;
			int n = 0;
			double avg = 0;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					Coordinate c2 = c.offset(i, 0, k);
					BlockCandidate at = riverBlocks.get(c2);
					if (at == null) {
						at = sandBlocks.get(c2);
					}
					if (at != null) {
						avg += at.location.yCoord;
						n++;
					}
				}
			}
			return n > 0 ? (int)(avg/n) : c.yCoord;
		}

	}

	private static class BlockCandidate {

		private final Coordinate location;
		private final boolean wasSand;
		private boolean putWater;
		private int averageY;

		private BlockCandidate(Coordinate c, boolean sand) {
			location = c;
			wasSand = sand;
		}

		public boolean isLeftover(World world) {
			for (int i = 0; i < 4; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i+2];
				int dx = location.xCoord+dir.offsetX;
				int dz = location.zCoord+dir.offsetZ;
				int y = averageY;
				Block at = world.getBlock(dx, y, dz);
				while (y > 0 && at == Blocks.air) {
					y--;
					at = world.getBlock(dx, y, dz);
				}
				if (at == Blocks.water || at == Blocks.flowing_water || !ReikaWorldHelper.softBlocks(world, dx, averageY, dz))
					continue;
				return false;
			}
			return true;
		}

		private void calculate(World world, int y) {
			putWater = this.isValidWater(world, y);
			averageY = y;
		}

		private boolean isValidWater(World world, int y) {
			return isValidWater(world, location.yCoord, location.xCoord, y, location.zCoord);
		}

		private static boolean isValidWater(World world, int y0, int x, int y, int z) {
			//int solid = 0;
			int sand = 0;
			for (int i = 0; i < 4; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i+2];
				int dx = x+dir.offsetX;
				int dz = z+dir.offsetZ;
				//if (!ReikaWorldHelper.softBlocks(world, dx, y, dz))
				//	solid++;
				if (isBarrier(world, dx, y, dz)) {
					for (int h = -1; h <= 1; h++) {
						if (world.getBlock(dx, y0+h, dz) == Blocks.sand)
							sand++;
					}
				}
				else {
					if (!CritterPet.isPinkForest(world, dx, dz))
						return false;
					int dy = y;
					while (dy > 0 && !isBarrier(world, dx, dy, dz)) {
						dy--;
					}
					Block at = world.getBlock(dx, dy, dz);
					if (at == Blocks.water || at == Blocks.flowing_water)
						continue;
					if (at != Blocks.sand || !isValidWater(world, y0, dx, dy, dz))
						return false;
				}
			}
			//return solid >= 3 && sand > 1;
			return sand > 1;
		}

		private static boolean isBarrier(World world, int dx, int dy, int dz) {
			Block at = world.getBlock(dx, dy, dz);
			return !ReikaWorldHelper.softBlocks(world, dx, dy, dz) || at == Blocks.water || at == Blocks.flowing_water;
		}

	}

	private static class SandFinder extends BreadthFirstSearch {

		private double highestY = -1;

		private final PropagationCondition propagation = new PropagationCondition() {

			@Override
			public boolean isValidLocation(World world, int x, int y, int z, Coordinate from) {
				return (y >= from.yCoord-1 || root.yCoord-y <= 2) && y >= highestY-2 && CritterPet.isPinkForest(world, x, z) && world.getBlock(x, y, z) == Blocks.sand;
			}

		};
		private final TerminationCondition terminate = new TerminationCondition() {

			@Override
			public boolean isValidTerminus(World world, int x, int y, int z) {
				return false;
			}

		};

		public SandFinder(int x, int y, int z) {
			super(x, y, z);
		}

		private void complete(World world) {
			this.complete(world, propagation, terminate );
		}

		@Override
		protected ArrayList<Coordinate> getNextSearchCoordsFor(World world, Coordinate c) {
			highestY = Math.max(DecoratorPinkForest.getAverageHeight(world, c.xCoord, c.zCoord, 2, (w, x, y, z) -> w.getBlock(x, y, z) == Blocks.sand && CritterPet.isPinkForest(w, x, z)), highestY);
			ArrayList<Coordinate> ret = new ArrayList();
			/*
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						ret.add(c.offset(i, j, k));
					}
				}
			}
			 */
			/*
			for (int i = 0; i < 4; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i+2];
				ret.add(c.offset(dir, 1));
			}
			for (int i = -1; i <= 1; i++) {
				for (int k = -1; k <= 1; k++) {
					ret.add(c.offset(i, -1, k));
					ret.add(c.offset(i, 1, k));
				}
			}
			 */
			for (int i = -2; i <= 2; i++) {
				for (int j = -1; j <= 1; j++) {
					for (int k = -2; k <= 2; k++) {
						ret.add(c.offset(i, j, k));
					}
				}
			}
			return ret;
		}

	}
}
