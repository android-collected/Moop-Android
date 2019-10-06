package soup.movie.ui.detail

import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.ext.AlwaysDiffCallback
import soup.movie.BR
import soup.movie.R
import soup.movie.ui.databinding.DataBindingListAdapter
import soup.movie.ui.databinding.DataBindingViewHolder

internal class DetailListAdapter(
    itemClickListener: (ContentItemUiModel) -> Unit
) : DataBindingListAdapter<ContentItemUiModel>(AlwaysDiffCallback()) {

    private val itemClickListener = DetailListItemListener(itemClickListener)

    override fun onBindViewHolder(holder: DataBindingViewHolder<ContentItemUiModel>, position: Int) {
        if (position == 0 && headerHeight > 0) {
            holder.binding.root.updateLayoutParams {
                height = headerHeight
            }
        }
        holder.binding.setVariable(BR.listener, itemClickListener)
        super.onBindViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is HeaderItemUiModel -> R.layout.detail_item_header
        is BoxOfficeItemUiModel -> R.layout.detail_item_box_office
        is CgvItemUiModel -> R.layout.detail_item_cgv
        is LotteItemUiModel -> R.layout.detail_item_lotte
        is MegaboxItemUiModel -> R.layout.detail_item_megabox
        is NaverItemUiModel -> R.layout.detail_item_naver
        is ImdbItemUiModel -> R.layout.detail_item_imdb
        is TrailerHeaderItemUiModel -> R.layout.detail_item_trailer_header
        is TrailerItemUiModel -> R.layout.detail_item_trailer
        is TrailerFooterItemUiModel -> R.layout.detail_item_trailer_footer
    }

    fun getSpanSize(position: Int): Int = when (getItem(position)) {
        is CgvItemUiModel,
        is LotteItemUiModel,
        is MegaboxItemUiModel -> 1
        else -> 3
    }

    private var headerHeight: Int = 0

    fun updateHeader(height: Int) {
        headerHeight = height
        notifyItemChanged(0)
    }
}

class DetailListItemListener(
    private val _onInfoClick: (ContentItemUiModel) -> Unit
) {

    fun onInfoClick(item: ContentItemUiModel?) {
        if (item != null) {
            _onInfoClick(item)
        }
    }
}
