/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet.Registry;

import Reika.CritterPet.CritterPet;
import Reika.DragonAPI.Interfaces.Configuration.BooleanConfig;
import Reika.DragonAPI.Interfaces.Configuration.IntegerConfig;

public enum CritterOptions implements BooleanConfig, IntegerConfig {

	BIOMEID("Pink Forest Biome ID", 144);

	private String label;
	private boolean defaultState;
	private int defaultValue;
	private Class type;

	public static final CritterOptions[] optionList = CritterOptions.values();

	private CritterOptions(String l, boolean d) {
		label = l;
		defaultState = d;
		type = boolean.class;
	}

	private CritterOptions(String l, int d) {
		label = l;
		defaultValue = d;
		type = int.class;
	}

	public boolean isBoolean() {
		return type == boolean.class;
	}

	public boolean isNumeric() {
		return type == int.class;
	}

	public Class getPropertyType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public boolean getState() {
		return (Boolean)CritterPet.config.getControl(this.ordinal());
	}

	public int getValue() {
		return (Integer)CritterPet.config.getControl(this.ordinal());
	}

	public boolean isDummiedOut() {
		return type == null;
	}

	@Override
	public boolean getDefaultState() {
		return defaultState;
	}

	@Override
	public int getDefaultValue() {
		return defaultValue;
	}

	@Override
	public boolean isEnforcingDefaults() {
		return false;
	}

	@Override
	public boolean shouldLoad() {
		return true;
	}
	/*
	@Override
	public boolean isValueValid(Property p) {
		switch(this) {
			case BIOMEID:
				return p.getInt() >= 40 && p.getInt() <= 255;
			default:
				return true;
		}
	}

	@Override
	public String getBoundsAsString() {
		switch(this) {
			case BIOMEID:
				return "(40-255)";
			default:
				return "";
		}
	}
	 */
}
