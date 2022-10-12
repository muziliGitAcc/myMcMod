package my.computer.mod.myEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class myBatteryEntity extends Entity {

    private static final DataParameter<Integer> COUNTER = EntityDataManager.createKey(myBatteryEntity.class, DataSerializers.VARINT);
    public myBatteryEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void registerData() {
        this.dataManager.register(COUNTER, 0);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        this.dataManager.set(COUNTER, compound.getInt("counter"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putInt("counter", this.dataManager.get(COUNTER));
    }
    //猜测是是否与玩家有碰撞 不设置的话玩家能够直接穿过去
    @Override
    public boolean func_241845_aY() {
        return true;
    }
    //是否有碰撞
    @Override
    public boolean canBeCollidedWith(){
        return true;
    }

    //碰撞时是否可以被推走
    @Override
    public boolean canBePushed(){
        return true;
    }

    @Override
    public void tick() {
        if (world.isRemote) {
        }
        if (!world.isRemote) {
            this.dataManager.set(COUNTER, this.dataManager.get(COUNTER) + 1);
        }
        super.tick();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
