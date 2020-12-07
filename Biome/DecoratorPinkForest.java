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

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import Reika.CritterPet.CritterPet;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Instantiable.Worldgen.StackableBiomeDecorator;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
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
				BiomewideFeatureGenerator.instance.generateUniqueCenterFeatures(currentWorld, x, z, randomGenerator, at);
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

	@FunctionalInterface
	static interface HeightValidityFunction {

		public boolean apply(World world, int x, int y, int z);

	}

	static double getAverageHeight(World world, int x, int z, int r) {
		return getAverageHeight(world, x, z, r, null);
	}

	static double getAverageHeight(World world, int x, int z, int r, HeightValidityFunction func) {
		double avg = 0;
		int n = 0;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				int dx = x+i;
				int dz = z+k;
				int top = getTrueTopAt(world, dx, dz);
				if (func != null && !func.apply(world, dx, top, dz))
					continue;
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

	static boolean isTerrain(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		return b.isReplaceableOreGen(world, x, y, z, Blocks.stone) || b.getMaterial() == Material.ground || b.getMaterial() == Material.clay || b.getMaterial() == Material.sand || b.isReplaceableOreGen(world, x, y, z, Blocks.grass) || ReikaBlockHelper.isOre(b, world.getBlockMetadata(x, y, z));
	}


}
