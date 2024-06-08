package com.cak.watering;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.jarjar.nio.util.Lazy;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class WateringControls {
    
    public static final Lazy<KeyMapping> TOGGLE_MODE = Lazy.of(() -> new KeyMapping(
        "key." + WateringOverlay.MODID + ".toggle_mode",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_B,
        "key.categories." + WateringOverlay.MODID
    ));
    
    public static void tickControls() {
        
        if (Minecraft.getInstance().player == null) return;
        
        while (TOGGLE_MODE.get().consumeClick()) {
            WateringOverlay.DisplayOptions.SELECTOR_INDEX = (WateringOverlay.DisplayOptions.SELECTOR_INDEX + 1) % OverlaySelector.values().length;
            OverlaySelector newSelector = OverlaySelector.values()[WateringOverlay.DisplayOptions.SELECTOR_INDEX];
            WateringOverlay.DisplayOptions.SELECTOR = newSelector;
            
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Changed selector to ").withStyle(ChatFormatting.GRAY).append(Component.literal("[").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)).append(Component.literal(newSelector.name()).withStyle(newSelector.getChatFormatting(), ChatFormatting.BOLD)).append(Component.literal("]").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)), true);
        }
    }
    
    @EventBusSubscriber(modid = WateringOverlay.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ControlRegistrationEvents {
        
        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(TOGGLE_MODE.get());
        }
        
    }
    
}
