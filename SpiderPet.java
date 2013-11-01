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

import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.ModLogger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;

@Mod( modid = "SpiderPet", name="Spider Pet", version="beta", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="after:DragonAPI")
@NetworkMod(clientSideRequired = true, serverSideRequired = true/*,
clientPacketHandlerSpec = @SidedPacketHandler(channels = { "SpiderPetData" }, packetHandler = ClientPackets.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = { "SpiderPetData" }, packetHandler = ServerPackets.class)*/)
public class SpiderPet extends DragonAPIMod {

	@Instance("SpiderPet")
	public static SpiderPet instance = new SpiderPet();

	private ModLogger logger;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		logger = new ModLogger(instance, true, false, false);
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		for (int i = 0; i < SpiderType.spiderList.length; i++) {
			SpiderType type = SpiderType.spiderList[i];
			int id = EntityRegistry.findGlobalUniqueEntityId();
			EntityRegistry.registerGlobalEntityID(type.entityClass, type.getName(), id);
			EntityRegistry.registerModEntity(type.entityClass, type.getName(), id, instance, 32, 20, true);
			EntityEggInfo egg = new EntityEggInfo(id, type.eggColor1, type.eggColor2);
			EntityList.entityEggs.put(id, egg);
			logger.log("Loading Spider Type "+type.getName());
		}
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {

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
