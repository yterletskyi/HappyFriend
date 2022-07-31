package com.yterletskyi.happyfriend.features.contacts.di

import android.content.Context
import com.yterletskyi.happyfriend.common.BirthdayFormatter
import com.yterletskyi.happyfriend.common.BirthdayParser
import com.yterletskyi.happyfriend.features.contacts.data.ContactsDataSource
import com.yterletskyi.happyfriend.features.contacts.data.FetchBirthdaysOnInitContactsDataSource
import com.yterletskyi.happyfriend.features.contacts.data.TimeMeasuredContactsDataSource
import com.yterletskyi.happyfriend.features.contacts.domain.ContactsInteractor
import com.yterletskyi.happyfriend.features.contacts.domain.ContactsInteractorImpl
import com.yterletskyi.happyfriend.features.friends.data.FriendsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class, FragmentComponent::class)
class ContactsDi {

    @Provides
    fun provideContactsDataSource(
        @ApplicationContext context: Context,
        birthdayParser: BirthdayParser
    ): ContactsDataSource {
        return TimeMeasuredContactsDataSource(
            FetchBirthdaysOnInitContactsDataSource(context, birthdayParser)
        )
    }

    @Provides
    fun provideContactsInteractor(
        @ApplicationContext context: Context,
        contactsDataSource: ContactsDataSource,
        friendsDataSource: FriendsDataSource,
        birthdayFormatter: BirthdayFormatter
    ): ContactsInteractor {
        return ContactsInteractorImpl(
            context,
            contactsDataSource,
            friendsDataSource,
            birthdayFormatter
        )
    }

    @Provides
    fun provideBirthdayParser(): BirthdayParser = BirthdayParser()
}