/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet.Biome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import Reika.CritterPet.CritterPet;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Instantiable.Worldgen.StackableBiomeDecorator;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class DecoratorPinkForest extends StackableBiomeDecorator {

	private final WorldGenRedBamboo redBambooGenerator = new WorldGenRedBamboo();
	//private final WorldGenPinkRiver riverGenerator = new WorldGenPinkRiver();

	//private int riverHeight;
	//private int glassHeight;

	private static final double RIVER_DEPTH = 5.5;
	private static final double RIVER_WATER_MAX_DEPTH = 3;

	private final HashMap<Coordinate, BiomeFootprint> biomeColumns = new HashMap();

	public DecoratorPinkForest() {
		super();
	}

	@Override
	protected void genDecorations(BiomeGenBase biome) {
		/*
		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < 16; k++) {
				int dx = chunk_X+i;
				int dz = chunk_Z+k;
				int top = this.getTrueTopAt(currentWorld, dx, dz);
				if (currentWorld.getBiomeGenForCoords(dx, dz) == biome) {
					//this.cleanColumn(currentWorld, dx, top, dz);
					double river = PinkForestRiverShaper.instance.getIntensity(dx, dz);
					if (river > 0) {
						double avg = this.getAverageHeight(currentWorld, dx, dz, 12);
						double yb = avg-RIVER_DEPTH;
						double yRes = Math.min(top, river*yb+(1-river)*Math.min(top, avg));
						if (yRes < avg && yRes < top) {
							int yf = (int)yRes;
							for (int y = yf+1; y <= top; y++) {
								Block b = Blocks.air;
								if (y-yf <= RIVER_WATER_MAX_DEPTH && y < avg)
									b = Blocks.water;
								currentWorld.setBlock(dx, y, dz, b);
							}
							currentWorld.setBlock(dx, yf, dz, Blocks.sand);
							currentWorld.setBlock(dx, yf-1, dz, Blocks.dirt);
						}
					}
				}
			}
		}
		 */

		/*
		if (ReikaWorldHelper.getNaturalGennedBiomeAt(currentWorld, chunk_X, chunk_Z) == biome) {
			new WorldGenPinkRiver().generate(currentWorld, randomGenerator, chunk_X, 0, chunk_Z);
			new WorldGenUraniumCave().generate(currentWorld, randomGenerator, chunk_X, 0, chunk_Z);
		}
		 */

		Coordinate c = new Coordinate(chunk_X, 0, chunk_Z);
		BiomeFootprint at = biomeColumns.get(c);
		if (at == null) {
			if (ReikaWorldHelper.getNaturalGennedBiomeAt(currentWorld, chunk_X, chunk_Z) == biome) {
				BiomeFootprint bf = new BiomeFootprint();
				if (bf.calculate(currentWorld, chunk_X, chunk_Z)) {
					for (Coordinate c2 : bf.getCoords()) {
						biomeColumns.put(c2, bf);
					}
				}
				at = bf;
			}
		}
		if (at != null) {
			Vec3 center = at.getCenter();
			int x = MathHelper.floor_double(center.xCoord);
			int z = MathHelper.floor_double(center.zCoord);
			if (x >= chunk_X && z >= chunk_Z && x-chunk_X < 16 && z-chunk_Z < 16) {
				this.generateUniqueCenterFeatures(x, z, at);
			}
		}

		super.genDecorations(biome);

		int x = chunk_X + randomGenerator.nextInt(16) + 8;
		int z = chunk_Z + randomGenerator.nextInt(16) + 8;

		int top = currentWorld.getTopSolidOrLiquidBlock(x, z);

		BiomePinkForest forest = (BiomePinkForest)biome;
		redBambooGenerator.setFrequency(forest.getSubBiome(currentWorld, x, z));
		redBambooGenerator.generate(currentWorld, randomGenerator, x, top, z);
	}

	private void generateUniqueCenterFeatures(int x, int z, BiomeFootprint bf) {
		ArrayList<Coordinate> li = new ArrayList(bf.getEdges());
		if (li.isEmpty())
			return;
		HashMap<Coordinate, RiverMouth> valid = new HashMap();
		int idx = randomGenerator.nextInt(li.size());
		Coordinate c = li.remove(idx);
		while (valid.size() < 39999 && !li.isEmpty()) {
			for (Coordinate c2 : valid.keySet()) {
				if (c.getTaxicabDistanceTo(c2) <= 64) {
					c = null;
					break;
				}
			}
			if (c != null) {
				RiverMouth ang = this.isValidRiverEndpoint(x, z, c, bf);
				if (ang != null) {
					valid.put(c, ang);
				}
			}
			idx = randomGenerator.nextInt(li.size());
			c = li.remove(idx);
		}
		for (Entry<Coordinate, RiverMouth> e : valid.entrySet()) {
			this.generateRiverAt(e.getKey(), e.getValue());
		}
	}

	private void generateRiverAt(Coordinate c, RiverMouth river) {
		int r = 3;
		int d = 4;
		/*
		for (double i = -r; i <= r; i += 0.5) {
			for (double k = -2; k <= 18; k += 0.5) {
				int dx = MathHelper.floor_double(c.xCoord-k*river.cosNormal+i*river.cosTangent);
				int dz = MathHelper.floor_double(c.zCoord-k*river.sinNormal+i*river.sinTangent);
				if (CritterPet.isPinkForest(currentWorld, dx, dz)) {
					//currentWorld.setBlock(dx, 140, dz, Blocks.wool, Math.abs(i), 2);

					int top = this.getTrueTopAt(currentWorld, dx, dz);
					double depth = d*Math.min(1, 1.125*Math.sqrt(1-Math.abs(i)/r));
					double target = river.averageHeight-depth;
					double dh = top-target;
					if (dh > 0) {
						for (int h = 0; h <= dh; h++) {
							int dy = top-h;
							currentWorld.setBlock(dx, dy, dz, Blocks.glass);
						}
					}
					currentWorld.setBlock(dx, (int)target-1, dz, Blocks.clay);
				}
			}
		}*/
		double exitY = river.averageHeight;
		for (double k = 0; k <= 10; k += 0.5) {
			int dx = MathHelper.floor_double(c.xCoord-k*river.cosNormal);
			int dz = MathHelper.floor_double(c.zCoord-k*river.sinNormal);
			int top = this.getTrueTopAt(currentWorld, dx, dz);
			exitY += top;
		}
		exitY /= 12;
		ArrayList<Coordinate> path = new ArrayList();
		path.add(c);
		double pa = river.angle+90;
		double h = river.averageHeight;
		double step = 9;
		int dx = MathHelper.floor_double(c.xCoord-step*river.cosNormal);
		int dz = MathHelper.floor_double(c.zCoord-step*river.sinNormal);
		Coordinate c2 = new Coordinate(dx, h, dz);
		while (c2 != null && path.size() < 4) {
			path.add(c2);
			ImmutablePair<Coordinate, Double> pair = this.findNextTarget(c, step, pa, c2, river);
			c2 = pair != null ? pair.left : null;
			pa = pair != null ? pair.right : pa;
		}
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
						int top = this.getTrueTopAt(currentWorld, ddx, ddz);
						currentWorld.setBlock(ddx, top, ddz, Blocks.glass);
						currentWorld.setBlock(ddx, top+1, ddz, Blocks.glass);
						currentWorld.setBlock(ddx, top-1, ddz, Blocks.obsidian);
					}
				}
			}
		}
	}

	private ImmutablePair<Coordinate, Double> findNextTarget(Coordinate root, double step, double pa, Coordinate from, RiverMouth river) {
		double ra = 30;
		double da = 2.5;
		double angmin = pa-ra;
		double angmax = pa+ra;
		final int h = from.yCoord;
		ArrayList<ImmutablePair<Coordinate, Double>> li = new ArrayList();
		for (double ang = angmin; ang <= angmax; ang += da) {
			int dx = MathHelper.floor_double(from.xCoord+step*Math.cos(Math.toRadians(ang)));
			int dz = MathHelper.floor_double(from.zCoord+step*Math.sin(Math.toRadians(ang)));
			if (CritterPet.isPinkForest(currentWorld, dx, dz)) {
				double top = this.getAverageHeight(currentWorld, dx, dz, 4);
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
					 */
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

	private void addStep(Coordinate from, Coordinate to, double step, int h, RiverMouth river, ArrayList<Coordinate> path) {

	}

	private RiverMouth isValidRiverEndpoint(int x, int z, Coordinate c, BiomeFootprint bf) {
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
		if (!CritterPet.isPinkForest(currentWorld, x0, z0))
			return null;
		double h = this.getAverageHeight(currentWorld, x0, z0, 3);
		for (int dl = -1; dl <= 6; dl += 2) {
			int ddl = dl+dd;
			int x1 = MathHelper.floor_double(c.xCoord+w*dx-dxn*ddl);
			int z1 = MathHelper.floor_double(c.zCoord+w*dz-dzn*ddl);
			int x2 = MathHelper.floor_double(c.xCoord-w*dx-dxn*ddl);
			int z2 = MathHelper.floor_double(c.zCoord-w*dz-dzn*ddl);
			if (!CritterPet.isPinkForest(currentWorld, x1, z1))
				return null;
			if (!CritterPet.isPinkForest(currentWorld, x2, z2))
				return null;
			double h1 = this.getAverageHeight(currentWorld, x1, z1, 2);
			double h2 = this.getAverageHeight(currentWorld, x1, z1, 2);
			if (Math.abs(h2-h1) >= 3 || h2 < h-2 || h1 < h-2 || h < h1-10 || h < h2-10)
				return null;
		}
		return new RiverMouth(c, raw, dx, dz, dxn, dzn, h);
	}

	public static int getTrueTopAt(World currentWorld, int dx, int dz) {
		int top = currentWorld.getTopSolidOrLiquidBlock(dx, dz);
		Block at = currentWorld.getBlock(dx, top, dz);
		while (top > 0 && (at == Blocks.air || at == CritterPet.log || at == CritterPet.leaves || at.isWood(currentWorld, dx, top, dz) || at.isLeaves(currentWorld, dx, top, dz) || ReikaWorldHelper.softBlocks(currentWorld, dx, top, dz))) {
			top--;
			at = currentWorld.getBlock(dx, top, dz);
		}
		at = currentWorld.getBlock(dx, top+1, dz);
		while (top < 255 && at == Blocks.glass) {
			top++;
			at = currentWorld.getBlock(dx, top+1, dz);
		}
		return top;
	}

	private void cleanColumn(World world, int x, int top, int z) {
		for (int i = top; i >= top-6; i--) {
			Block b = i == 0 ? Blocks.grass : Blocks.stone;
			Block at = world.getBlock(x, top-i, z);
			if (i > 0 && (at == Blocks.dirt || at == Blocks.grass))
				b = Blocks.dirt;
			if (at != b)
				world.setBlock(x, top-i, z, b);
		}
		/*
		if (river > 0) {
			/*
			riverHeight = -1;
			double avg = this.getAverageHeight(world, x, z, 15); //was 6 then 9
			int watermax = (int)(Math.min(avg-1.5, riverHeight));
			if (watermax > top) {
				for (int i = top+1; i <= watermax; i++) {
					world.setBlock(x, i, z, Blocks.water);
				}
			}
			else {
				world.setBlock(x, top, z, Blocks.sand);
			}
		 */

		/*
			if (!this.tryPlaceWaterAt(world, x, top+1, z)) {
				world.setBlock(x, top, z, Blocks.sand);
			}
		 *//*
		}

		/*
		for (int h = 0; h < 10; h++) {
			if (world.getBlock(x, top+h, z) == Blocks.glass)
				world.setBlock(x, top+h, z, Blocks.air);
		}
		  */
	}

	/*
	private boolean tryPlaceWaterAt(World world, int x, int y, int z) {
		int r = 15;//12;
		ArrayList<ForgeDirection> open = new ArrayList();
		for (int i = 0; i < 4; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i+2];
			for (int d = 1; d <= r; d++) {
				int dx = x+d*dir.offsetX;
				int dz = z+d*dir.offsetZ;
				Block at = world.getBlock(dx, y, dz);
				boolean soft = at != Blocks.sand && at != Blocks.stone && at != Blocks.dirt && at != Blocks.clay;//ReikaWorldHelper.softBlocks(world, dx, y, dz);
				if (!soft) {
					break;
				}
				if (soft && d == r)
					open.add(dir);
			}
		}
		boolean can = open.size() <= 1 || (open.size() == 2 && !ReikaDirectionHelper.arePerpendicular(open.get(0), open.get(1)));
		if (can) {
			world.setBlock(x, y, z, Blocks.water);
			for (int d = 1; d <= r; d++) {
				for (ForgeDirection dir : open) {
					int dy = y;
					int dx = x+d*dir.offsetX;
					int dz = z+d*dir.offsetZ;
					int floor = dy-1;
					while (ReikaWorldHelper.softBlocks(world, x, floor, z)) {
						floor--;
					}
					if (world.getBlock(dx, floor, dz) == Blocks.clay) {
						int max = floor+3;
						for (int dy2 = floor+1; dy2 <= max; dy2++) {
							world.setBlock(dx, dy2, dz, Blocks.water);
						}
					}
				}
			}
		}
		return can;
	}
	 */
	private double getAverageHeight(World world, int x, int z, int r) {
		double avg = 0;
		int n = 0;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				int dx = x+i;
				int dz = z+k;
				int top = this.getTrueTopAt(world, dx, dz);
				/*
				if (world.getBlock(dx, top, dz) == Blocks.clay) {
					riverHeight = Math.max(riverHeight, top+1);

					int glassHeight = -1;
					for (int h = top+1; h < 12; h++) {
						if (world.getBlock(dx, h, dz) == Blocks.glass) {
							glassHeight = h;
							break;
						}
					}
					if (glassHeight >= 0) {
						avg += glassHeight;
						n++;
					}
				}
				else {*/
				avg += top;
				n++;
				//}
			}
		}
		if (n > 0)
			avg /= n;
		return avg;//n == 0 ? riverHeight : avg;
	}

	@Override
	protected ModLogger getLogger() {
		return CritterPet.logger;
	}

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


}
