package Reika.CritterPet.Biome;

import java.util.List;
import java.util.Locale;

import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockPinkGrass extends BlockTallGrass {

	public static enum GrassTypes {
		PEACH_FRINGE,
		TINY_PINK_LUMPS,
		RED_STRANDS_1,
		RED_STRANDS_2,
		;

		private static final GrassTypes[] list = values();

		private IIcon icon;

		public float getHeight() {
			switch(this) {
				case PEACH_FRINGE:
					return 1;
				case RED_STRANDS_1:
				case RED_STRANDS_2:
					return 0.375F;
				case TINY_PINK_LUMPS:
					return 0.125F;
				default:
					return 1;
			}
		}

		public boolean isSelfColor() {
			switch(this) {
				case PEACH_FRINGE:
				case RED_STRANDS_1:
				case RED_STRANDS_2:
					return true;
				default:
					return false;
			}
		}
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		GrassTypes gr = GrassTypes.list[world.getBlockMetadata(x, y, z)];
		return gr.isSelfColor() ? 0xffffff : super.colorMultiplier(world, x, y, z);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("critterpet:birch_leaves");
		for (int i = 0; i < GrassTypes.list.length; i++) {
			GrassTypes gr = GrassTypes.list[i];
			gr.icon = ico.registerIcon("critterpet:grass_"+gr.name().toLowerCase(Locale.ENGLISH));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int s, int meta) {
		return GrassTypes.list[meta].icon;
	}

	@Override
	public void getSubBlocks(Item it, CreativeTabs cr, List li) {
		for (int i = 0; i < GrassTypes.list.length; i++) {
			li.add(new ItemStack(it, 1, i));
		}
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return EnumPlantType.Plains;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		GrassTypes gr = GrassTypes.list[world.getBlockMetadata(x, y, z)];
		float f = 0.4F; //from parent
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, gr.getHeight(), 0.5F + f);
	}

}
