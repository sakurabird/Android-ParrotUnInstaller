package sakurafish.com.parrot.uninstaller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;

import sakurafish.com.parrot.uninstaller.R;
import sakurafish.com.parrot.uninstaller.ui.TutorialPagerAdapter;
import sakurafish.com.parrot.uninstaller.utils.Utils;

/**
 * Created by sakura on 2014/11/04.
 */
public class TutorialFragment extends BaseFragment {

    private Context mContext;

    public static TutorialFragment getInstance() {
        return  new TutorialFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_turotial, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();

        initLayout();
    }

    private void initLayout() {
        TutorialPagerAdapter adapter = new TutorialPagerAdapter(mContext, new TutorialPagerAdapter.ClickListener() {
            @Override
            public void onClick() {
                Utils.showToast(getActivity(), getString(R.string.tutorial_toast));
                getActivity().finish();
            }
        });
        ViewPager viewPager = (ViewPager) getView().findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        CirclePageIndicator indicator = (CirclePageIndicator) getView().findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        indicator.setCurrentItem(0);
        indicator.notifyDataSetChanged();
    }

    private void finalizeLayout() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        finalizeLayout();
        mContext = null;
    }
}
