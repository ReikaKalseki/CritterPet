package Reika.CritterPet.Registry;


public enum CritterClass {

	SPIDER(),
	BEETLE(),
	SLIME(),
	WOLF(),
	FLYING(),
	BUG();

	private int index;

	public int getTextureRow() {
		return 1+this.ordinal();
	}

	public int getNextIndex() {
		int ret = index;
		index++;
		return ret;
	}

}
