package com.codinginflow.imagesearchapp.ui.gallery

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.codinginflow.imagesearchapp.data.UnsplashRepository

/**
 * Dagger Hilt has special annotation for ViewModel injection
 */
class GalleryViewModel @ViewModelInject constructor(
    private val repository: UnsplashRepository,
    @Assisted state: SavedStateHandle
) : ViewModel() {
    // Similar to saved state, this creates a LiveData from savedState. But it is fully backed by
    // savedState meaning we do not have to do anything special to put the latest value in the savedState
    // It happens automatically. This way we can persist state beyond process death.
    private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)

    val photos = currentQuery.switchMap { queryString ->
        // You cannot load from a PagingSource the same PagingData so we need to cache this page within
        // viewModel (in memory). This facilitates screen rotation, otherwise we'll get a crash
        repository.getSearchResults(queryString).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query
    }

    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val DEFAULT_QUERY = "cats"
    }
}