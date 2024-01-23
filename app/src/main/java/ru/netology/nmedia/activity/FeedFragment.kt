package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


class FeedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(layoutInflater, container, false)

        val viewModel: PostViewModel by activityViewModels()

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post)
            }

            override fun onShare(post: Post) {
                viewModel.shareById(post.id)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val chooser = Intent.createChooser(intent, null)
                startActivity(chooser)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                findNavController()
                    .navigate(
                        R.id.action_feedFragment_to_editPostFragment,
                        Bundle().apply {
                            putString("EXTRA_CONTENT", post.content)
                            putLong("EXTRA_ID", post.id)
                        }
                    )
                viewModel.edit(post)
            }

            override fun onPost(post: Post) {
                findNavController()
                    .navigate(
                        R.id.action_feedFragment_to_postDetailsFragment,
                        Bundle().apply { putLong("EXTRA_ID", post.id) })
            }

            override fun onPhoto(post: Post) {
                if (post.attachment == null) return
                findNavController()
                    .navigate(
                        R.id.action_feedFragment_to_photoFragment,
                        Bundle().apply { putString("EXTRA_URI", post.attachment?.url) })
            }
        }
        )
        with(binding) {

            list.adapter = adapter

            viewModel.dataState.observe(viewLifecycleOwner) { state ->
                progress.isVisible = state.loading
                swiperefresh.isRefreshing = state.refreshing
                if (state.error) {
                    Snackbar.make(root, "Error loading", Snackbar.LENGTH_LONG)
                        .setAnchorView(binding.addNewPostButton)
                        .setAction(R.string.retry_loading) { viewModel.load() }
                        .show()
                }
            }
            viewModel.data.observe(viewLifecycleOwner) { state ->
                val isNewPost = state.posts.size > adapter.currentList.size && adapter.itemCount > 0
                adapter.submitList(state.posts) {
                    if (isNewPost) list.smoothScrollToPosition(0)
                }
                emptyText.isVisible = state.empty
            }

            viewModel.newerCount.observe(viewLifecycleOwner) { count ->
                if (count > 0) {
                    getNewerPosts.visibility = View.VISIBLE
                } else getNewerPosts.visibility = View.GONE

                getNewerPosts.setOnClickListener {
                    viewModel.getNewer(count.toLong())
                    getNewerPosts.visibility = View.GONE
                }
            }

            swiperefresh.setOnRefreshListener {
                viewModel.refreshPosts()
            }

            addNewPostButton.setOnClickListener {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }
            return root
        }
    }
}