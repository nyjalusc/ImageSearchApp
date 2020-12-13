package com.codinginflow.imagesearchapp.ui.gallery

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.codinginflow.imagesearchapp.R

class GalleryFragment : Fragment(R.layout.fragment_gallery) {
    private val viewModel by viewModels<GalleryViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Pass `viewLifecycleOwner` and not `this` because we want to stop observing
        // when the view of fragment is destroyed. It can exist in memory once its view is
        // destroyed eg. when the fragment is placed in backstack.
        viewModel.photos.observe(viewLifecycleOwner)  {

        }
    }
}