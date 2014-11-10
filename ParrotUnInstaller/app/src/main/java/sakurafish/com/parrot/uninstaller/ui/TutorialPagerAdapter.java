package sakurafish.com.parrot.uninstaller.ui;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import sakurafish.com.parrot.uninstaller.R;

public class TutorialPagerAdapter extends PagerAdapter{

    private static int[] sImageResources = {
            R.drawable.tutorial1,
            R.drawable.tutorial2,
            R.drawable.tutorial3,
            R.drawable.tutorial4,
            R.drawable.tutorial5,
    };
    private static int[] sTitleResources = {
            R.string.tutorial_title1,
            R.string.tutorial_title2,
            R.string.tutorial_title3,
            R.string.tutorial_title4,
            R.string.tutorial_title5,
    };
    private static int[] sTextResources = {
            R.string.tutorial_text1,
            R.string.tutorial_text2,
            R.string.tutorial_text3,
            R.string.tutorial_text4,
            R.string.tutorial_text5,
    };
    private Context mContext;
    private ClickListener mListener = null;

    public TutorialPagerAdapter(@NonNull final Context context, @NonNull final ClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public int getCount() {
        return sTitleResources.length;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        View contentView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.views_turotial, null);
        TextView title = (TextView) contentView.findViewById(R.id.title);
        title.setText(sTitleResources[position]);
        TextView text = (TextView) contentView.findViewById(R.id.text);
        text.setText(sTextResources[position]);
        ImageView image = (ImageView) contentView.findViewById(R.id.image);
        image.setImageResource(sImageResources[position]);

        Button button = (Button) contentView.findViewById(R.id.start);
        if (position == sTitleResources.length - 1) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.INVISIBLE);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick();
            }
        });

        container.addView(contentView, 0);
        return contentView;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object view) {
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    public interface ClickListener {
        void onClick();
    }
}
