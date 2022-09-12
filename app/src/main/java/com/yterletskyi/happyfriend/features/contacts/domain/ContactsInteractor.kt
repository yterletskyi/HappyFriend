package com.yterletskyi.happyfriend.features.contacts.domain

import com.yterletskyi.happyfriend.common.BirthdayFormatter
import com.yterletskyi.happyfriend.common.LifecycleComponent
import com.yterletskyi.happyfriend.common.drawable.AvatarInitialsDrawable
import com.yterletskyi.happyfriend.common.drawable.RoundDrawable
import com.yterletskyi.happyfriend.features.contacts.data.ContactsDataSource
import com.yterletskyi.happyfriend.features.contacts.data.initials
import com.yterletskyi.happyfriend.features.friends.data.FriendsDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

interface ContactsInteractor : LifecycleComponent {
    val contactsFlow: Flow<List<ContactModelItem>>
    fun search(query: String = "")
}

class ContactsInteractorImpl @Inject constructor(
    private val contactsDataSource: ContactsDataSource,
    private val friendsDataSource: FriendsDataSource,
    private val birthdayFormatter: BirthdayFormatter
) : ContactsInteractor {

    override val contactsFlow: Flow<List<ContactModelItem>> = combine(
        contactsDataSource.contactsFlow,
        friendsDataSource.friendsFlow
    ) { contacts, friends ->
        contacts
            .map { co ->
                ContactModelItem(
                    id = co.id,
                    image = RoundDrawable(
                        co.image ?: AvatarInitialsDrawable(co.initials)
                    ),
                    fullName = co.name,
                    birthday = birthdayFormatter.format(co.birthday),
                    isFriend = friends.find { fr -> fr.contactId == co.id } != null
                )
            }
    }

    override fun initialize() {
        contactsDataSource.initialize()
    }

    override fun search(query: String) = contactsDataSource.search(query)

    override fun destroy() {}
}
