package com.yterletskyi.happyfriend.features.ideas.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.yterletskyi.happyfriend.App
import com.yterletskyi.happyfriend.common.binding.BaseBindingFragment
import com.yterletskyi.happyfriend.common.list.RecyclerDelegationAdapter
import com.yterletskyi.happyfriend.databinding.FragmentIdeasBinding
import com.yterletskyi.happyfriend.features.friends.domain.FriendsInteractor
import com.yterletskyi.happyfriend.features.ideas.domain.IdeasDiffUtil
import com.yterletskyi.happyfriend.features.ideas.domain.IdeasInteractor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class IdeasFragment : BaseBindingFragment<FragmentIdeasBinding>(
    FragmentIdeasBinding::inflate
) {

    @Inject
    lateinit var app: App

    @Inject
    lateinit var ideasInteractor: IdeasInteractor

    @Inject
    lateinit var friendsInteractor: FriendsInteractor

    private val viewModel by viewModels<IdeasViewModel>()

    private lateinit var rvItemsAdapter: RecyclerDelegationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.rvItems) {
            adapter = RecyclerDelegationAdapter(context).apply {
                addDelegate(
                    IdeasAdapterDelegate(
                        onTextChanged = { i, t -> viewModel.updateIdea(i, t.trim()) },
                        onCheckboxChanged = { i, c -> viewModel.updateIdea(i, c) },
                        onRemoveClicked = { i -> viewModel.removeIdea(i) },
                        onNewIdeaClicked = viewModel::addIdea
                    )
                )
                addDelegate(
                    AddIdeaAdapterDelegate(
                        onItemClicked = viewModel::addIdea
                    )
                )
                rvItemsAdapter = this
            }
            layoutManager = LinearLayoutManager(context)
        }

        with(binding.toolbar) {
            onBackClicked = { findNavController().popBackStack() }
        }

        viewModel.title.observe(viewLifecycleOwner) {
            binding.toolbar.title = it
        }

        viewModel.ideasLiveData.observe(viewLifecycleOwner) {
            val diffUtil = IdeasDiffUtil(
                oldList = rvItemsAdapter.getData(),
                newList = it
            )
            rvItemsAdapter.setItemsWithDiff(it, diffUtil)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}