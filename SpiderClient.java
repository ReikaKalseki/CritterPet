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

import Reika.SpiderPet.Registry.SpiderType;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class SpiderClient extends SpiderCommon {

	public static final SpiderRenderer spider = new SpiderRenderer();

	@Override
	public void registerSounds() {
		//RotarySounds.addSounds();
		//MinecraftForge.EVENT_BUS.register(new SoundLoader(RotaryCraft.instance, SoundRegistry.soundList));
	}

	@Override
	public void registerRenderers() {
		for (int i = 0; i < SpiderType.spiderList.length; i++) {
			SpiderType s = SpiderType.spiderList[i];
			RenderingRegistry.registerEntityRenderingHandler(s.entityClass, spider);
		}
	}

	// Override any other methods that need to be handled differently client side.

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
