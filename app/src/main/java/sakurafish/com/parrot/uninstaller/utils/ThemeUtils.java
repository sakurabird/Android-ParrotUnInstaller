package sakurafish.com.parrot.uninstaller.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import sakurafish.com.parrot.uninstaller.R;
import sakurafish.com.parrot.uninstaller.config.Config;
import sakurafish.com.parrot.uninstaller.config.Config.ThemeColor;
import sakurafish.com.parrot.uninstaller.pref.Pref;

/**
 * Created by sakura on 2014/10/31.
 */
public class ThemeUtils {

    private ThemeUtils() {
    }

    public static void onActivityCreateSetTheme(@NonNull final Activity activity) {
        final String s = Pref.getPrefString(activity, Config.PREF_THEME);
        final int color = s == null ? 1 : Integer.valueOf(s);
        switch (color) {
            case 0:
            case ThemeColor.GREEN:
                activity.setTheme(R.style.MyTheme_Green);
                break;
            case ThemeColor.PINK:
                activity.setTheme(R.style.MyTheme_Pink);
                break;
            case ThemeColor.BLUE:
                activity.setTheme(R.style.MyTheme_Blue);
                break;
            case ThemeColor.BROWN:
                activity.setTheme(R.style.MyTheme_Brown);
                break;
            case ThemeColor.PURPLE:
                activity.setTheme(R.style.MyTheme_Purple);
                break;
            default:
                activity.setTheme(R.style.MyTheme_Green);
                break;
        }
    }

    public static void changeToTheme(@NonNull final Activity activity) {
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    public static void setNavBackground(@NonNull final Context context, @NonNull final View view, final boolean isChild) {
        final String s = Pref.getPrefString(context, Config.PREF_THEME);
        final int color = s == null ? 1 : Integer.valueOf(s);
        switch (color) {
            case 0:
            case ThemeColor.GREEN:
                view.setBackgroundColor(isChild ? context.getResources().getColor(R.color.nav_background_child_green) :
                        context.getResources().getColor(R.color.nav_background_green));
                break;
            case ThemeColor.PINK:
                view.setBackgroundColor(isChild ? context.getResources().getColor(R.color.nav_background_child_pink) :
                        context.getResources().getColor(R.color.nav_background_pink));
                break;
            case ThemeColor.BLUE:
                view.setBackgroundColor(isChild ? context.getResources().getColor(R.color.nav_background_child_blue) :
                        context.getResources().getColor(R.color.nav_background_blue));
                break;
            case ThemeColor.BROWN:
                view.setBackgroundColor(isChild ? context.getResources().getColor(R.color.nav_background_child_brown) :
                        context.getResources().getColor(R.color.nav_background_brown));
                break;
            case ThemeColor.PURPLE:
                view.setBackgroundColor(isChild ? context.getResources().getColor(R.color.nav_background_child_purple) :
                        context.getResources().getColor(R.color.nav_background_purple));
                break;
            default:
                view.setBackgroundColor(isChild ? context.getResources().getColor(R.color.nav_background_child_green) :
                        context.getResources().getColor(R.color.nav_background_green));
                break;
        }
    }

    public static void setTopBackground(@NonNull final Context context, @NonNull final View view) {
        final String s = Pref.getPrefString(context, Config.PREF_THEME);
        final int color = s == null ? 1 : Integer.valueOf(s);
        switch (color) {
            case 0:
            case ThemeColor.GREEN:
                view.setBackgroundColor(context.getResources().getColor(R.color.list_background_green));
                break;
            case ThemeColor.PINK:
                view.setBackgroundColor(context.getResources().getColor(R.color.list_background_pink));
                break;
            case ThemeColor.BLUE:
                view.setBackgroundColor(context.getResources().getColor(R.color.list_background_blue));
                break;
            case ThemeColor.BROWN:
                view.setBackgroundColor(context.getResources().getColor(R.color.list_background_brown));
                break;
            case ThemeColor.PURPLE:
                view.setBackgroundColor(context.getResources().getColor(R.color.list_background_purple));
                break;
            default:
                view.setBackgroundColor(context.getResources().getColor(R.color.list_background_green));
                break;
        }
    }

    public static void setDividerBackground(@NonNull final Context context, @NonNull final View view) {
        final String s = Pref.getPrefString(context, Config.PREF_THEME);
        final int color = s == null ? 1 : Integer.valueOf(s);
        switch (color) {
            case 0:
            case ThemeColor.GREEN:
                view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_green));
                break;
            case ThemeColor.PINK:
                view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_pink));
                break;
            case ThemeColor.BLUE:
                view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_blue));
                break;
            case ThemeColor.BROWN:
                view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_brown));
                break;
            case ThemeColor.PURPLE:
                view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_purple));
                break;
            default:
                view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_green));
                break;
        }
    }
}
