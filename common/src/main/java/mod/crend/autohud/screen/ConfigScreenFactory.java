package mod.crend.autohud.screen;

import eu.midnightdust.lib.config.MidnightConfig;
import mod.crend.autohud.AutoHud;
import net.minecraft.client.gui.screen.Screen;

public class ConfigScreenFactory {
	public static Screen makeScreen(Screen parent) {
		return MidnightConfig.getScreen(parent, AutoHud.MOD_ID);
	}

}
