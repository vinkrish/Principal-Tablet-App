package in.principal.util;

import in.principal.activity.R;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by vinkrish.
 */

public class AnimationUtils {

    public static void activityExit(Activity activity) {
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public static void activityEnter(Activity activity) {
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public static void activityExitVertical(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_top_middle_full, R.anim.slide_middle_bottom_full);
    }

    public static void activityEnterVertical(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_bottom_middle_full, R.anim.slide_middle_top_full);
    }

    // Translate animate Views
    public static Animation animateViewFly(View viewToAnimate, float fromX, float toX,
                                           float fromY, float toY, int typeRelative, int animationDuration,
                                           int animationRepeatCount) {
        viewToAnimate.setVisibility(View.VISIBLE);
        Animation animation = new TranslateAnimation(typeRelative, fromX, typeRelative, toX, typeRelative, fromY, typeRelative, toY);
        animation.setDuration(animationDuration);
        animation.setRepeatCount(animationRepeatCount);
        animation.setInterpolator(new LinearInterpolator());
        viewToAnimate.startAnimation(animation);
        return animation;
    }

    // convert dp to px
    public static int dpToPx(Resources resources, int dp) {
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

}
