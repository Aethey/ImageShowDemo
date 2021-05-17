package com.example.imagelisttest.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.imagelisttest.Config
import com.example.imagelisttest.api.PhotoService
import com.example.imagelisttest.model.Photo
import retrofit2.HttpException
import java.io.IOException

/**
 * if query is null find photos else search photos
 */
class PhotoPagingSource constructor(private val service: PhotoService) :
    PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        // if(page null default 1)
        val position = params.key ?: 1

        return try {
            val response = service.getListPhotos(position, params.loadSize, "latest")
            val nextKey = if (response.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / Config.NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = response,
                prevKey = if (position == 1) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    // The refresh key is used for the initial load of the next PagingSource, after invalidation
    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {

        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}