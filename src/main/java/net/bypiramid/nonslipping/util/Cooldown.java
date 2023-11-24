package net.bypiramid.nonslipping.util;

import java.util.concurrent.TimeUnit;

public class Cooldown {

    private final Double duration;
    private final long startTime = System.currentTimeMillis();

    /**
     * This constructor supports decimal cooldown formats: 0.5D, 1.5D, 2.5D...
     *
     * @param duration The duration in decimal
     * @author comicxz
     */
    public Cooldown(Double duration) {
        this.duration = duration;
    }

    public double getPercentage() {
        return (getRemaining() * 100) / getRealDuration();
    }

    /**
     * This method is what made the decimal cooldown supported.
     *
     * @return The duration intelligently calculated.
     * @author comicxz
     */
    private long getRealDuration() {
        long extra = (int) (duration % 1 * 10) * 100;
        long realDuration = 0;

        if (duration >= 1D) {
            realDuration = duration.intValue();
        }

        return TimeUnit.SECONDS.toMillis(realDuration) + extra;
    }

    public double getRemaining() {
        long endTime = startTime + getRealDuration();
        return (-(System.currentTimeMillis() - endTime)) / 1000D;
    }

    public boolean expired() {
        return getRemaining() < 0D;
    }
}
