package easings;

public class EasingFunction {

    public static float easeOutBounce(float x) {
        float n1 = 7.5625f;
        float d1 = 2.75f;
        double v;
        if (x < 1 / d1) {
            v = n1 * x * x;
        } else if (x < 2 / d1) {
            v = n1 * (x -= 1.5 / d1) * x + 0.75;
        } else if (x < 2.5 / d1) {
            v = n1 * (x -= 2.25 / d1) * x + 0.9375;
        } else {
            v = n1 * (x -= 2.625 / d1) * x + 0.984375;
        }
        return (float) v;
    }

    public static float easeInOutElastic(float x) {
        double c5 = (2 * Math.PI) / 4.5;
        double v = x == 0
                ? 0
                : x == 1
                ? 1
                : x < 0.5
                ? -(Math.pow(2, 20 * x - 10) * Math.sin((20 * x - 11.125) * c5)) / 2
                : (Math.pow(2, -20 * x + 10) * Math.sin((20 * x - 11.125) * c5)) / 2 + 1;
        return (float) v;
    }

    public static float easeOutExpo(float x)
    {
        return (float) (x == 1 ? 1 : (1 - Math.pow(2, -10 * x)));
    }

    public static float easeOutCubic(float x)
    {
        return (float) (1 - Math.pow(1 - x, 3));
    }

    public static float easeOutQuint(float x)
    {
        return (float) (1 - Math.pow(1 - x, 5));
    }

    public static float easeOutElastic(float x)
    {
        double c4 = (2 * Math.PI) / 3;

        double v = x == 0
                ? 0
                : x == 1
                ? 1
                : Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1;
        return (float) v;
    }

}
