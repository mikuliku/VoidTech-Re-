package com.voidtech.registry;

import com.voidtech.VoidTech;
import com.voidtech.menu.VoidEnergyInterfaceMenu;
import com.voidtech.menu.VoidFluidInterfaceMenu;
import com.voidtech.menu.VoidItemInterfaceMenu;
import com.voidtech.menu.VoidMiningMachineMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, VoidTech.MOD_ID);

    public static final RegistryObject<MenuType<VoidMiningMachineMenu>> VOID_MINING_MACHINE =
            MENUS.register("void_mining_machine", () -> IForgeMenuType.create(
                    (windowId, inventory, data) -> VoidMiningMachineMenu.fromNetwork(windowId, inventory, data)));

    public static final RegistryObject<MenuType<VoidEnergyInterfaceMenu>> VOID_ENERGY_INTERFACE =
            MENUS.register("void_energy_interface", () -> IForgeMenuType.create(
                    (windowId, inventory, data) -> VoidEnergyInterfaceMenu.fromNetwork(windowId, inventory, data)));

    public static final RegistryObject<MenuType<VoidItemInterfaceMenu>> VOID_ITEM_INTERFACE =
            MENUS.register("void_item_interface", () -> IForgeMenuType.create(
                    (windowId, inventory, data) -> VoidItemInterfaceMenu.fromNetwork(windowId, inventory, data)));

    public static final RegistryObject<MenuType<VoidFluidInterfaceMenu>> VOID_FLUID_INTERFACE =
            MENUS.register("void_fluid_interface", () -> IForgeMenuType.create(
                    (windowId, inventory, data) -> VoidFluidInterfaceMenu.fromNetwork(windowId, inventory, data)));

    private ModMenus() {}
}
