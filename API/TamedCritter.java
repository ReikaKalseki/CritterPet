/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet.API;

import Reika.DragonAPI.Interfaces.Entity.TameHostile;

public interface TamedCritter extends TameHostile {

	public String getMobOwner();

	public boolean isVanillaCritter();

	public boolean isModCritter();

	public int getCritterMaxHealth();

}
