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

import net.minecraft.world.World;

public class CritterCommon {

	public static int bambooRender;

	/**
	 * Client side only register stuff...
	 */
	public void registerRenderers()
	{
		//unused server side. -- see ClientProxy for implementation
	}

	public void addArmorRenders() {}

	public World getClientWorld() {
		return null;
	}

	public void registerRenderInformation() {
		// TODO Auto-generated method stub

	}

	public void registerSounds() {
		// TODO Auto-generated method stub
	}

}
