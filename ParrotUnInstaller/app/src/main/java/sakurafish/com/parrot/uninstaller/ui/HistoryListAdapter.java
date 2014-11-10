package sakurafish.com.parrot.uninstaller.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import greendao.Apps;
import sakurafish.com.parrot.uninstaller.R;
import sakurafish.com.parrot.uninstaller.UninstallerApplication;
import sakurafish.com.parrot.uninstaller.config.Config;
import sakurafish.com.parrot.uninstaller.pref.Pref;

public class HistoryListAdapter extends ArrayAdapter<Apps> {

    private static int sFont_size;
    //    private int mLastPosition;
    private List<Apps> mAppsList;
    private Context mContext;
    private AppItemClickListener mItemListener;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");

    public HistoryListAdapter(final Context context) {
        super(context, R.layout.row_applist);
        mContext = context;

        final String s = Pref.getPrefString(context, Config.PREF_FONT_SIZE);
        if (TextUtils.isEmpty(s)) {
            Pref.setPref(context, Config.PREF_FONT_SIZE, "2");
        }
        sFont_size = Integer.valueOf(Pref.getPrefString(context, Config.PREF_FONT_SIZE));
    }

    public void swapItems(@Nullable final List<Apps> appsList) {
        mAppsList = appsList;
        notifyDataSetChanged();
    }

    @Override
    public Apps getItem(final int position) {
        return ((null != mAppsList) ? mAppsList.get(position) : null);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public int getCount() {
        return ((null != mAppsList) ? mAppsList.size() : 0);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        final Apps apps = mAppsList.get(position);

        if (convertView == null) {
            convertView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.row_historylist, null);
            holder = new ViewHolder();

            holder.appName = (TextView) convertView.findViewById(R.id.app_name);
            holder.appName.setTextSize(Config.FONT_SIZE_TITLES[sFont_size - 1]);
            holder.packageSize = (TextView) convertView.findViewById(R.id.app_size);
            holder.packageSize.setTextSize(Config.FONT_SIZE_FONT_SUMMARIES[sFont_size - 1]);
            holder.packageDate = (TextView) convertView.findViewById(R.id.app_date);
            holder.packageDate.setTextSize(Config.FONT_SIZE_FONT_SUMMARIES[sFont_size - 1]);
            holder.googleplay = (ImageView) convertView.findViewById(R.id.googleplay);
            holder.googleplay.setTag(apps);
            holder.googleplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Pref.getPrefBool(mContext, Config.PREF_SOUND_ON, false)) {
                        UninstallerApplication.getSoundManager().play(UninstallerApplication.getSoundIds()[0]);
                    }
                    try {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + ((Apps) v.getTag()).getPackage_name())));
                    } catch (android.content.ActivityNotFoundException e) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + ((Apps) v.getTag()).getPackage_name())));
                    }

                }
            });
            holder.delete = (ImageView) convertView.findViewById(R.id.delete);
            holder.delete.setTag(apps);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (mItemListener != null) {
                        mItemListener.onClickItem(v, (Apps) v.getTag());
                    }
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.appName.setText(apps.getName());
        holder.packageDate.setText(df.format(apps.getInstall_time()));
        float f = apps.getPackage_size() * (float) 0.000001;//MB
        holder.packageSize.setText(String.format("%.2f MB", f));

//        float initialTranslation = (mLastPosition <= position ? 500f : -500f);
//        convertView.setTranslationY(initialTranslation);
//        convertView.animate()
//                .setInterpolator(new DecelerateInterpolator(1.0f))
//                .translationY(0f)
//                .setDuration(300l)
//                .setListener(null);
//        mLastPosition = position;

        return convertView;
    }

    public void setOnAppItemClickListener(@NonNull final AppItemClickListener listener) {
        mItemListener = listener;
    }

    private class ViewHolder {
        TextView appName;
        TextView packageSize;
        TextView packageDate;
        ImageView googleplay;
        ImageView delete;
    }
}
