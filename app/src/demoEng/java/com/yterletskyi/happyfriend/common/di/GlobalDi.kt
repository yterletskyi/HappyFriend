package com.yterletskyi.happyfriend.common.di

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Room
import com.yterletskyi.happyfriend.App
import com.yterletskyi.happyfriend.common.BirthdayFormatter
import com.yterletskyi.happyfriend.common.LocalizedBirthdayFormatter
import com.yterletskyi.happyfriend.common.data.AppDatabase
import com.yterletskyi.happyfriend.features.contacts.data.ContactsDataSource
import com.yterletskyi.happyfriend.features.contacts.data.DemoContactsDataSource
import com.yterletskyi.happyfriend.features.friends.data.DemoFriendsDataSource
import com.yterletskyi.happyfriend.features.friends.data.FriendsDao
import com.yterletskyi.happyfriend.features.friends.data.FriendsDataSource
import com.yterletskyi.happyfriend.features.ideas.data.DemoIdeasDataSource
import com.yterletskyi.happyfriend.features.ideas.data.IdeasDao
import com.yterletskyi.happyfriend.features.ideas.data.IdeasDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GlobalDi {

    @Provides
    @Singleton
    fun provideApp(@ApplicationContext context: Context): App = context as App

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "happy-friend")
            .addCallback(PrepopulateMyWishlistFriend())
            .addCallback(PrepopulateGeneralIdeasList())
            .build()
    }

    @Provides
    fun provideIdeasDao(database: AppDatabase): IdeasDao {
        return database.ideasDao
    }

    @Provides
    fun provideFriendsDao(database: AppDatabase): FriendsDao {
        return database.friendsDao
    }

    @Provides
    fun provideContactsDataSource(
        @ApplicationContext context: Context,
    ): ContactsDataSource {
        return DemoContactsDataSource(context)
    }

    @Provides
    @Singleton
    fun provideFriendsDataSource(
        contactsDataSource: ContactsDataSource,
    ): FriendsDataSource {
        return DemoFriendsDataSource(contactsDataSource)
    }

    @Provides
    @Singleton
    fun provideIdeasDataSource(): IdeasDataSource {
        return DemoIdeasDataSource()
    }

    @Provides
    fun provideBirthdayFormatter(): BirthdayFormatter =
        LocalizedBirthdayFormatter(Locale.getDefault())

    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver =
        context.contentResolver

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}
