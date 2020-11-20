/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet;

import java.io.File;
import java.net.URL;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;

import Reika.CritterPet.Biome.BiomePinkForest;
import Reika.CritterPet.Biome.BlockPinkGrass;
import Reika.CritterPet.Biome.BlockPinkLeaves;
import Reika.CritterPet.Biome.BlockPinkLog;
import Reika.CritterPet.Biome.BlockRedBamboo;
import Reika.CritterPet.Entities.Base.EntitySpiderBase;
import Reika.CritterPet.Registry.CritterOptions;
import Reika.CritterPet.Registry.CritterType;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.DragonAPIMod.LoadProfiler.LoadPhase;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent;
import Reika.DragonAPI.Instantiable.Event.GenLayerRiverEvent;
import Reika.DragonAPI.Instantiable.Event.IceFreezeEvent;
import Reika.DragonAPI.Instantiable.Event.SnowOrIceOnGenEvent;
import Reika.DragonAPI.Instantiable.Event.Client.GrassIconEvent;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Instantiable.IO.ModLogger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod( modid = "CritterPet", name="Critter Pet", version = "v@MAJOR_VERSION@@MINOR_VERSION@", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI")

public class CritterPet extends DragonAPIMod {

	@Instance("CritterPet")
	public static CritterPet instance = new CritterPet();

	public static final ControlledConfig config = new ControlledConfig(instance, CritterOptions.optionList, null);

	public static ItemCritterEgg egg;
	public static ItemTaming tool;

	public static BlockPinkLog log;
	public static BlockRedBamboo bamboo;
	public static BlockPinkLeaves leaves;
	public static BlockPinkGrass grass;

	public static BiomePinkForest pinkforest;
	//public static BiomePinkRiver pinkriver;

	private IIcon biomeGrassIcon;
	private IIcon biomeGrassIconSide;

	public static ModLogger logger;

	@SidedProxy(clientSide="Reika.CritterPet.CritterClient", serverSide="Reika.CritterPet.CritterCommon")
	public static CritterCommon proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		this.startTiming(LoadPhase.PRELOAD);
		this.verifyInstallation();
		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		logger = new ModLogger(instance, false);
		if (DragonOptions.FILELOG.getState())
			logger.setOutput("**_Loading_Log.log");

		egg = new ItemCritterEgg();
		egg.setUnlocalizedName("petcritteregg");
		tool = new ItemTaming();
		tool.setUnlocalizedName("crittertamer");
		GameRegistry.registerItem(egg, "petcritteregg");
		GameRegistry.registerItem(tool, "crittertamer");

		log = new BlockPinkLog();
		GameRegistry.registerBlock(log, null, "pinklog");
		LanguageRegistry.addName(log, "Pink Birch Log");
		bamboo = new BlockRedBamboo();
		GameRegistry.registerBlock(bamboo, null, "redbamboo");
		LanguageRegistry.addName(bamboo, "Red Bamboo");
		leaves = new BlockPinkLeaves();
		GameRegistry.registerBlock(leaves, null, "pinkleaves");
		LanguageRegistry.addName(leaves, "Pink Birch Leaves");
		grass = new BlockPinkGrass();
		GameRegistry.registerBlock(grass, null, "pinkgrass");
		LanguageRegistry.addName(grass, "Pink Grass");

		proxy.registerSounds();

		this.basicSetup(evt);
		this.finishTiming();
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		this.startTiming(LoadPhase.LOAD);
		for (int i = 0; i < CritterType.critterList.length; i++) {
			CritterType type = CritterType.critterList[i];
			if (type.isAvailable()) {
				int id = EntityRegistry.findGlobalUniqueEntityId();
				EntityRegistry.registerGlobalEntityID(type.entityClass, "critterpet."+type.name, id);
				EntityRegistry.registerModEntity(type.entityClass, type.name, id, instance, 32, 20, true);
				type.initializeMapping(id);
				GameRegistry.addShapelessRecipe(new ItemStack(tool, 1, i+1), new ItemStack(tool, 1, 0), type.tamingItem);
				logger.log("Loading Critter Type "+type.name());
			}
			else {
				logger.log("Not Loading Critter Type "+type.name());
			}
		}
		proxy.registerRenderers();
		LanguageRegistry.addName(tool, "Critter Taming Device");
		GameRegistry.addRecipe(new ItemStack(tool), " ID", " II", "I  ", 'I', Items.iron_ingot, 'D', Items.diamond);

		int id = CritterOptions.BIOMEID.getValue();
		if (id >= 0) {
			pinkforest = new BiomePinkForest(CritterOptions.BIOMEID.getValue());
			BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(pinkforest, 4));
			BiomeManager.addSpawnBiome(pinkforest);
			BiomeManager.addStrongholdBiome(pinkforest);
			//BiomeManager.addVillageBiome(pinkforest, true);
			BiomeDictionary.registerBiomeType(pinkforest, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.LUSH, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.WET);

			//pinkriver = new BiomePinkRiver();
		}

		this.finishTiming();
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		this.startTiming(LoadPhase.POSTLOAD);
		this.finishTiming();
	}

	@SubscribeEvent
	public void disallowDespawn(AllowDespawn d) {
		EntityLivingBase e = d.entityLiving;
		if (e instanceof EntitySpiderBase)
			d.setResult(Result.DENY);
	}

	@Override
	public String getDisplayName() {
		return "Critter Pet";
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return DragonAPICore.getReikaForumPage();
	}

	@Override
	public String getWiki() {
		return null;
	}

	@Override
	public String getUpdateCheckURL() {
		return CommandableUpdateChecker.reikaURL;
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}

	@Override
	public File getConfigFolder() {
		return config.getConfigFolder();
	}

	@SubscribeEvent
	public void meltSnowIce(BlockTickEvent evt) {
		if (!evt.world.isRaining() && evt.world.isDaytime() && evt.getBiome() instanceof BiomePinkForest && evt.world.canBlockSeeTheSky(evt.xCoord, evt.yCoord+1, evt.zCoord)) {
			if (evt.block == Blocks.snow_layer)
				evt.world.setBlockToAir(evt.xCoord, evt.yCoord, evt.zCoord);
			else if (evt.block == Blocks.ice)
				evt.world.setBlock(evt.xCoord, evt.yCoord, evt.zCoord, Blocks.water);
		}
	}

	@SubscribeEvent
	public void preventNewIce(IceFreezeEvent evt) {
		if (evt.getBiome() instanceof BiomePinkForest) {
			evt.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void preventSnowGen(SnowOrIceOnGenEvent evt) {
		if (evt.getBiome() instanceof BiomePinkForest) {
			evt.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void shapePinkForest(ChunkProviderEvent.ReplaceBiomeBlocks evt) {
		if (evt.world != null && evt.blockArray != null) {
			pinkforest.shapeTerrain(evt.world, evt.chunkX, evt.chunkZ, evt.blockArray, evt.metaArray);
		}
	}

	@SubscribeEvent
	public void retextureGrass(GrassIconEvent evt) {
		if (evt.getBiome() instanceof BiomePinkForest) {
			evt.icon = evt.isTop ? biomeGrassIcon : biomeGrassIconSide;
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) {
		if (event.map.getTextureType() == 0) {
			biomeGrassIcon = event.map.registerIcon("critterpet:grass_top");
			biomeGrassIconSide = event.map.registerIcon("critterpet:grass_side_overlay");
		}
	}

	@SubscribeEvent
	public void changePinkRivers(GenLayerRiverEvent evt) {
		if (evt.originalBiomeID == pinkforest.biomeID) {
			//evt.riverBiomeID = pinkriver.biomeID;
			evt.setResult(Result.DENY);
		}
	}

}
