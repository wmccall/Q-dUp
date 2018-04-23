package mccode.qdup.Utils;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.widget.Button;

/**
 * Created by Will on 3/20/2018.
 */

public class GeneralUIUtils {

    public static ValueAnimator initializeValueAnimator(int startColor, int endColor, int timeDuration, final Button button){
        ValueAnimator animation = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        animation.setDuration(timeDuration);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                button.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        return animation;
    }

    public static void animateButtonClick(int startColor, int endColor, int timeDuration, final Button button){
        ValueAnimator colorAnimation = initializeValueAnimator(startColor, endColor, timeDuration, button);
        ValueAnimator colorAnimationRev = initializeValueAnimator(endColor, startColor, timeDuration, button);
        colorAnimation.start();
        colorAnimationRev.start();
    }
}
