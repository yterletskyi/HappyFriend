package com.yterletskyi.happyfriend.common.di

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Room
import com.yterletskyi.happyfriend.App
import com.yterletskyi.happyfriend.common.BirthdayFormatter
import com.yterletskyi.happyfriend.common.LocalizedBirthdayFormatter
import com.yterletskyi.happyfriend.common.analytics.Analytics
import com.yterletskyi.happyfriend.common.analytics.CompoundAnalytics
import com.yterletskyi.happyfriend.common.analytics.ToLogAnalytics
import com.yterletskyi.happyfriend.common.data.AppDatabase
import com.yterletskyi.happyfriend.features.friends.data.FriendsDao
import com.yterletskyi.happyfriend.features.friends.data.FriendsDataSource
import com.yterletskyi.happyfriend.features.friends.data.RoomFriendsDataSource
import com.yterletskyi.happyfriend.features.ideas.data.IdeasDao
import com.yterletskyi.happyfriend.features.ideas.data.IdeasDataSource
import com.yterletskyi.happyfriend.features.ideas.data.RoomIdeasDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton

typealias OurFirebaseAnalytics = com.yterletskyi.happyfriend.common.analytics.FirebaseAnalytics
typealias TheirFirebaseAnalytics = com.google.firebase.analytics.FirebaseAnalytics

@Module
@InstallIn(SingletonComponent::class)
object CommonDi {

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
    @Singleton
    fun provideFriendsDataSource(friendsDao: FriendsDao): FriendsDataSource {
        return RoomFriendsDataSource(friendsDao)
    }

    @Provides
    @Singleton
    fun provideIdeasDataSource(ideasDao: IdeasDao): IdeasDataSource {
        return RoomIdeasDataSource(ideasDao)
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

    @Provides
    fun provideToLogAnalytics(): ToLogAnalytics {
        return ToLogAnalytics()
    }

    @Provides
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): TheirFirebaseAnalytics {
        return TheirFirebaseAnalytics.getInstance(context)
    }

    @Provides
    fun provideAnalytics(
        toLogAnalytics: ToLogAnalytics,
        theirFirebaseAnalytics: TheirFirebaseAnalytics,
    ): Analytics {
        val ourAnalytics = OurFirebaseAnalytics(theirFirebaseAnalytics)
        return CompoundAnalytics(ourAnalytics, toLogAnalytics)
    }
}