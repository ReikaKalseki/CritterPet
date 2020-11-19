package Reika.CritterPet.Biome;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenerator;

import Reika.ChromatiCraft.API.Interfaces.DyeTreeBlocker;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//Village gen is fucked
//gens with snow
public class BiomePinkForest extends BiomeGenBase implements DyeTreeBlocker {

	private final PinkTreeGenerator treeGen = new PinkTreeGenerator();
	private final GiantPinkTreeGenerator giantTreeGen = new GiantPinkTreeGenerator();

	PinkForestNoiseData noise;
	private final PinkForestTerrainShaper terrain = new PinkForestTerrainShaper();

	public BiomePinkForest(int id) {
		super(id);
		biomeName = "Pink Birch Forest";
		theBiomeDecorator.treesPerChunk = 6;
		theBiomeDecorator.grassPerChunk = 12;
		enableRain = true;
		enableSnow = true; //but melt the snow when it is daytime and sunny, and disallow all water freezing to ice

		temperature = 0.14F;
		rainfall = 0.65F;

		fillerBlock = Blocks.stone;

		this.setHeight(new Height(1.75F, 0.25F));
	}

	@Override
	public void genTerrainBlocks(World world, Random rand, Block[] arr, byte[] m, int x, int z, double stoneNoise) {
		super.genTerrainBlocks(world, rand, arr, m, x, z, stoneNoise);
	}

	public void shapeTerrain(World world, int chunkX, int chunkZ, Block[] blockArray, byte[] metaArray) {
		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < 16; k++) {
				int x = chunkX*16+i;
				int z = chunkZ*16+k;
				BiomeGenBase b = world.getWorldChunkManager().getBiomeGenAt(x, z);
				if (b == this) {
					terrain.generateColumn(world, x, z, chunkX, chunkZ, blockArray, metaArray, b);
				}
			}
		}
	}

	@Override
	public float getFloatTemperature(int x, int y, int z) {
		return (float)ReikaMathLibrary.normalizeToBounds(super.getFloatTemperature(x, y, z), 0.1, 0.14, 0, 1);
	}

	@Override
	public boolean canSpawnLightningBolt() {
		return true;
	}

	@Override
	public BiomeDecorator createBiomeDecorator() {
		return new DecoratorPinkForest();
	}

	@Override
	public WorldGenerator getRandomWorldGenForGrass(Random rand) {
		return new WorldGenPinkGrass();
	}

	@Override
	public WorldGenAbstractTree func_150567_a(Random rand) {
		return rand.nextInt(4) == 0 ? giantTreeGen : treeGen;
	}

	@Override
	public void plantFlower(World world, Random rand, int x, int y, int z) {
		//plant bushes
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBiomeGrassColor(int x, int y, int z) { //less intense with Y
		//https://i.imgur.com/uj47T6U.jpg
		//https://i.imgur.com/ThGytyc.jpg
		//CD8988 to E0A29B to EDBFB1
		int min = 100;//92;//64;
		int max = 120;//112;//72;
		float f = 0;
		if (y >= max) {
			f = 1;
		}
		else if (y > min) {
			f = (y-min)/(float)(max-min);
		}
		World world = Minecraft.getMinecraft().theWorld;
		return ReikaColorAPI.mixColors(0xEDBFB1, 0xCD8988, f); //this.isRoad(world, x, z) ? ReikaColorAPI.getModifiedHue(0xff0000, this.getSubBiome(world, x, z).ordinal()*120) : 0;//
		//return this.getRoadFactor(world, x, z) > Math.abs(noise.roadEdgeNoise.getValue(x, z)*0.75) ? 0xffffff : 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBiomeFoliageColor(int x, int y, int z) { //less intense with Y
		//https://i.imgur.com/1Lu8tSU.jpg
		//https://i.imgur.com/bPN7Kqk.jpg
		//E95F84 through FA8FAA to FFB3D1
		int min = 108;//82;//64;
		int max = 150;//112;//128;
		float f = 0;
		if (y >= max) {
			f = 1;
		}
		else if (y > min) {
			f = (y-min)/(float)(max-min);
		}
		return ReikaColorAPI.mixColors(0xFFB3D1, 0xE95F84, f);
	}

	public BiomeSection getSubBiome(World world, int x, int z) {
		this.initNoise(world);
		double d = 13;//14;//9;//12;//4;
		int avg = 0;
		int dd = 5;
		/*
		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
		 */
		double dx = x+0*dd+0.5+noise.sectionDisplacementNoiseX.getValue(x+0*dd, z+0*dd)*d;
		double dz = z+0*dd+0.5+noise.sectionDisplacementNoiseZ.getValue(x+0*dd, z+0*dd)*d;
		double val = noise.sectionNoise.getValue(dx, dz);
		double n = ReikaMathLibrary.normalizeToBounds(val, 0, BiomeSection.list.length-0.001);
		int idx = MathHelper.floor_double(n);
		avg = idx;
		/*avg += idx;
			}
		}
		avg /= 9;*/
		return BiomeSection.list[avg];
	}

	int getUpthrust(World world, int x, int z) {
		this.initNoise(world);
		int min = 10; //was 0 then 18
		int delta = 12; //was 12 then 9, then 6
		return (int)Math.round(ReikaMathLibrary.normalizeToBounds(noise.upthrustNoise.getValue(x, z), delta, min+delta));
	}

	int getMiniCliffDelta(World world, int x, int z) {
		this.initNoise(world);
		double size = 3.5;
		int min = 1; //was 0-5, then 1-4
		double val = Math.abs(noise.streamsMiniCliffNoise.getValue(x, z));
		val = ReikaMathLibrary.normalizeToBounds(val, min, size, 0, 1);
		double n = Math.abs(noise.roadNoise.getValue(x, z));
		//if (n < 0.5)
		//	val *= n*2;
		double f = Math.max(0, (1-n*1.25)*0.875+0.125);
		//double f = Math.max(0, n > 0 ? 0.5/n : 0);
		double thresh = 0.33; //was 0.5, then 0.25
		if (n < thresh)
			f = 0;
		double sc = 1;//.125;
		double ret = Math.min(size, val*(size*sc)*f);
		return (int)Math.round(ret);
	}

	int getSwampDepression(World world, int x, int z) {
		this.initNoise(world);
		double val = noise.swampDepressionNoise.getValue(x, z);
		double ret = Math.max(0, val)*4;
		return (int)Math.round(ret);
	}

	int getDirtThickness(World world, int x, int z) {
		return (int)Math.round(ReikaMathLibrary.normalizeToBounds(noise.dirtThicknessNoise.getValue(x, z), 1, 3.6));
	}

	public double getRoadFactor(World world, int x, int z) {
		this.initNoise(world);
		double n = Math.abs(noise.roadNoise.getValue(x, z));
		double thick = this.getSubBiome(world, x, z).getRoadThickness();
		if (thick <= 0 || n >= thick)
			return 0;
		double f = n/thick;
		return Math.max(0, 1D-f*f*1.5);
	}

	public boolean isRoad(World world, int x, int z) {
		return this.getRoadFactor(world, x, z) > 0;
	}

	private void initNoise(World world) {
		if (noise == null || noise.seed != world.getSeed()) {
			noise = new PinkForestNoiseData(world.getSeed());
		}
	}

	public static enum BiomeSection {
		FOREST,
		STREAMS,
		SWAMP;

		private static final BiomeSection[] list = values();

		public double getRoadThickness() {
			switch(this) {
				case FOREST:
					return 0.1875;
				case STREAMS:
					return 0.125;
				case SWAMP:
					return 0.25;
				default:
					return 0;
			}
		}
	}

}
