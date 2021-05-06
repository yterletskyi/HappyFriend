package com.yterletskyi.happy_friend.features.friends.ui

import com.yterletskyi.happy_friend.common.list.AdapterDelegate
import com.yterletskyi.happy_friend.common.list.ModelItem
import com.yterletskyi.happy_friend.databinding.ItemFriendBinding
import com.yterletskyi.happy_friend.features.friends.domain.FriendModelItem

class FriendsAdapterDelegate(
    private val onItemClicked: (Int) -> Unit
) : AdapterDelegate<ItemFriendBinding>(
    ItemFriendBinding::inflate
) {

    override fun onViewHolderCreated(viewHolder: Holder<ItemFriendBinding>) {
        with(viewHolder) {
            binding.root.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: Holder<ItemFriendBinding>, item: ModelItem) {
        item as FriendModelItem

        viewHolder.binding.image.setImageDrawable(item.image)
        viewHolder.binding.text.text = item.fullName
        viewHolder.binding.birthday.text = item.birthday.toString()
    }

    override fun isForViewType(item: ModelItem, position: Int): Boolean = item is FriendModelItem

    override fun getViewType(): Int = 1
}