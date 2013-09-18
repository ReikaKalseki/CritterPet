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

import java.net.MalformedURLException;
import java.net.URL;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod( modid = "SpiderPet", name="Spider Pet", version="beta", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="after:DragonAPI")
@NetworkMod(clientSideRequired = true, serverSideRequired = true/*,
clientPacketHandlerSpec = @SidedPacketHandler(channels = { "SpiderPetData" }, packetHandler = ClientPackets.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = { "SpiderPetData" }, packetHandler = ServerPackets.class)*/)
public class SpiderPet extends DragonAPIMod {

	//@Instance
	public static SpiderPet instance = new SpiderPet();

	@Override
	public void preload(FMLPreInitializationEvent evt) {

	}

	@Override
	public void load(FMLInitializationEvent event) {

	}

	@Override
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
		try {
			return new URL("http://www.minecraftforum.net/topic/1969694-");
		}
		catch (MalformedURLException e) {
			throw new RegistrationException(instance, "The mod provided a malformed URL for its documentation site!");
		}
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

}
