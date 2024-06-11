package com.cak.watering;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

public class WateringControls {
    
    public static final Lazy<KeyMapping> TOGGLE_MODE = Lazy.of(() -> new KeyMapping(
            "key." + WateringOverlay.MODID + ".toggle_mode",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories." + WateringOverlay.MODID
        )
    );
    
    public static void tickControls(TickEvent.ClientTickEvent event) {
        
        if (Minecraft.getInstance().player == null) return;
        if (event.phase == TickEvent.Phase.END) { // Only call code once as the tick event is called twice every tick
            while (TOGGLE_MODE.get().consumeClick()) {
                WateringOverlay.DisplayOptions.SELECTOR_INDEX =
                    (WateringOverlay.DisplayOptions.SELECTOR_INDEX +1) % OverlaySelector.values().length;
                OverlaySelector newSelector = OverlaySelector.values()[WateringOverlay.DisplayOptions.SELECTOR_INDEX];
                WateringOverlay.DisplayOptions.SELECTOR = newSelector;
            
                Minecraft.getInstance().player
                    .displayClientMessage(
                        Component.translatable("chat.watering_overlay.option_title").withStyle(ChatFormatting.GRAY)
                            .append(Component.literal("[").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD))
                            .append(Component.literal(newSelector.name()).withStyle(newSelector.getChatFormatting(), ChatFormatting.BOLD))
                            .append(Component.literal("]").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)),
                        true);
            }
        }
    
    }
    
    @Mod.EventBusSubscriber(modid = WateringOverlay.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ControlRegistrationEvents {
        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(TOGGLE_MODE.get());
        }
    }
    
}
