package Reika.CritterPet.Biome;

import net.minecraft.block.BlockOldLeaf;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPinkLeaves extends BlockOldLeaf {

	make not biome tint, but Y tint, with giant leaves using a different y bounds

	public static enum LeafTypes {
		TREE,
		GIANTTREE,
		BUSH1,
		BUSH2,
		BUSH3;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("critterpet:pink-tree-leaf");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int s, int meta) {
		return blockIcon;
	}

}
