package Reika.CritterPet.Biome;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;


public class PinkForestPersistentData extends WorldSavedData {

	private static final String IDENTIFIER = "PinkForestData";

	public PinkForestPersistentData() {
		super(IDENTIFIER);
	}

	public PinkForestPersistentData(String s) {
		super(s);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		BiomewideFeatureGenerator.instance.readFromNBT(NBT.getCompoundTag("biomewideFeatures"));
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		NBTTagCompound tag = new NBTTagCompound();
		BiomewideFeatureGenerator.instance.writeToNBT(tag);
		NBT.setTag("biomewideFeatures", tag);
	}

	public static PinkForestPersistentData initNetworkData(World world) {
		PinkForestPersistentData data = (PinkForestPersistentData)world.loadItemData(PinkForestPersistentData.class, IDENTIFIER);
		if (data == null) {
			data = new PinkForestPersistentData();
			world.setItemData(IDENTIFIER, data);
		}
		return data;
	}

}
