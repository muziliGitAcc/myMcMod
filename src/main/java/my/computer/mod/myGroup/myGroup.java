package my.computer.mod.myGroup;

import my.computer.mod.myItem.myItemRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class myGroup extends ItemGroup {
    public myGroup() {
        super("myComputer");
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(myItemRegistry.myComputer.get());
    }
}

