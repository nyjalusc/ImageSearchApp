package com.codinginflow.imagesearchapp.ui.gallery

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.codinginflow.imagesearchapp.R
import com.codinginflow.imagesearchapp.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery) {
    private val viewModel by viewModels<GalleryViewModel>()

    // This is to dereference the binding object once view is destroyed
    private var _binding: FragmentGalleryBinding? = null

    // This is to avoid using the safe call operator all the time
    // If its used outside view lifecycle it will throw NPE
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentGalleryBinding.bind(view)

        val adapter = UnsplashPhotoAdapter()
        binding.apply {
            // Our RV itself is not going to change its height and width
            recyclerView.setHasFixedSize(true)

            // retry() is a function from Paging Adapter that will retry the failed requests
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = UnsplashPhotoLoadStateAdapter { adapter.retry() },
                footer = UnsplashPhotoLoadStateAdapter { adapter.retry() }
            )
        }

        // Pass `viewLifecycleOwner` and not `this` because we want to stop observing
        // when the view of fragment is destroyed. It can exist in memory once its view is
        // destroyed eg. when the fragment is placed in backstack.
        viewModel.photos.observe(viewLifecycleOwner)  {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        // Enable options menu
        setHasOptionsMenu(true)
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