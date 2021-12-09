/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet.Interfaces;

import net.minecraft.entity.player.EntityPlayer;

import Reika.CritterPet.API.TamedCritter;
import Reika.CritterPet.Registry.CritterType;

public interface TamedMob extends TamedCritter {

	public void setOwner(EntityPlayer ep);

	public boolean hasOwner();

	public void spawnEffects();

	public CritterType getBaseCritter();

}
