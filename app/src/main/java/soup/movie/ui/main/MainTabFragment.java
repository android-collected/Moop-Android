package soup.movie.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;

import soup.movie.ui.BaseFragment;

public abstract class MainTabFragment extends BaseFragment {

    public MainTabFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public final void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(getMenuResource(), menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected abstract @MenuRes int getMenuResource();

    protected final void setTitle(String title) {
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            activity.setTitle(title);
        }
    }

    protected final void showSubPanel(String title) {
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).setSubPanel(title);
            ((MainActivity) activity).setSubPanelVisibility(true);

            //TODO: show fragment
        }
    }
}