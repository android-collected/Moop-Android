package soup.movie.ui.main.now

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.fragment_vertical_list.*
import soup.movie.databinding.FragmentVerticalListBinding
import soup.movie.ui.main.BaseTabFragment
import soup.movie.ui.main.now.NowViewState.*
import soup.movie.util.log.printRenderLog
import soup.movie.util.setVisibleIf
import javax.inject.Inject

class NowFragment :
        BaseTabFragment<NowContract.View, NowContract.Presenter>(),
        NowContract.View, BaseTabFragment.OnReselectListener {

    @Inject
    override lateinit var presenter: NowContract.Presenter

    private val listAdapter by lazy {
        NowListAdapter(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            FragmentVerticalListBinding.inflate(inflater, container, false).root

    override fun initViewState(ctx: Context) {
        super.initViewState(ctx)
        listView.apply {
            adapter = listAdapter
            itemAnimator = SlideInUpAnimator().apply {
                addDuration = 200
                removeDuration = 200
            }
        }
        swipeRefreshLayout.setOnRefreshListener {
            presenter.refresh()
        }
        errorView.setOnClickListener {
            presenter.refresh()
        }
    }

    override fun render(viewState: NowViewState) {
        printRenderLog { viewState }
        swipeRefreshLayout?.isRefreshing = viewState is LoadingState
        errorView?.setVisibleIf { viewState is ErrorState }
        if (viewState is DoneState) {
            listAdapter.submitList(viewState.movies)
        }
    }

    override fun onReselect() {
        listView?.smoothScrollToPosition(0)
    }

    companion object {

        fun newInstance(): NowFragment = NowFragment()
    }
}
