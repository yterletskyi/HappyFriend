package com.yterletskyi.happyfriend.features.friends.ui

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yterletskyi.happyfriend.R
import com.yterletskyi.happyfriend.common.binding.BaseBindingFragment
import com.yterletskyi.happyfriend.common.list.RecyclerDelegationAdapter
import com.yterletskyi.happyfriend.common.list.SpaceItemDecoration
import com.yterletskyi.happyfriend.common.x.dp
import com.yterletskyi.happyfriend.databinding.FragmentFriendsBinding
import com.yterletskyi.happyfriend.features.friends.domain.FriendModelItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendsFragment : BaseBindingFragment<FragmentFriendsBinding>(
    FragmentFriendsBinding::inflate
) {

    private val viewModel by viewModels<FriendsViewModel>()
    private lateinit var rvItemsAdapter: RecyclerDelegationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissions()

        with(binding.rvItems) {
            layoutManager = LinearLayoutManager(context)
            adapter = RecyclerDelegationAdapter(context).apply {
                addDelegate(
                    FriendsAdapterDelegate(
                        onItemClicked = ::showIdeasScreen,
                        onItemLongClicked = ::showActionsDialog
                    )
                )
                rvItemsAdapter = this
                addItemDecoration(
                    SpaceItemDecoration(space = 4.dp)
                )
            }
        }

        with(binding.toolbar) {
            onActionClicked = { showSearchFragment() }
        }

        with(binding.incEmptyState.btnAddFriend) {
            setOnClickListener { showSearchFragment() }
        }

        viewModel.showEmptyState.observe(viewLifecycleOwner) {
            binding.incEmptyState.root.isVisible = it
            binding.rvItems.isVisible = !it
        }
    }

    private fun requestPermissions() {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                viewModel.friends.observe(viewLifecycleOwner, rvItemsAdapter::setItems)
            } else {
                Toast.makeText(context, "Please grant permission", Toast.LENGTH_SHORT).show()
            }
        }.launch(Manifest.permission.READ_CONTACTS)
    }

    private fun showIdeasScreen(index: Int) {
        val friend = rvItemsAdapter.getItemTyped<FriendModelItem>(index)
        findNavController().navigate(
            FriendsFragmentDirections.toIdeasScreen(friend.id)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.friends_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                showSearchFragment(); true
            }
            else -> false
        }
    }

    private fun showSearchFragment() {
        findNavController().navigate(
            FriendsFragmentDirections.toContactsScreen()
        )
    }

    private fun showActionsDialog(index: Int) = context?.let {
        AlertDialog.Builder(it)
            .setItems(R.array.friend_actions) { _, which ->
                when (which) {
                    0 -> viewModel.removeFriend(index)
                    else -> throw IllegalArgumentException("Action $which is not supported")
                }
            }
            .show()
    }
}