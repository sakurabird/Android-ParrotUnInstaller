package sakurafish.com.parrot.uninstaller.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sakurafish.com.parrot.uninstaller.R;
import sakurafish.com.parrot.uninstaller.config.Config.NavMenu;
import sakurafish.com.parrot.uninstaller.utils.ThemeUtils;

/**
 * Created by sakura on 2014/10/17.
 */
public class NavExpandableAdapter extends BaseExpandableListAdapter {

    private static final int[] EMPTY_STATE_SET = {};
    private static final int[] GROUP_EXPANDED_STATE_SET =
            {android.R.attr.state_expanded};
    private static final int[][] GROUP_STATE_SETS = {
            EMPTY_STATE_SET, // 0
            GROUP_EXPANDED_STATE_SET // 1
    };
    final private Context mContext;
    final private TypedArray mGroupIcons;
    final private String[] mGroupTitles;
    final private String[] mChildTitles;

    public NavExpandableAdapter(final Context context) {
        mContext = context;
        mGroupIcons = mContext.getResources().obtainTypedArray(R.array.navigation_group_icons);
        mGroupTitles = mContext.getResources().getStringArray(R.array.navigation_group_strings);
        mChildTitles = mContext.getResources().getStringArray(R.array.navigation_sort_strings);
    }

    @Override
    public Object getChild(final int groupPosition, final int childPosition) {
        return mChildTitles[childPosition];
    }

    @Override
    public long getChildId(final int groupPosition, final int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             final boolean isLastChild, View convertView, final ViewGroup parent) {
        ChildViewHolder holder = null;

        if (convertView == null) {
            convertView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.drawer_child_list_row, null);
            holder = new ChildViewHolder();

            holder.childRow = (RelativeLayout) convertView.findViewById(R.id.relativeLayout_drawer_child);
            ThemeUtils.setNavBackground(mContext, holder.childRow, true);
            holder.title = (TextView) convertView.findViewById(R.id.title);

            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        holder.title.setText(mChildTitles[childPosition]);

        return convertView;
    }

    @Override
    public int getChildrenCount(final int groupPosition) {
        return groupPosition == NavMenu.SORT ? mChildTitles.length : 0;
    }

    @Override
    public Object getGroup(final int groupPosition) {
        return mGroupTitles[groupPosition];
    }

    @Override
    public int getGroupCount() {
        return mGroupTitles.length;
    }

    @Override
    public long getGroupId(final int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             View convertView, final ViewGroup parent) {
        GroupViewHolder holder = null;

        if (convertView == null) {
            convertView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.drawer_group_list_row, null);
            holder = new GroupViewHolder();

            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.expand_indicator = (ImageView) convertView.findViewById(R.id.explist_indicator);
            holder.divider = convertView.findViewById(R.id.divider);

            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        if (groupPosition != NavMenu.HELP) {
            holder.divider.setVisibility(View.GONE);
        }
        holder.icon.setImageDrawable(mGroupIcons.getDrawable(groupPosition));
        holder.title.setText(mGroupTitles[groupPosition]);

        final int i = groupPosition == NavMenu.SORT ? View.VISIBLE : View.INVISIBLE;
        holder.expand_indicator.setVisibility(i);
        final int stateSetIndex = (isExpanded ? 1 : 0);
        Drawable drawable = holder.expand_indicator.getDrawable();
        drawable.setState(GROUP_STATE_SETS[stateSetIndex]);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(final int groupPosition, final int childPosition) {
        return groupPosition == NavMenu.SORT;
    }

    private class GroupViewHolder {
        private ImageView icon;
        private TextView title;
        private ImageView expand_indicator;
        private View divider;
    }

    private class ChildViewHolder {
        private RelativeLayout childRow;
        private TextView title;
    }
}