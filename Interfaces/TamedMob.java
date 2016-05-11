/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet.Interfaces;

import net.minecraft.entity.player.EntityPlayer;
import Reika.CritterPet.Registry.CritterType;

public interface TamedMob {

	public String getMobOwner();

	public void setOwner(EntityPlayer ep);

	public boolean hasOwner();

	public void spawnEffects();

	public CritterType getBaseCritter();

	public boolean isVanillaCritter();

	public boolean isModCritter();

	public int getCritterMaxHealth();

}
