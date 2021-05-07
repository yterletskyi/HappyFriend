package com.yterletskyi.happy_friend.features.friends.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface FriendsDataSource {
    fun getFriends(): Flow<List<Friend>>
    suspend fun addFriend(friend: Friend)
    suspend fun removeFriend(contactId: Long)
    suspend fun isFriend(contactId: Long): Boolean
}

class InMemoryFriendsDataSource : FriendsDataSource {

    private val flow: MutableStateFlow<List<Friend>> = MutableStateFlow(emptyList())

    override fun getFriends(): Flow<List<Friend>> = flow

    override suspend fun addFriend(friend: Friend) {
        val friends = flow.value
        val newFriends = friends
            .toMutableList()
            .apply { add(friend) }
        flow.value = newFriends
    }

    override suspend fun removeFriend(contactId: Long) {
        val friends = flow.value
        val newFriends = friends
            .toMutableList()
            .apply { removeIf { it.contactId == contactId } }
        flow.value = newFriends
    }

    override suspend fun isFriend(contactId: Long): Boolean = flow.value
        .find { it.contactId == contactId } != null

}

class RoomFriendsDataSource(
    private val friendsDao: FriendsDao
) : FriendsDataSource {

    override fun getFriends(): Flow<List<Friend>> = friendsDao.getFriends()

    override suspend fun addFriend(friend: Friend) = friendsDao.addFriend(friend)

    override suspend fun removeFriend(contactId: Long) = friendsDao.removeFriend(contactId)

    override suspend fun isFriend(contactId: Long): Boolean = friendsDao.isFriend(contactId)

}