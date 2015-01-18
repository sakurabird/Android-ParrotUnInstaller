package sakurafish.com.parrot.uninstaller.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sakurafish.com.parrot.uninstaller.R;
import sakurafish.com.parrot.uninstaller.TopActivity;
import sakurafish.com.parrot.uninstaller.TutorialActivity;
import sakurafish.com.parrot.uninstaller.config.Config;

/**
 * Created by sakura on 2014/10/09.
 */
public class HelpFragment extends BaseFragment {

    private Context mContext;

    public static HelpFragment getInstance() {
        return new HelpFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        ((TopActivity) mContext).setFragmentViews(Config.NavMenu.HELP);
        initLayout();
    }

    public void initLayout() {
        getView().findViewById(R.id.button_tutorial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, TutorialActivity.class));
            }
        });
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
