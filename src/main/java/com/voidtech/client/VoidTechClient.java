package com.voidtech.client;

import com.voidtech.client.screen.VoidEnergyInterfaceScreen;
import com.voidtech.client.screen.VoidFluidInterfaceScreen;
import com.voidtech.client.screen.VoidItemInterfaceScreen;
import com.voidtech.client.screen.VoidMiningMachineScreen;
import com.voidtech.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = "voidtech",
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class VoidTechClient {

    private VoidTechClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(
                    ModMenus.VOID_MINING_MACHINE.get(),
                    VoidMiningMachineScreen::new
            );

            MenuScreens.register(
                    ModMenus.VOID_ENERGY_INTERFACE.get(),
                    VoidEnergyInterfaceScreen::new
            );

            MenuScreens.register(
                    ModMenus.VOID_ITEM_INTERFACE.get(),
                    VoidItemInterfaceScreen::new
            );

            MenuScreens.register(
                    ModMenus.VOID_FLUID_INTERFACE.get(),
                    VoidFluidInterfaceScreen::new
            );
        });
    }
}
