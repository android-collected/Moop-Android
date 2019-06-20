package soup.movie.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.SharedElementCallback
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import soup.movie.R
import soup.movie.analytics.EventAnalytics
import soup.movie.databinding.HomeContentsBinding
import soup.movie.databinding.HomeFragmentBinding
import soup.movie.databinding.HomeHeaderBinding
import soup.movie.databinding.HomeHeaderHintBinding
import soup.movie.ui.base.BaseFragment
import soup.movie.ui.main.MainViewModel
import soup.movie.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeFragment : BaseFragment() {

    private val activityViewModel: MainViewModel by activityViewModel()
    private val viewModel: HomeViewModel by viewModel()

    @Inject
    lateinit var analytics: EventAnalytics

    private val listAdapter by lazyFast {
        HomeListAdapter { movie, sharedElements ->
            analytics.clickMovie()
            MovieSelectManager.select(movie)
            findNavController().navigate(
                HomeFragmentDirections.actionToDetail(),
                ActivityNavigatorExtras(
                    activityOptions = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(requireActivity(), *sharedElements)
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return HomeFragmentBinding.inflate(inflater, container, false)
            .apply { init(viewModel) }
            .root
    }

    private fun HomeFragmentBinding.init(viewModel: HomeViewModel) {
        prepareSharedElements()
        header.apply {
            toolbar.setNavigationOnClickListener {
                activityViewModel.openNavigationMenu()
            }
            toolbar.inflateMenu(R.menu.fragment_movie_list)
            toolbar.setOnMenuItemClickListener {
                consume {
                    if (it.itemId == R.id.action_filter) {
                        analytics.clickMenuFilter()
                        findNavController().navigate(HomeFragmentDirections.actionToFilter())
                    }
                }
            }
            actionNow.setOnDebounceClickListener {
                viewModel.onNowClick()
            }
            actionPlan.setOnDebounceClickListener {
                viewModel.onPlanClick()
            }
        }
        headerHint.hintButton.setOnDebounceClickListener {
            header.appBar.setExpanded(true)
            contents.listView.smoothScrollToPosition(0)
        }
        contents.apply {
            swipeRefreshLayout.apply {
                setProgressBackgroundColorSchemeColor(context.getColorAttr(R.attr.colorSurface))
                setColorSchemeColors(context.getColorAttr(R.attr.colorOnSurface))
                setOnRefreshListener {
                    viewModel.refresh()
                }
            }
            listView.apply {
                adapter = listAdapter
                itemAnimator = SlideInUpAnimator().apply {
                    addDuration = 200
                    removeDuration = 200
                }
            }
            errorView.setOnDebounceClickListener {
                viewModel.refresh()
            }
        }
        viewModel.headerUiModel.observe(viewLifecycleOwner) {
            header.render(it)
            headerHint.render(it)
        }
        viewModel.contentsUiModel.observe(viewLifecycleOwner) {
            contents.render(it)
        }
    }

    /** UI Renderer */

    private fun HomeHeaderBinding.render(uiModel: HomeHeaderUiModel) {
        fun View.isTabChecked(checked: Boolean) {
            isEnabled = checked.not()
            if (this is ViewGroup) {
                children.forEach {
                    it.isEnabled = checked.not()
                }
            }
        }
        actionNow.isTabChecked(uiModel.isNow)
        actionPlan.isTabChecked(uiModel.isPlan)
    }

    private fun HomeHeaderHintBinding.render(uiModel: HomeHeaderUiModel) {
        hintLabel.setText(if (uiModel.isNow) {
            R.string.menu_now
        } else {
            R.string.menu_plan
        })
    }

    private fun HomeContentsBinding.render(uiModel: HomeContentsUiModel) {
        swipeRefreshLayout.isRefreshing = uiModel.isLoading
        errorView.isVisible = uiModel.isError
        listAdapter.submitList(uiModel.movies)
        noItemsView.isVisible = uiModel.hasNoItem
    }

    /** SharedElements */

    private fun HomeFragmentBinding.prepareSharedElements() {
        postponeEnterTransition(400, TimeUnit.MILLISECONDS)
        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: List<String>,
                sharedElements: MutableMap<String, View>
            ) {
                sharedElements.clear()
                MovieSelectManager.getSelectedItem()?.run {
                    contents.listView.findViewWithTag<View>(id)?.let { movieView ->
                        names.forEach { name ->
                            val id: Int = when (name) {
                                "background" -> R.id.backgroundView
                                "poster" -> R.id.posterView
                                "age_bg" -> R.id.ageBgView
                                "new" -> R.id.newView
                                "best" -> R.id.bestView
                                "d_day" -> R.id.dDayView
                                else -> View.NO_ID
                            }
                            movieView.findViewById<View>(id)?.let {
                                sharedElements[name] = it
                            }
                        }
                    }
                }
            }
        })
    }
}