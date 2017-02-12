package sakurafish.com.parrot.uninstaller.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

public class AppListAdapter extends ArrayAdapter<Apps> {

    private static int sFont_size;
    final private Context mContext;
    final private BitmapLruCache mLruCache;
    final private TextView mAppCount;
    final private SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
    final private PackageManager mPackageManager;
    private List<Apps> mAppsList;
    private boolean[] mSelectedList;
    private AppItemClickListener mItemListener;
    private int mSelected = 0;

    public AppListAdapter(final Context context, final BitmapLruCache lruCache, final TextView appCount) {
        super(context, R.layout.row_applist);
        mContext = context;
        mLruCache = lruCache;
        mAppCount = appCount;

        mPackageManager = mContext.getPackageManager();
        final String s = Pref.getPrefString(context, Config.PREF_FONT_SIZE);
        if (TextUtils.isEmpty(s)) {
            Pref.setPref(context, Config.PREF_FONT_SIZE, "2");
        }
        sFont_size = Integer.valueOf(Pref.getPrefString(context, Config.PREF_FONT_SIZE));
    }

    public void swapItems(@Nullable final List<Apps> appsList) {
        mAppsList = appsList;
        mSelectedList = new boolean[mAppsList.size()];
        mSelected = 0;
        mAppCount.setText(createAppSizeText());
        notifyDataSetChanged();
    }

    public void swapClickedPosition(final int position) {
        mSelected = mSelectedList[position] ? --mSelected : ++mSelected;
        mSelectedList[position] = !mSelectedList[position];
        mAppCount.setText(createAppSizeText());
        notifyDataSetChanged();
    }

    public boolean[] getSelectedList() {
        return mSelectedList;
    }

    public int getSelected() {
        return mSelected;
    }

    public synchronized void countSelected() {
        mSelected = 0;
        for (int i = 0; i < mSelectedList.length; i++) {
            if (mSelectedList[i]) {
                mSelected++;
            }
        }
        mAppCount.setText(createAppSizeText());
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
        ViewHolder holder;
        final Apps apps = mAppsList.get(position);

        if (convertView == null) {
            convertView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.row_applist, null);
            holder = new ViewHolder();

            holder.selected = convertView.findViewById(R.id.selected);
            holder.appName = (TextView) convertView.findViewById(R.id.app_name);
            holder.appName.setTextSize(Config.FONT_SIZE_TITLES[sFont_size - 1]);
            holder.packageSize = (TextView) convertView.findViewById(R.id.app_size);
            holder.packageSize.setTextSize(Config.FONT_SIZE_FONT_SUMMARIES[sFont_size - 1]);
            holder.packageDate = (TextView) convertView.findViewById(R.id.app_date);
            holder.packageDate.setTextSize(Config.FONT_SIZE_FONT_SUMMARIES[sFont_size - 1]);
            holder.iconView = (ImageView) convertView.findViewById(R.id.app_icon);
            holder.contextMenu = (ImageView) convertView.findViewById(R.id.row_context_menu);
            holder.contextMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Pref.getPrefBool(mContext, Config.PREF_SOUND_ON, false)) {
                        UninstallerApplication.getSoundManager().play(UninstallerApplication.getSoundIds()[0]);
                    }
                    if (mItemListener != null) {
                        mItemListener.onClickItem(v, (Apps) v.getTag());
                    }
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mSelectedList[position]) {
            holder.selected.setBackgroundColor(mContext.getResources().getColor(R.color.selected_color));
        } else {
            holder.selected.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }
        holder.appName.setText(apps.getName());

        final Bitmap bitmap = mLruCache.get(apps.getPackage_name());
        if (bitmap != null) {
            // キャッシュに存在
            holder.iconView.setImageBitmap(bitmap);
        } else {
            // キャッシュになし
            Drawable icon;
            try {
                icon = mPackageManager.getApplicationIcon(apps.getPackage_name());
                Bitmap apkIcon;
                if (icon instanceof BitmapDrawable) {
                    apkIcon = ((BitmapDrawable) icon).getBitmap();
                } else {
                    Bitmap bmp = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bmp);
                    icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    icon.draw(canvas);
                    apkIcon = bmp;
                }
                holder.iconView.setImageBitmap(apkIcon);
                mLruCache.put(apps.getPackage_name(), apkIcon);
            } catch (final PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        holder.packageDate.setText(df.format(apps.getInstall_time()));
        final float f = apps.getPackage_size() * (float) 0.000001;//MB
        holder.packageSize.setText(String.format("%.2f MB", f));
        holder.contextMenu.setTag(apps);

        return convertView;
    }

    @NonNull
    private String createAppSizeText() {
        final StringBuilder sb = new StringBuilder();
        sb.append(mSelected);
        sb.append("/");
        sb.append(mAppsList.size());
        return sb.toString();
    }

    public void setOnAppItemClickListener(@NonNull final AppItemClickListener listener) {
        mItemListener = listener;
    }

    private class ViewHolder {
        View selected;
        TextView appName;
        TextView packageSize;
        TextView packageDate;
        ImageView iconView;
        ImageView contextMenu;
    }
}
