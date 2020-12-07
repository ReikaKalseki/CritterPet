package Reika.CritterPet.Biome;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenerator;

import Reika.ChromatiCraft.API.Interfaces.DyeTreeBlocker;
import Reika.ChromatiCraft.API.Interfaces.NonconvertibleBiome;
import Reika.CritterPet.CritterPet;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Math.Noise.NoiseGeneratorBase;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ModSpawnEntry;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BiomePinkForest extends BiomeGenBase implements DyeTreeBlocker, NonconvertibleBiome {

	//private final PinkTreeGenerator treeGen = new PinkTreeGenerator();
	//private final GiantPinkTreeGenerator giantTreeGen = new GiantPinkTreeGenerator();
	private static final NoiseGeneratorBase waterColorMix = new SimplexNoiseGenerator(~System.currentTimeMillis()).setFrequency(1/3.5D);
	private static final NoiseGeneratorBase waterBrightnessMix = new SimplexNoiseGenerator(~System.currentTimeMillis()).setFrequency(1.5D);

	PinkForestNoiseData noise;
	private final PinkForestTerrainShaper terrain = new PinkForestTerrainShaper();

	public BiomePinkForest(int id) {
		super(id);
		biomeName = "Pink Birch Forest";
		theBiomeDecorator.treesPerChunk = 6;
		theBiomeDecorator.grassPerChunk = 24; //was 12
		enableRain = true;
		enableSnow = true; //but melt the snow when it is daytime and sunny, and disallow all water freezing to ice

		temperature = 0.14F;
		rainfall = 0.65F;

		fillerBlock = Blocks.stone;

		this.setHeight(new Height(1.75F, 0.25F));

		spawnableMonsterList.clear();

		//base vanilla mobs, most with 1/4th spawn rates (or 1/3 for creepers and halved for endermen)
		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityZombie.class, 25, 4, 4));
		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySkeleton.class, 25, 4, 4));
		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityCreeper.class, 33, 4, 4));
		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityEnderman.class, 5, 1, 2));
		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 100, 4, 4));

		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySpider.class, 100, 4, 4)); //large stinger stand-in
		spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityCaveSpider.class, 50, 6, 6)); //basic (small) stinger stand-in
		if (ModList.TWILIGHT.isLoaded()) {
			ModSpawnEntry msp = new ModSpawnEntry(ModList.TWILIGHT, "twilightforest.entity.EntityTFHedgeSpider", 80, 4, 4); //elite stinger stand-in
			spawnableMonsterList.add(msp.getEntry());
		}
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
				if (b == this) { // || b == CritterPet.pinkriver) {
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
		return rand.nextInt(4) == 0 ? new GiantPinkTreeGenerator(false) : new PinkTreeGenerator();
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
		this.initNoise(world);
		return ReikaColorAPI.mixColors(0xEDBFB1, 0xCD8988, f); //this.isRoad(world, x, z) ? ReikaColorAPI.getModifiedHue(0xff0000, this.getSubBiome(world, x, z).ordinal()*120) : 0;//
		//return this.getRoadFactor(world, x, z) > Math.abs(noise.roadEdgeNoise.getValue(x, z)*0.75) ? 0xffffff : 0;
		//return ReikaColorAPI.mixColors(0, 0xffffff, (float)ReikaMathLibrary.normalizeToBounds(noise.riverNoise.getValue(x, z), 0, 1));
		//return ReikaColorAPI.getModifiedHue(0xff0000,  noise.riverNoise.getEdgeRatio(x, z) < 5 ? 0 : (int)((noise.riverNoise.getEdgeRatio(x, z)-5)*5));
		/*
		double df = 1/36D;
		double sc = 15;
		SimplexNoiseGenerator s1 = (SimplexNoiseGenerator)new SimplexNoiseGenerator(world.getSeed()-34589).setFrequency(df).addOctave(4.1, 0.17).addOctave(0.08, 15.7);
		SimplexNoiseGenerator s2 = (SimplexNoiseGenerator)new SimplexNoiseGenerator(world.getSeed()-34589).setFrequency(df).addOctave(4.1, 0.17).addOctave(0.08, 15.7);
		s1.clampEdge = true;
		s2.clampEdge = true;
		return ReikaColorAPI.mixColors(0, 0xffffff,  noise.riverNoise.getEdgeRatio(x+s1.getValue(x, z)*sc, z+s2.getValue(x, z)*sc) < 5 ? 0 : 1);
		 */
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

	public int getWaterColor(IBlockAccess world, int x, int y, int z, int l) {
		float f = (float)ReikaMathLibrary.normalizeToBounds(waterColorMix.getValue(x, z), 0, 1);
		//was 3C6D76, 144D5A, then 0x62939C, 0x144D5A
		int ret = ReikaColorAPI.mixColors(0xA2E2EE, 0x4F99AA, f);//this.getWaterColorMultiplier();
		f = (float)ReikaMathLibrary.normalizeToBounds(waterBrightnessMix.getValue(x, z), 0, 1.35);//was 1.5F
		f = Math.max(1, f);
		ret = ReikaColorAPI.getColorWithBrightnessMultiplier(ret, f);
		boolean flag = true;
		int r = 3;//6;//4;
		/*
		f = 1;
		for (int d = 1; d < r && flag; d++) {
			for (int i = 0; i < 4; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				int dx = x+d*dir.offsetX;
				int dz = z+d*dir.offsetZ;
				if (!CritterPet.isPinkForest(world.getBiomeGenForCoords(dx, dz))) {
					f = d/(float)r;
					flag = false;
					break;
				}
			}
		}
		 */
		int nb = 0;
		int n = 0;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				n++;
				if (CritterPet.isPinkForest(world.getBiomeGenForCoords(x+i, z+k)))
					nb++;
			}
		}
		flag = nb == n;
		if (!flag) {
			f = nb/(float)n;
			ret = ReikaColorAPI.mixColors(ret, 0x1845ff, f);
		}
		return ret;
	}

	public BiomeSection getSubBiome(World world, int x, int z) {
		this.initNoise(world);
		/*
		int avg = 0;
		int dd = 5;
		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
		 */
		double val = noise.sectionNoise.getValue(x, z);
		double n = ReikaMathLibrary.normalizeToBounds(val, 0, BiomeSection.list.length-0.001);
		int idx = MathHelper.floor_double(n);
		/*avg = idx;
		avg += idx;
			}
		}
		avg /= 9;*/
		return BiomeSection.list[idx];
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
	/*
	int getRiverDelta(World world, int x, int z) {
		//this.initNoise(world);
		int depth = 5;
		//double val = Math.abs(noise.riverNoise.getValue(x, z));

		double thresh = 0.2;
		double val = noise.riverNoise.getEdgeRatio(x, z);
		if (val > thresh) {
			return 0;
		}
		return (int)((1-val/thresh)*depth);
	}*/

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
		double ret = Math.max(0, 1D-f*f*1.5);
		return ret*this.getRoadBiomeEdgeFactor(world, x, z);
	}

	private double getRoadBiomeEdgeFactor(World world, int x, int z) {
		int edge = getNearestBiomeEdge(world, x, z, 18);
		if (edge < 0 || edge > 18)
			return 1;
		if (edge <= 8)
			return 0;
		return (edge-8)/10D;
	}

	public static int getNearestBiomeEdge(World world, int x, int z, int r) {
		for (int d = 1; d <= r; d++) {
			for (CubeDirections dir : CubeDirections.list) {
				int dx = x+d*dir.directionX;
				int dz = z+d*dir.directionZ;
				if (!CritterPet.isPinkForest(world, dx, dz)) {
					return d;
				}
			}
		}
		return -1;
	}

	public boolean isRoad(World world, int x, int z) {
		return this.getRoadFactor(world, x, z) > 0;
	}

	private void initNoise(World world) {
		if (noise == null || noise.seed != world.getSeed() | true) {
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
