package my.computer.mod.myItem;

import net.minecraft.item.Item;

import static my.computer.mod.Generic.propertiesGeneric.itemGroup;

public class myComputerItem extends Item {
    /**
     * new Properties().group(ItemGroup.MATERIALS)，
     * 这个Properties规定了物品的一些属性，比如：是不是食物，或者这个物品在创造模式的哪一个物品栏。
     *
     * 在这里我们创建了一个Properties并且调用了group方法然后传入了ItemGroup.MATERIALS，
     * 这样做是将物品添加进，原版「杂项」创造模式物品栏里。当然你也可以不调用 group方法，
     * 如果这样就只能通过/give命令才能获取到物品了。
     * */
    public myComputerItem() {
        super(new Properties().group(itemGroup));
    }
}
