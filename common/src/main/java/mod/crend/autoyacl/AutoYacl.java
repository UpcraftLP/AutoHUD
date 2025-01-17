package mod.crend.autoyacl;

import dev.isxander.yacl.api.*;
import dev.isxander.yacl.config.ConfigEntry;
import dev.isxander.yacl.gui.controllers.ColorController;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.cycling.CyclingListController;
import dev.isxander.yacl.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl.gui.controllers.slider.LongSliderController;
import dev.isxander.yacl.gui.controllers.string.StringController;
import dev.isxander.yacl.gui.controllers.string.number.DoubleFieldController;
import dev.isxander.yacl.gui.controllers.string.number.FloatFieldController;
import dev.isxander.yacl.gui.controllers.string.number.IntegerFieldController;
import dev.isxander.yacl.gui.controllers.string.number.LongFieldController;
import mod.crend.autoyacl.annotation.*;
import net.minecraft.text.Text;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Automagic config UI generation from a config file based on annotations.
 *
 * @see AutoYacl#parse
 */
public class AutoYacl <T> {

	/**
	 * A wrapper can add fields to the current option builder, but no nested groups.
	 */
	protected static class Wrapper {
		protected final OptionAddable builder;
		protected final Object bDefaults;
		protected final Object bParent;
		protected final Map<String, OptionGroup.Builder> groups;
		protected final String modId;
		
		protected Wrapper(String modId, OptionAddable builder, Object bDefaults, Object bParent, Map<String, OptionGroup.Builder> groups) {
			this.builder = builder;
			this.bDefaults = bDefaults;
			this.bParent = bParent;
			this.groups = groups;
			this.modId = modId;
		}
		
		OptionAddable getContainingBuilder(Field field) {
			Category category = field.getAnnotation(Category.class);
			if (category != null && !category.group().isBlank()) {
				if (!groups.containsKey(category.group())) {
					groups.put(category.group(), OptionGroup.createBuilder().name(Text.translatable(modId + ".group." + category.group())));
				}
				return groups.get(category.group());
			}
			return builder;
		}

		protected static String getTranslationKey(String modId, String key, Field field, boolean group) {
			Translation translationKey = field.getAnnotation(Translation.class);
			return (translationKey == null ? modId + (group ? ".group." : ".option.") + key : translationKey.key());
		}
		protected static void setCommonAttributes(String modId, Option.Builder<?> optionBuilder, String key, Field field) {
			String translationKey = getTranslationKey(modId, key, field, false);
			optionBuilder.name(Text.translatable(translationKey));
			if (field.isAnnotationPresent(Tooltip.class)) {
				optionBuilder.tooltip(Text.translatable(translationKey + ".@Tooltip"));
			}
			OnSave onSave = field.getAnnotation(OnSave.class);
			if (onSave != null) {
				if (onSave.gameRestart()) optionBuilder.flag(OptionFlag.GAME_RESTART);
				if (onSave.reloadChunks()) optionBuilder.flag(OptionFlag.RELOAD_CHUNKS);
				if (onSave.worldRenderUpdate()) optionBuilder.flag(OptionFlag.WORLD_RENDER_UPDATE);
				if (onSave.assetReload()) optionBuilder.flag(OptionFlag.ASSET_RELOAD);
			}
		}
		protected void setCommonAttributes(OptionGroup.Builder optionGroupBuilder, String key, Field field) {
			String translationKey = getTranslationKey(modId, key, field, true);
			optionGroupBuilder.name(Text.translatable(translationKey));
			if (field.isAnnotationPresent(Tooltip.class)) {
				optionGroupBuilder.tooltip(Text.translatable(translationKey + ".@Tooltip"));
			}
		}
		
