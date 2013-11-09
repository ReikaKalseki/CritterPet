/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.SpiderPet;

import java.net.URL;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.ControlledConfig;
import Reika.DragonAPI.Instantiable.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.SpiderPet.Registry.SpiderOptions;
import Reika.SpiderPet.Registry.SpiderType;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod( modid = "SpiderPet", name="Spider Pet", version="beta", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="after:DragonAPI")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class SpiderPet extends DragonAPIMod {

	@Instance("SpiderPet")
	public static SpiderPet instance = new SpiderPet();

	public static final ControlledConfig config = new ControlledConfig(instance, SpiderOptions.optionList, null, null, null, 0);

	public static ItemSpiderEgg egg;
	public static ItemTaming tool;

	public static ModLogger logger;

	@SidedProxy(clientSide="Reika.SpiderPet.SpiderClient", serverSide="Reika.SpiderPet.SpiderServer")
	public static SpiderCommon proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		logger = new ModLogger(instance, SpiderOptions.LOGLOADING.getState(), SpiderOptions.DEBUGMODE.getState(), false);
		MinecraftForge.EVENT_BUS.register(this);

		ReikaRegistryHelper.setupModData(instance, evt);
		ReikaRegistryHelper.setupVersionChecking(evt);

		proxy.registerSounds();
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		egg = new ItemSpiderEgg(SpiderOptions.EGGID.getValue());
		tool = new ItemTaming(SpiderOptions.TOOLID.getValue());
		for (int i = 0; i < SpiderType.spiderList.length; i++) {
			SpiderType type = SpiderType.spiderList[i];
			int id = EntityRegistry.findGlobalUniqueEntityId();
			EntityRegistry.registerGlobalEntityID(type.entityClass, type.getName(), id);
			EntityRegistry.registerModEntity(type.entityClass, type.getName(), id, instance, 32, 20, true);
			type.initializeMapping(id);
			//EntityEggInfo egg = new EntityEggInfo(id, type.eggColor1, type.eggColor2);
			//EntityList.entityEggs.put(id, egg);
			logger.log("Loading Spider Type "+type.getName());
			GameRegistry.addShapelessRecipe(new ItemStack(tool.itemID, 1, i+1), new ItemStack(tool.itemID, 1, 0), type.tamingItem);
		}
		proxy.registerRenderers();
		LanguageRegistry.addName(tool, "Spider Taming Device");
		GameRegistry.addRecipe(new ItemStack(tool), " ID", " II", "I  ", 'I', Item.ingotIron, 'D', Item.diamond);
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {

	}

	@EventHandler
	public void disallowDespawn(AllowDespawn d) {
		EntityLivingBase e = d.entityLiving;
		if (e instanceof EntitySpiderBase)
			d.setResult(Result.DENY);
	}

	@Override
	public String getDisplayName() {
		return "Spider Pet";
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return DragonAPICore.getReikaForumPage(instance);
	}

	@Override
	public boolean hasWiki() {
		return false;
	}

	@Override
	public URL getWiki() {
		return null;
	}

	@Override
	public boolean hasVersion() {
		return false;
	}

	@Override
	public String getVersionName() {
		return null;
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}

}
