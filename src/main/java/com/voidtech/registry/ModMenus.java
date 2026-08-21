package com.voidtech.registry;

import com.voidtech.VoidTech;
import com.voidtech.menu.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.*;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, VoidTech.MOD_ID);

    public static final RegistryObject<MenuType<VoidMiningMachineMenu>> VOID_MINING_MACHINE =
            MENUS.register("void_mining_machine", () -> IForgeMenuType.create(
                    (id, inv, data) -> VoidMiningMachineMenu.fromNetwork(id, inv, data)));

    public static final RegistryObject<MenuType<VoidFabricatorMenu>> VOID_FABRICATOR =
            MENUS.register("void_fabricator", () -> IForgeMenuType.create(
                    (id, inv, data) -> VoidFabricatorMenu.fromNetwork(id, inv, data)));

    public static final RegistryObject<MenuType<VoidEnergyInterfaceMenu>> VOID_ENERGY_INTERFACE =
            MENUS.register("void_energy_interface", () -> IForgeMenuType.create(
                    (id, inv, data) -> VoidEnergyInterfaceMenu.fromNetwork(id, inv, data)));

    public static final RegistryObject<MenuType<VoidItemInterfaceMenu>> VOID_ITEM_INTERFACE =
            MENUS.register("void_item_interface", () -> IForgeMenuType.create(
                    (id, inv, data) -> VoidItemInterfaceMenu.fromNetwork(id, inv, data)));

    public static final RegistryObject<MenuType<VoidFluidInterfaceMenu>> VOID_FLUID_INTERFACE =
            MENUS.register("void_fluid_interface", () -> IForgeMenuType.create(
                    (id, inv, data) -> VoidFluidInterfaceMenu.fromNetwork(id, inv, data)));

    public static final RegistryObject<MenuType<VoidFluidMachineMenu>> VOID_FLUID_MACHINE =
            MENUS.register("void_fluid_machine", () -> IForgeMenuType.create(
                    (id, inv, data) -> VoidFluidMachineMenu.fromNetwork(id, inv, data)));

    private ModMenus() {}
}
