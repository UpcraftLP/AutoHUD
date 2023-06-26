package mod.crend.autohud.config;

public class ConfigHandler {

    public ConfigHandler() {
        init();
    }

    public int timeRevealed() { return Config.ticksRevealed; }
    public boolean animationMove() { return Config.animationMove; }
    public boolean animationFade() { return Config.animationFade; }
    public boolean animationNone() { return !Config.animationMove && !Config.animationFade; }
    public double animationSpeed() { return Config.animationSpeed; }
    public double animationSpeedMoveIn() { return Config.animationSpeeds.moveIn == 0 ? Config.animationSpeed : Config.animationSpeeds.moveIn; }
    public double animationSpeedMoveOut() { return Config.animationSpeeds.moveOut == 0 ? Config.animationSpeed : Config.animationSpeeds.moveOut; }
    public double animationSpeedFadeIn() { return Config.animationSpeeds.fadeIn == 0 ? Config.animationSpeed : Config.animationSpeeds.fadeIn; }
    public double animationSpeedFadeOut() { return Config.animationSpeeds.fadeOut == 0 ? Config.animationSpeed : Config.animationSpeeds.fadeOut; }
    public RevealType revealType() { return Config.revealType; }
    public boolean statusEffectTimer() { return Config.statusEffectTimer; }

    public abstract static class IComponent {
        IComponent(Config.IComponent config, Config.AdvancedComponent values, Config.DefaultValues defaultValues) {
            this.config = config;
            this.values = values;
            this.defaultValues = defaultValues;
        }
        Config.IComponent config;
        Config.AdvancedComponent values;
        Config.DefaultValues defaultValues;

        public abstract boolean active();
        public boolean onChange() { return false; }

        public ScrollDirection direction() { return values.direction; }
        public double speedMultiplier() { return (values.speedMultiplier < 0 ? defaultValues.speedMultiplier : values.speedMultiplier); }
        public int distance() { return (values.distance < 0 ? defaultValues.distance : values.distance); }
        public double maximumFade() { return (values.maximumFade < 0 ? Math.min(defaultValues.maximumFade, 1.0d) : Math.min(values.maximumFade, 1.0d)); }
    }
    public static class SimpleComponent extends IComponent {
        private SimpleComponent(Config.SimpleComponent config, Config.AdvancedComponent values, Config.DefaultValues defaultValues) {
            super(config, values, defaultValues);
        }
        boolean active = true;

        @Override
        public boolean active() {
            return active;
        }
    }

    public static class PolicyComponent extends IComponent {
        private PolicyComponent(Config.PolicyComponent config, Config.AdvancedComponent values, Config.DefaultValues defaultValues) {
            super(config, values, defaultValues);
        }

        @Override
        public boolean active() {
            return policy() != RevealPolicy.Always;
        }

        @Override
        public boolean onChange() {
            return policy() != RevealPolicy.Always;
        }

        public RevealPolicy policy() {
            return ((Config.PolicyComponent) config).policy;
        }
    }
    public static class BooleanComponent extends IComponent {
        private BooleanComponent(Config.BooleanComponent config, Config.AdvancedComponent values, Config.DefaultValues defaultValues) {
            super(config, values, defaultValues);
        }

        @Override
        public boolean active() {
            return ((Config.BooleanComponent) config).active;
        }

        @Override
        public boolean onChange() {
            return ((Config.BooleanComponent) config).onChange;
        }
    }
    public static final IComponent None = new IComponent(new Config.IComponent(), new Config.AdvancedComponent(), new Config.DefaultValues()) {
        @Override
        public boolean active() {
            return true;
        }
    };

    PolicyComponent health;
    PolicyComponent armor;
    PolicyComponent hunger;
    PolicyComponent air;
    BooleanComponent experience;
    BooleanComponent mountJumpBar;
    PolicyComponent mountHealth;
    BooleanComponent hotbar;
    BooleanComponent statusEffects;
    BooleanComponent scoreboard;

    public PolicyComponent health() { return health; }
    public PolicyComponent armor() { return armor; }
    public PolicyComponent hunger() { return hunger; }
    public PolicyComponent air() { return air; }
    public BooleanComponent experience() { return experience; }
    public BooleanComponent mountJumpBar() { return mountJumpBar; }
    public PolicyComponent mountHealth() { return mountHealth; }
    public BooleanComponent hotbar() { return hotbar; }
    public boolean isHotbarOnSlotChange() { return Config.hotbar.onSlotChange; }
    public boolean isHotbarOnLowDurability() { return Config.hotbar.onLowDurability; }
    public int getHotbarDurabilityPercentage() { return Config.hotbar.durabilityPercentage; }
    public int getHotbarDurabilityTotal() { return Config.hotbar.durabilityTotal; }
    public float getHotbarItemsMaximumFade() { return Config.hotbar.maximumFadeHotbarItems; }
    public BooleanComponent statusEffects() { return statusEffects; }
    public boolean hidePersistentStatusEffects() { return Config.hidePersistentStatusEffects; }
    public BooleanComponent scoreboard() { return scoreboard; }
    public boolean shouldRevealScoreboardOnTitleChange() { return Config.scoreboard.scoreboard.onChange; }
    public boolean shouldRevealScoreboardOnScoreChange() { return Config.scoreboard.onScoreChange; }
    public boolean shouldRevealScoreboardOnTeamChange() { return Config.scoreboard.onTeamChange; }

    private void init() {
        health = new PolicyComponent(Config.health, Config.advanced.health, Config.defaultValues);
        armor = new PolicyComponent(Config.armor, Config.advanced.armor, Config.defaultValues);
        hunger = new PolicyComponent(Config.hunger, Config.advanced.hunger, Config.defaultValues);
        air = new PolicyComponent(Config.air, Config.advanced.air, Config.defaultValues);
        experience = new BooleanComponent(Config.experience, Config.advanced.experience, Config.defaultValues);
        mountJumpBar = new BooleanComponent(Config.mountJumpBar, Config.advanced.mountJumpBar, Config.defaultValues);
        mountHealth = new PolicyComponent(Config.mountHealth, Config.advanced.mountHealth, Config.defaultValues);
        hotbar = new BooleanComponent(Config.hotbar.hotbar, Config.advanced.hotbar, Config.defaultValues);
        statusEffects = new BooleanComponent(Config.statusEffects, Config.advanced.statusEffects, Config.defaultValues);
        scoreboard = new BooleanComponent(Config.scoreboard.scoreboard, Config.advanced.scoreboard, Config.defaultValues);
    }
}
