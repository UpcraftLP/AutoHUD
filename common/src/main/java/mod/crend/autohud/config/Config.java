package mod.crend.autohud.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class Config extends MidnightConfig {

    /* MAIN OPTIONS */
    @MidnightConfig.Entry
    public static boolean dynamicOnLoad = true;
    @MidnightConfig.Entry(min = 10, max = 200, isSlider = true)
    public static int ticksRevealed = 40;
    @MidnightConfig.Entry
    public static boolean animationMove = true;
    @MidnightConfig.Entry
    public static boolean animationFade = true;
    @MidnightConfig.Entry(min = 1, max = 4.0, precision = 100, isSlider = true)
    public static double animationSpeed = 2.0;

    public static class AnimationSpeeds {

        @MidnightConfig.Entry(min = 1, max = 4.0, isSlider = true, precision = 100)
        public double moveIn = 0;
        @MidnightConfig.Entry(min = 1, max = 4.0, isSlider = true, precision = 100)
        public double moveOut = 0;
        @MidnightConfig.Entry(min = 1, max = 4.0, isSlider = true, precision = 100)
        public double fadeIn = 0;
        @MidnightConfig.Entry(min = 1, max = 4.0, isSlider = true, precision = 100)
        public double fadeOut = 0;
    }

    @MidnightConfig.Entry
    public static AnimationSpeeds animationSpeeds = new AnimationSpeeds();

    //    @Tooltip
    @MidnightConfig.Entry
    public static RevealType revealType = RevealType.Stacked;
    //    @Category(name = "components", group = "statusEffects")
//    @Tooltip
    @MidnightConfig.Entry()
    public static boolean statusEffectTimer = true;

    /* COMPONENTS */
    public static class DefaultValues {

        @MidnightConfig.Entry(min = 0.1, max = 3.0, isSlider = true, precision = 10)
        public double speedMultiplier = 1.0;
        @MidnightConfig.Entry(min = 0, max = 200, isSlider = true)
        public int distance = 60;
        @MidnightConfig.Entry(min = 0.0, max = 1.0, isSlider = true, precision = 10)
        public double maximumFade = 0.0d;
    }

    //    @Category(name = "advanced")
    @MidnightConfig.Entry
    public static DefaultValues defaultValues = new DefaultValues();

    public static class AdvancedComponent {

        //        @Translation(key = "autohud.option.advanced.direction")
        @MidnightConfig.Entry
        public ScrollDirection direction = ScrollDirection.Down;
        //        @Translation(key = "autohud.option.advanced.speedMultiplier")
        @MidnightConfig.Entry(min = 0.1, max = 3.0, precision = 10, isSlider = true)
        public double speedMultiplier = -1;
        //        @Translation(key = "autohud.option.advanced.distance")
        @MidnightConfig.Entry(min = 0, max = 200, isSlider = true)
        public int distance = -1;
        //        @Translation(key = "autohud.option.advanced.maximumFade")
        @MidnightConfig.Entry(min = 0.1, max = 3.0, precision = 10, isSlider = true)
        public double maximumFade = -1d;
    }

    public static class IComponent {
    }

    public static class SimpleComponent extends IComponent {
        @MidnightConfig.Entry
        public boolean active = true;
    }

    public static class PolicyComponent extends IComponent {
        @MidnightConfig.Entry
        public RevealPolicy policy = RevealPolicy.Changing;
    }

    public static class BooleanComponent extends IComponent {
        @MidnightConfig.Entry
        public boolean active = true;
        @MidnightConfig.Entry
        public boolean onChange = true;
    }

    //    @Category(name = "components", group = "statusBars")
    @MidnightConfig.Entry
    public static PolicyComponent health = new PolicyComponent();
    //    @Category(name = "components", group = "statusBars")
    @MidnightConfig.Entry
    public static PolicyComponent hunger = new PolicyComponent() {{
        policy = RevealPolicy.Low;
    }};
    //    @Category(name = "components", group = "statusBars")
    @MidnightConfig.Entry
    public static PolicyComponent air = new PolicyComponent() {{
        policy = RevealPolicy.NotFull;
    }};
    //    @Category(name = "components", group = "statusBars")
    @MidnightConfig.Entry
    public static PolicyComponent armor = new PolicyComponent();
    //    @Category(name = "components", group = "statusBars")
    @MidnightConfig.Entry
    public static BooleanComponent experience = new BooleanComponent();
    //    @Category(name = "components", group = "statusBars")
    @MidnightConfig.Entry
    public static BooleanComponent mountJumpBar = new BooleanComponent();
    //    @Category(name = "components", group = "statusBars")
    @MidnightConfig.Entry
    public static PolicyComponent mountHealth = new PolicyComponent();
    //    @Category(name = "components", group = "statusEffects")
    @MidnightConfig.Entry
    public static BooleanComponent statusEffects = new BooleanComponent();
    //    @Category(name = "components", group = "statusEffects")
//    @Tooltip
    @MidnightConfig.Entry
    public static boolean hidePersistentStatusEffects = true;

    //    @Category(name = "components")
    @MidnightConfig.Entry
    public static HotbarComponents hotbar = new HotbarComponents();
    //    @Category(name = "components")
    @MidnightConfig.Entry
    public static ScoreboardComponents scoreboard = new ScoreboardComponents();
    //    @Category(name = "advanced")
//    @Label(key = "autohud.option.advanced.label")
//    @TransitiveObject
    @MidnightConfig.Entry
    public static AdvancedComponents advanced = new AdvancedComponents();


    public static class HotbarComponents {
        @MidnightConfig.Entry
        public BooleanComponent hotbar = new BooleanComponent();
        @MidnightConfig.Entry
        public boolean onSlotChange = true;
        @MidnightConfig.Entry
        public boolean onLowDurability = true;
        @MidnightConfig.Entry(min = 0, max = 100, isSlider = true)
        public int durabilityPercentage = 10;
        @MidnightConfig.Entry
        public int durabilityTotal = 20;
        @MidnightConfig.Entry(min = 0.0F, max = 1.0F, isSlider = true, precision = 10)
        public float maximumFadeHotbarItems = 0.0f;
    }

    public static class ScoreboardComponents {
        @MidnightConfig.Entry
        public BooleanComponent scoreboard = new BooleanComponent();
        @MidnightConfig.Entry
        public boolean onScoreChange = true;
        //        @Tooltip
        @MidnightConfig.Entry
        public boolean onTeamChange = true;
    }

    public static class AdvancedComponents {
        //        @Translation(key = "autohud.group.hotbar")
        @MidnightConfig.Entry
        public AdvancedComponent hotbar = new AdvancedComponent();
        //        @Translation(key = "autohud.group.health")
        @MidnightConfig.Entry
        public AdvancedComponent health = new AdvancedComponent();
        //        @Translation(key = "autohud.group.armor")
        @MidnightConfig.Entry
        public AdvancedComponent armor = new AdvancedComponent();
        //        @Translation(key = "autohud.group.hunger")
        @MidnightConfig.Entry
        public AdvancedComponent hunger = new AdvancedComponent();
        //        @Translation(key = "autohud.group.air")
        @MidnightConfig.Entry
        public AdvancedComponent air = new AdvancedComponent();
        //        @Translation(key = "autohud.group.experience")
        @MidnightConfig.Entry
        public AdvancedComponent experience = new AdvancedComponent();
        //        @Translation(key = "autohud.group.mountJumpBar")
        @MidnightConfig.Entry
        public AdvancedComponent mountJumpBar = new AdvancedComponent();
        //        @Translation(key = "autohud.group.mountHealth")
        @MidnightConfig.Entry
        public AdvancedComponent mountHealth = new AdvancedComponent();
        //        @Translation(key = "autohud.group.statusEffects")
        @MidnightConfig.Entry
        public AdvancedComponent statusEffects = new AdvancedComponent();
        //        @Translation(key = "autohud.group.scoreboard")
        @MidnightConfig.Entry
        public AdvancedComponent scoreboard = new AdvancedComponent();

        public AdvancedComponents() {
            statusEffects.direction = ScrollDirection.Up;
            scoreboard.direction = ScrollDirection.Right;
            scoreboard.distance = 100;
        }
    }

    /* OPTIONS END */
}
