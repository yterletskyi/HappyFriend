package com.yterletskyi.happyfriend.features.settings.ui

import com.yterletskyi.happyfriend.common.list.AdapterDelegate
import com.yterletskyi.happyfriend.common.list.ModelItem
import com.yterletskyi.happyfriend.databinding.ItemSettingsTextBinding
import com.yterletskyi.happyfriend.features.settings.domain.VersionModelItem

class TextSettingAdapter : AdapterDelegate<ItemSettingsTextBinding>(
    ItemSettingsTextBinding::inflate
) {

    override fun onBindViewHolder(viewHolder: Holder<ItemSettingsTextBinding>, item: ModelItem) {
        item as VersionModelItem
        with(viewHolder.binding) {
            versionTitle.text = item.title
            appVersion.text = item.appVersion
        }
    }

    override fun isForViewType(item: ModelItem, position: Int): Boolean {
        return item is VersionModelItem
    }

    override fun getViewType(): Int = APP_VERSION_VALUE_TYPE

    companion object {
        private const val APP_VERSION_VALUE_TYPE = 2
    }
}
