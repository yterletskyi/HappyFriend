package com.yterletskyi.happy_friend.features.contacts.data


import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.core.net.toUri
import com.yterletskyi.happy_friend.common.BirthdayParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

interface ContactsDataSource {
    val contactsFlow: Flow<List<Contact>>
    fun search(query: String = "")
}

class FakeContactsDataSource : ContactsDataSource {

    private val list = listOf(
        Contact(
            id = 1,
            imageUri = null,
            name = "Yura Basa",
            birthday = LocalDate.of(1995, 6, 30)
        ),
        Contact(
            id = 2,
            imageUri = null,
            name = "Ostap Holub",
            birthday = LocalDate.of(1997, 2, 6)
        ),
        Contact(
            id = 3,
            imageUri = null,
            name = "Andre Yaniv",
            birthday = LocalDate.of(1997, 1, 10)
        ),
        Contact(
            id = 4,
            imageUri = null,
            name = "Taras Smakula",
            birthday = LocalDate.of(1997, 2, 9)
        )
    )

    private val _contactsFlow: MutableStateFlow<List<Contact>> = MutableStateFlow(list)
    override val contactsFlow: Flow<List<Contact>> = _contactsFlow

    override fun search(query: String) {
        _contactsFlow.value = list
    }
}

class PhoneContactsDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val birthdayParser: BirthdayParser,
) : ContactsDataSource {

    private val _contactsFlow: MutableStateFlow<List<Contact>> = MutableStateFlow(emptyList())
    override val contactsFlow: Flow<List<Contact>> = _contactsFlow

    init {
        search("")
    }

    override fun search(query: String) {
        val uri = when {
            query.isBlank() -> ContactsContract.Contacts.CONTENT_URI
            else -> Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, query)
        }
        val list = mutableListOf<Contact>()
        context.contentResolver.query(
            uri,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
            ),
            "display_name is not null",
            null,
            "display_name"
        )?.use { cursor ->
            while (cursor.moveToNext()) {

                var contact = cursorToContact(cursor)

                context.contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Event.START_DATE),
                    "${ContactsContract.Data.CONTACT_ID}=? AND ${ContactsContract.Data.MIMETYPE}=? AND ${ContactsContract.CommonDataKinds.Event.TYPE}=${ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY}",
                    arrayOf(
                        contact.id.toString(),
                        ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
                    ),
                    null,
                )?.use {
                    Log.i("info23", "fetching birthday for ${contact.name}")
                    while (it.moveToNext()) {
                        val birthdayStr = it.getString(0)
                        Log.i("info24", "${contact.name} birthday is: $birthdayStr")
                        contact = contact.copy(birthday = birthdayParser.parse(birthdayStr))
                    }
                }

                list.add(contact)
            }
        }

        _contactsFlow.value = list
    }

    private fun cursorToContact(cursor: Cursor): Contact {
        return Contact(
            id = cursor.getLong(0),
            name = cursor.getString(1),
            imageUri = cursor.getString(2)?.toUri(),
//            birthday = cursor.getString(3)?.let(birthdayParser::parse),
            birthday = null
        )
    }

}

@OptIn(ExperimentalTime::class)
class TimeMeasuredContactsDataSource(
    private val impl: ContactsDataSource
) : ContactsDataSource by impl {

    override fun search(query: String) {
        val time = measureTime { impl.search(query) }
        Log.i("info23", "getContacts with query=<$query> took <${time.inSeconds}> sec")
    }
}