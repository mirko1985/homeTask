package com.example.hometask.repositories

import androidx.paging.*
import androidx.room.withTransaction
import com.example.hometask.AppModule.PAGE_SIZE
import com.example.hometask.data.CharacterData
import com.example.hometask.data.database.RickAndMortyServiceDatabase
import com.example.hometask.network.RickAndMortyService
import javax.inject.Inject

class RickAndMortyRepository @Inject constructor(
    internal val rickAndMortyService: RickAndMortyService,
    internal val rickAndMortyDB: RickAndMortyServiceDatabase
) {

    @OptIn(ExperimentalPagingApi::class)
    class CharactersDataRemoteMediator(
        private val rickAndMortyService: RickAndMortyService,
        private val rickAndMortyDB: RickAndMortyServiceDatabase
    ) : RemoteMediator<Int, CharacterData>() {
        override suspend fun load(
            loadType: LoadType,
            state: PagingState<Int, CharacterData>
        ): MediatorResult {
            return try {

                val page = when (loadType) {
                    LoadType.REFRESH -> (state.lastItemOrNull()?.id?.div(PAGE_SIZE) ?: 1) + 1
                    LoadType.PREPEND -> {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    LoadType.APPEND -> {
                        state.lastItemOrNull() ?: return MediatorResult.Success(
                            endOfPaginationReached = false
                        )
                        (state.lastItemOrNull()?.id?.div(PAGE_SIZE) ?: 1) + 1
                    }
                }

                val response = rickAndMortyService.getCharactersByPage(page)

                rickAndMortyDB.withTransaction {
                    if (loadType == LoadType.REFRESH)
                        rickAndMortyDB.charactersDao().deletePage(page)
                    rickAndMortyDB.charactersDao().insertAll(response.body()!!.results)
                }

                MediatorResult.Success(endOfPaginationReached = response.body()!!.info.next == "null")
            } catch (e: Exception) {
                MediatorResult.Error(e)
            }
        }

    }
}
