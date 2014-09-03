/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet.Registry;

import net.minecraftforge.common.config.Configuration;
import Reika.CritterPet.CritterPet;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.ConfigList;

public enum CritterOptions implements ConfigList {

	NULL("Null", false);

	private String label;
	private boolean defaultState;
	private int defaultValue;
	private float defaultFloat;
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

	public boolean isDecimal() {
		return type == float.class;
	}

	public float setDecimal(Configuration config) {
		if (!this.isDecimal())
			throw new RegistrationException(CritterPet.instance, "Config Property \""+this.getLabel()+"\" is not decimal!");
		return (float)config.get("Control Setup", this.getLabel(), defaultFloat).getDouble(defaultFloat);
	}

	public float getFloat() {
		return (Float)CritterPet.config.getControl(this.ordinal());
	}

	public Class getPropertyType() {
		return type;
	}

	public int setValue(Configuration config) {
		if (!this.isNumeric())
			throw new RegistrationException(CritterPet.instance, "Config Property \""+this.getLabel()+"\" is not numerical!");
		return config.get("Control Setup", this.getLabel(), defaultValue).getInt();
	}

	public String getLabel() {
		return label;
	}

	public boolean setState(Configuration config) {
		if (!this.isBoolean())
			throw new RegistrationException(CritterPet.instance, "Config Property \""+this.getLabel()+"\" is not boolean!");
		return config.get("Control Setup", this.getLabel(), defaultState).getBoolean(defaultState);
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
	public float getDefaultFloat() {
		return defaultFloat;
	}

	@Override
	public boolean isEnforcingDefaults() {
		return false;
	}

	@Override
	public boolean shouldLoad() {
		return true;
	}

}
