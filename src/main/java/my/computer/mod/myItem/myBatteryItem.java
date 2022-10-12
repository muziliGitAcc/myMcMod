package my.computer.mod.myItem;

import my.computer.mod.myEntity.MyEntities;
import my.computer.mod.myEntity.myBatteryEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import static my.computer.mod.Generic.propertiesGeneric.itemGroup;

public class myBatteryItem extends Item {
    public myBatteryItem() {
        super(new Properties().group(itemGroup));
    }

    @Override
    public ActionResultType onItemUse(final ItemUseContext context) {
        final World world = context.getWorld();
        final BlockPos pos = context.getPos();

        final Vector3d position;
        if (world.getBlockState(pos).isReplaceable(new BlockItemUseContext(context))) {
            position = Vector3d.copyCentered(pos);
        } else {
            position = Vector3d.copyCentered(pos.offset(context.getFace()));
        }

        final myBatteryEntity robot = MyEntities.ROBOT.get().create(context.getWorld());
//        robot.setPositionAndRotation(position.x, position.y, position.z,
//                Direction.fromAngle(context.getPlacementYaw()).getOpposite().getHorizontalAngle(), 0);
        robot.func_242281_f(position.x, position.y, position.z);

        if (!world.isRemote()) {

            world.addEntity(robot);

        }

        context.getPlayer().addStat(Stats.ITEM_USED.get(this));

        return ActionResultType.func_233537_a_(world.isRemote());
    }
}
