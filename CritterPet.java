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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;

import Reika.CritterPet.Entities.Base.EntitySpiderBase;
import Reika.CritterPet.Registry.CritterOptions;
import Reika.CritterPet.Registry.CritterType;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.DragonAPIMod.LoadProfiler.LoadPhase;
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

@Mod( modid = "CritterPet", name="Critter Pet", version = "v@MAJOR_VERSION@@MINOR_VERSION@", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI")

public class CritterPet extends DragonAPIMod {

	@Instance("CritterPet")
	public static CritterPet instance = new CritterPet();

	public static final ControlledConfig config = new ControlledConfig(instance, CritterOptions.optionList, null);

	public static ItemCritterEgg egg;
	public static ItemTaming tool;

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

}
