package com.yterletskyi.happy_friend.features.ideas.ui

import android.graphics.Paint
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.yterletskyi.happy_friend.common.list.AdapterDelegate
import com.yterletskyi.happy_friend.common.list.ModelItem
import com.yterletskyi.happy_friend.databinding.ItemIdeaBinding
import com.yterletskyi.happy_friend.features.ideas.domain.IdeaModelItem

class IdeasAdapterDelegate(
    private val onTextChanged: (Int, String) -> Unit,
    private val onCheckboxChanged: (Int, Boolean) -> Unit,
    private val onRemoveClicked: (Int) -> Unit
) : AdapterDelegate<ItemIdeaBinding>(
    ItemIdeaBinding::inflate
) {

    override fun onViewHolderCreated(viewHolder: Holder<ItemIdeaBinding>) {
        with(viewHolder) {
            binding.input.doAfterTextChanged {
                onTextChanged(adapterPosition, binding.input.text.toString())
            }
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                onCheckboxChanged(adapterPosition, isChecked)
                updateStrikethrough(binding.input, isChecked)
            }
            binding.remove.setOnClickListener {
                onRemoveClicked(adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: Holder<ItemIdeaBinding>, item: ModelItem) {
        item as IdeaModelItem

        with(viewHolder.binding) {
            checkbox.isChecked = item.done
            with(input) {
                setText(item.text)
                updateStrikethrough(this, item.done)
            }
        }
    }

    override fun isForViewType(item: ModelItem, position: Int): Boolean = item is IdeaModelItem

    override fun getViewType(): Int = 1

    private fun updateStrikethrough(view: TextView, isChecked: Boolean) {
        with(view) {
            paintFlags =
                if (isChecked) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

}