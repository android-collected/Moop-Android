package soup.movie.ui.main.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import soup.movie.R;
import soup.movie.data.model.TheaterCode;
import soup.movie.di.FragmentScoped;
import soup.movie.ui.main.MainTabFragment;
import soup.movie.ui.main.settings.SettingsViewState.DoneState;
import soup.movie.ui.theater.edit.TheaterEditActivity;
import timber.log.Timber;

@FragmentScoped
public class SettingsFragment extends MainTabFragment implements SettingsContract.View {

    @BindView(R.id.icon_home_type_vertical)
    CardView homeTypeVerticalCard;

    @BindView(R.id.icon_home_type_horizontal)
    CardView homeTypeHorizontalCard;

    @BindView(R.id.theater_empty)
    TextView theaterEmpty;

    @BindView(R.id.theater_group)
    ChipGroup theaterGroup;

    @Inject
    SettingsContract.Presenter presenter;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.attach(this);
    }

    @Override
    public void onPause() {
        presenter.detach();
        super.onPause();
    }

    @Override
    public void render(@NonNull SettingsViewState viewState) {
        Timber.i("render: %s", viewState);
        if (viewState instanceof DoneState) {
            renderInternal((DoneState) viewState);
        } else {
            throw new IllegalStateException("Unknown UI Model");
        }
    }

    private void renderInternal(DoneState viewState) {
        updateHomeTypeCards(viewState.isHomeTypeVertical());
        List<TheaterCode> theaters = viewState.getTheaterList();
        if (theaters.isEmpty()) {
            theaterEmpty.setVisibility(View.VISIBLE);
            theaterGroup.removeAllViews();
            theaterGroup.setVisibility(View.GONE);
        } else {
            theaterEmpty.setVisibility(View.GONE);
            theaterGroup.removeAllViews();
            theaterGroup.setVisibility(View.VISIBLE);

            for (TheaterCode theater : theaters) {
                Chip theaterChip = (Chip) View.inflate(getContext(), R.layout.chip_cgv, null);
                theaterChip.setText(theater.getName());
                theaterGroup.addView(theaterChip);
            }
        }
    }

    private void updateHomeTypeCards(boolean verticalIsSelected) {
        if (verticalIsSelected) {
            homeTypeVerticalCard.setSelected(true);
            homeTypeHorizontalCard.setSelected(false);
        } else {
            homeTypeVerticalCard.setSelected(false);
            homeTypeHorizontalCard.setSelected(true);
        }
    }

    @OnClick(R.id.icon_home_type_vertical)
    public void onVerticalHomeTypeClicked() {
        presenter.onVerticalHomeTypeClicked();
    }

    @OnClick(R.id.icon_home_type_horizontal)
    public void onHorizontalHomeTypeClicked() {
        presenter.onHorizontalHomeTypeClicked();
    }

    @OnClick(R.id.theater_edit)
    public void onTheaterEditClicked() {
        startActivity(new Intent(getContext(), TheaterEditActivity.class));
    }
}
