package com.codinginflow.imagesearchapp.data

import androidx.paging.PagingSource
import com.codinginflow.imagesearchapp.api.UnsplashApi
import retrofit2.HttpException
import java.io.IOException

private const val UNSPLASH_STARTING_PAGE_INDEX = 1

class UnsplashPagingSource(
    private val unsplashApi: UnsplashApi,
    private val query: String
) : PagingSource<Int, UnsplashPhoto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val currentPagePosition = params.key ?: UNSPLASH_STARTING_PAGE_INDEX // First page if params.key is null


        return try {
            val response = unsplashApi.searchPhotos(query, currentPagePosition, params.loadSize)
            val photos = response.results
            LoadResult.Page(
                data = photos,
                // If current page is the first page null else pos - 1
                prevKey = if (currentPagePosition == UNSPLASH_STARTING_PAGE_INDEX) null else currentPagePosition - 1,
                // if no more photos found null otherwise pos + 1
                nextKey = if (photos.isEmpty()) null else currentPagePosition + 1,
            )
        } catch (exception: IOException) {
            // Thrown by Retrofit or OkHttp
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            // Comes from the server
            LoadResult.Error(exception)
        }
    }
}