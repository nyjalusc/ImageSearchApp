package com.codinginflow.imagesearchapp.ui.gallery

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingSource
import com.codinginflow.imagesearchapp.R
import com.codinginflow.imagesearchapp.data.UnsplashPhoto
import com.codinginflow.imagesearchapp.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery), UnsplashPhotoAdapter.OnItemClickListener {
    private val viewModel: GalleryViewModel by viewModels()

    // This is to dereference the binding object once view is destroyed
    private var _binding: FragmentGalleryBinding? = null

    // This is to avoid using the safe call operator all the time
    // If its used outside view lifecycle it will throw NPE
    // If you remove `get()` it will cause a crash because _binding will get assigned
    // at the time of instantiation, the local var _binding will have a null value at that time
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View is already inflated since we passed the layout in Fragment constructor
        // We just have to bind the view with the Binding object
        _binding = FragmentGalleryBinding.bind(view)

        val adapter = UnsplashPhotoAdapter(this)
        binding.apply {
            // Our RV itself is not going to change its height and width
            recyclerView.setHasFixedSize(true)

            // retry() is a function from Paging Adapter that will retry the failed requests
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = UnsplashPhotoLoadStateAdapter { adapter.retry() },
                footer = UnsplashPhotoLoadStateAdapter { adapter.retry() }
            )

            buttonRetry.setOnClickListener { adapter.retry() }
        }

        // Pass `viewLifecycleOwner` and not `this` because we want to stop observing
        // when the view of fragment is destroyed. It can exist in memory once its view is
        // destroyed eg. when the fragment is placed in backstack.
        viewModel.photos.observe(viewLifecycleOwner)  {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                // CombinedLoadStates has 2 properties - source and mediator, here mediator is a [RemoteMediator] used for
                // implementing paging based on multiple sources such as (Room (local source) + Network (remote source)). RemoteMediator
                // will be responsible for executing some callbacks when certain conditions are met.
                // Each of these LoadStates represent a combination of 3 separate properties: prepend, append and refresh
                // Prepend - represents pages being added at pos N - 1, N - 2..1, Append is the opposite (N + 1, N + 2 ...)
                // And refresh represents the whole PagingSource. This is why we care only about `refresh` properties LoadingState.
                progressBar.isVisible = combinedLoadStates.source.refresh is LoadState.Loading
                recyclerView.isVisible = combinedLoadStates.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = combinedLoadStates.source.refresh is LoadState.Error
                textViewError.isVisible = combinedLoadStates.source.refresh is LoadState.Error

                // Empty view - Could be because we have reached end of pagination or we don't have any items to show
                // Added LoadState.NotLoading so that we do not show the empty view during first load.
                if (combinedLoadStates.source.refresh is LoadState.NotLoading &&
                        combinedLoadStates.append.endOfPaginationReached &&
                        adapter.itemCount < 1) {
                    recyclerView.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }
            }
        }

        // Enable options menu
        setHasOptionsMenu(true)
    }

    override fun onItemClick(photo: UnsplashPhoto) {
        // This direction class is created by SafeArgs plugin of Navigation component.
        // Build the project after you update nav_graph
        // Provides compile time safety, Bundles do not!
        val action = GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(photo)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_gallery, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    // Upon submit move the recyclerview to position 0
                    binding.recyclerView.scrollToPosition(0)
                    viewModel.searchPhotos(query)
                    // Closes keyboard
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // We handled the action, don't do anything else
                return true
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
