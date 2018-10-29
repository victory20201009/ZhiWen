package com.zzu.zk.zhiwen.customed_ui;
import android.content.Context;
import android.view.Gravity;
public class BadgeFactory {

    public static BadgeView create(Context context) {
        return new BadgeView(context);
    }

    public static BadgeView createDot(Context context) {
        return new BadgeView(context).setBadgeLayoutParams(10, 10)
                .setTextSize(0)
                .setBadgeGravity(Gravity.RIGHT | Gravity.TOP)
                .setBackgroundShape(BadgeView.SHAPE_CIRCLE);
    }

    public static BadgeView createCircle(Context context) {
        return new BadgeView(context).setBadgeLayoutParams(16, 16)
                .setTextSize(12)
                .setBadgeGravity(Gravity.RIGHT | Gravity.TOP)
                .setBackgroundShape(BadgeView.SHAPE_CIRCLE);
    }

    public static BadgeView createRectangle(Context context) {
        return new BadgeView(context).setBadgeLayoutParams(2, 20)
                .setTextSize(12)
                .setBadgeGravity(Gravity.RIGHT | Gravity.TOP)
                .setBackgroundShape(BadgeView.SHAPE_RECTANGLE);
    }

    public static BadgeView createOval(Context context) {
        return new BadgeView(context).setBadgeLayoutParams(25, 20)
                .setTextSize(12)
                .setBadgeGravity(Gravity.RIGHT | Gravity.TOP)
                .setBackgroundShape(BadgeView.SHAPE_OVAL);
    }

    public static BadgeView createSquare(Context context) {
        return new BadgeView(context).setBadgeLayoutParams(20, 20)
                .setTextSize(12)
                .setBadgeGravity(Gravity.RIGHT | Gravity.TOP)
                .setBackgroundShape(BadgeView.SHAPE_SQUARE);
    }

    public static BadgeView createRoundRect(Context context) {
        return new BadgeView(context).setBadgeLayoutParams(25, 20)
                .setTextSize(12)
                .setBadgeGravity(Gravity.RIGHT | Gravity.TOP)
                .setBackgroundShape(BadgeView.SHAPTE_ROUND_RECTANGLE);
    }

}
