package my.computer.mod.myEntity;

import my.computer.mod.Generic.propertiesGeneric;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;

public final class MyEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, propertiesGeneric.modId);

    ///////////////////////////////////////////////////////////////////

    public static final RegistryObject<EntityType<myBatteryEntity>> ROBOT = register("robot", myBatteryEntity::new, EntityClassification.MISC, b -> b.size(14f / 16f, 14f / 16f).immuneToFire().disableSummoning());

    ///////////////////////////////////////////////////////////////////

    public static void initialize() {
    }

    ///////////////////////////////////////////////////////////////////

    private static <T extends Entity> RegistryObject<EntityType<T>> register(final String name, final EntityType.IFactory<T> factory, final EntityClassification classification, final Function<EntityType.Builder<T>, EntityType.Builder<T>> customizer) {
        return ENTITIES.register(name, () -> customizer.apply(EntityType.Builder.create(factory, classification)).build(name));
    }
}