		protected void registerObject(OptionAddable containingBuilder, String key, Field field) {
			// register object transitively
			try {
				Wrapper transitiveWrapper = new Wrapper(modId, containingBuilder, field.get(bDefaults), field.get(bParent), groups);
				for (Field memberField : field.getType().getFields()) {
					transitiveWrapper.register(key + "." + memberField.getName(), memberField);
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		protected void register(String key, Field field) {
			OptionAddable containingBuilder = getContainingBuilder(field);
			
			Label label = field.getAnnotation(Label.class);
			if (label != null) {
				containingBuilder.option(LabelOption.create(Text.translatable(label.key())));
			}

			Option.Builder<?> optionBuilder = fromType(field, bDefaults, bParent);
			if (optionBuilder != null) {
				setCommonAttributes(modId, optionBuilder, key, field);
				containingBuilder.option(optionBuilder.build());
			} else {
				registerObject(containingBuilder, key, field);
			}
		}

		@SuppressWarnings("unchecked")
		private static <T> Binding<T> makeBinding(Field field, Object defaults, Object parent) {
			try {
				return Binding.generic((T) (field.get(defaults)),
					() -> {
						try {
							return (T) (field.get(parent));
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}, val -> {
						try {
							field.set(parent, val);
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					});
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		public static Option.Builder<?> createOptionBuilder(String modId, String key, Field field, Object defaults, Object parent) {
			assert(field.getDeclaringClass().isInstance(defaults));
			assert(field.getDeclaringClass().isInstance(parent));
			Option.Builder<?> optionBuilder = fromType(field, defaults, parent);
			if (optionBuilder != null) {
				setCommonAttributes(modId, optionBuilder, key, field);
			}
			return optionBuilder;
		}

		@SuppressWarnings("unchecked")
		private static Option.Builder<?> fromType(Field field, Object defaults, Object parent) {
			Class<?> type = field.getType();

			if (type.equals(boolean.class)) {

				return Option.createBuilder(boolean.class)
						.binding(makeBinding(field, defaults, parent))
						.controller(TickBoxController::new);

			} else if (type.equals(int.class)) {

				var builder = Option.createBuilder(int.class)
						.binding(makeBinding(field, defaults, parent));
				IntegerRange range = field.getAnnotation(IntegerRange.class);
				if (range != null) {
					builder.controller(opt -> new IntegerSliderController(opt, range.min(), range.max(), range.interval()));
				} else {
					builder.controller(IntegerFieldController::new);
				}
				return builder;

			} else if (type.equals(long.class)) {

				var builder = Option.createBuilder(long.class)
						.binding(makeBinding(field, defaults, parent));
				LongRange range = field.getAnnotation(LongRange.class);
				if (range != null) {
					builder.controller(opt -> new LongSliderController(opt, range.min(), range.max(), range.interval()));
				} else {
					builder.controller(LongFieldController::new);
				}
				return builder;

			} else if (type.equals(float.class)) {

				var builder = Option.createBuilder(float.class)
						.binding(makeBinding(field, defaults, parent));
				FloatRange range = field.getAnnotation(FloatRange.class);
				if (range != null) {
					builder.controller(opt -> new FloatSliderController(opt, range.min(), range.max(), range.interval()));
				} else {
					builder.controller(FloatFieldController::new);
				}
				return builder;

			} else if (type.equals(double.class)) {

				var builder = Option.createBuilder(double.class)
						.binding(makeBinding(field, defaults, parent));
				DoubleRange range = field.getAnnotation(DoubleRange.class);
				if (range != null) {
					builder.controller(opt -> new DoubleSliderController(opt, range.min(), range.max(), range.interval()));
				} else {
					builder.controller(DoubleFieldController::new);
				}
				return builder;

			} else if (type.equals(String.class)) {

				return Option.createBuilder(String.class)
						.binding(makeBinding(field, defaults, parent))
						.controller(StringController::new);

			} else if (type.isEnum()) {

				var valid_entries = Arrays.stream(type.getEnumConstants()).toList();
				return Option.createBuilder(type)
						.binding(makeBinding(field, defaults, parent))
						.controller(opt -> new CyclingListController(opt, valid_entries, YaclHelper.getEnumFormatter()));

			} else if (type.equals(Color.class)) {

				return Option.createBuilder(Color.class)
						.binding(makeBinding(field, defaults, parent))
						.controller(ColorController::new);

			}
			return null;
		}

	}

	/**
	 * A category wrapper can, in addition to fields, also add objects as and fields to groups.
	 * Use @Category(group=) and @TransitiveObject to fine tune groupings.
	 */
	public static class CategoryWrapper extends Wrapper {
		public CategoryWrapper(String modId, OptionAddable builder, Object bDefaults, Object bParent) {
			super(modId, builder, bDefaults, bParent, new HashMap<>());
		}
		private CategoryWrapper(String modId, OptionAddable builder, Object bDefaults, Object bParent, Map<String, OptionGroup.Builder> groups) {
			super(modId, builder, bDefaults, bParent, groups);
		}

		protected void registerObjectTransitively(OptionAddable containingBuilder, String key, Field field) {
			// register object transitively
			try {
				Wrapper transitiveWrapper = new CategoryWrapper(modId, containingBuilder, field.get(bDefaults), field.get(bParent), groups);
				for (Field memberField : field.getType().getFields()) {
					transitiveWrapper.register(key + "." + memberField.getName(), memberField);
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void registerObject(OptionAddable containingBuilder, String key, Field field) {
			if (field.isAnnotationPresent(TransitiveObject.class)) {
				registerObjectTransitively(containingBuilder, key, field);
				return;
			}
			if (containingBuilder != builder) {
				super.registerObject(containingBuilder, key, field);
				return;
			}

			if (!groups.containsKey(key)) {
				groups.put(key, OptionGroup.createBuilder());
			}
			var groupBuilder = groups.get(key);
			setCommonAttributes(groupBuilder, key, field);
			try {
				Wrapper groupWrapper = new Wrapper(modId, groupBuilder, field.get(bDefaults), field.get(bParent), groups);
				for (Field memberField : field.getType().getFields()) {
					groupWrapper.register(key + "." + memberField.getName(), memberField);
				}

			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		public ConfigCategory build() {
			ConfigCategory.Builder categoryBuilder = (ConfigCategory.Builder) builder;
			for (var group : groups.values()) {
				categoryBuilder.group(group.build());
			}
			return categoryBuilder.build();
		}
	}

	private static <T> CategoryWrapper wrapBuilder(String modId, String categoryName, T defaults, T config) {
		return new CategoryWrapper(
				modId,
				ConfigCategory.createBuilder().name(Text.translatable(modId + ".category." + categoryName)),
				defaults,
				config);
	}

	/**
	 * Parses the given config class's annotations and generates a YACL config UI.
	 *
	 * <p>You can use it in your screen factory as follows:
	 * <pre>
	 * public class ConfigScreenFactory {
	 *     public static Screen makeScreen(Screen parent) {
	 *         return YetAnotherConfigLib.create(Config.CONFIG_STORE.withYacl().instance,
	 *             (defaults, config, builder) -> AutoYacl.parse(Config.class, defaults, config, builder)
	 *         ).generateScreen(parent);
	 *     }
	 * }
	 * </pre>
	 *
	 * @param configClass the class referring to T
	 * @param defaults default config, to revert options to default from
	 * @param config current config
	 * @param builder YACL screen builder. Should be empty at first and will return buildable.
	 * @return The builder after every field has been added to it.
	 * @param <T> config class
	 */
	public static <T> YetAnotherConfigLib.Builder parse(Class<?> configClass, T defaults, T config, YetAnotherConfigLib.Builder builder) {
		AutoYaclConfig ayc = configClass.getAnnotation(AutoYaclConfig.class);
		String modId = ayc.modid();
		Text modTitle = Text.translatable(ayc.translationKey());
		CategoryWrapper categoryMain = wrapBuilder(modId, "general", defaults, config);
		Map<String, CategoryWrapper> categories = new HashMap<>();
		for (Field field : configClass.getFields()) {
			if (field.isAnnotationPresent(ConfigEntry.class)) {
				Category category = field.getAnnotation(Category.class);
				if (category == null) {
					categoryMain.register(field.getName(), field);
				} else {
					if (!categories.containsKey(category.name())) {
						categories.put(category.name(), wrapBuilder(modId, category.name(), defaults, config));
					}
					categories.get(category.name()).register(field.getName(), field);
				}
			}
		}
		builder.category(categoryMain.build());
		for (var entry : categories.values()) {
			builder.category(entry.build());
		}
		return builder.title(modTitle);
	}

	private final Class<T> configClass;
	private final T defaults;
	private final T config;
	private final String modId;

	/**
	 * Instance this class to get a dynamic builder from which you may create individual options.
	 *
	 * @param configClass the class referring to T
	 * @param defaults default config, to revert options to default from
	 * @param config current config
	 */
	public AutoYacl(Class<T> configClass, T defaults, T config) {
		this.configClass = configClass;
		this.defaults = defaults;
		this.config = config;
		AutoYaclConfig ayc = configClass.getAnnotation(AutoYaclConfig.class);
		this.modId = ayc.modid();
	}

	/**
	 * Only create an option from the field with the given key. All annotations except for @Category and @Label will be
	 * used to configure the option builder.
	 *
	 * @param key the name of the field in the config class
	 * @return an option builder that can be built or further configured
	 * @param <S> the type of the field
	 */
	@SuppressWarnings("unchecked")
	public <S> Option.Builder<S> makeOption(String key) {
		try {
			return (Option.Builder<S>) Wrapper.createOptionBuilder(modId, key, configClass.getField(key), defaults, config);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}
