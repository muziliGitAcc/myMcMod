package my.computer.mod.myBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class myComputerBlock extends Block {

    public myComputerBlock()
    {
        super(Properties.create(Material.ROCK).hardnessAndResistance(5));
    }
}

