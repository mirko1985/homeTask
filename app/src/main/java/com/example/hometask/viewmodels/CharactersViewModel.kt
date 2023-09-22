package com.example.hometask.viewmodels

import androidx.lifecycle.ViewModel
import androidx.paging.*
import com.example.hometask.AppModule
import com.example.hometask.repositories.RickAndMortyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(private val rickAndMortyRepository: RickAndMortyRepository) :
    ViewModel() {

    data class CharacterInfo(
        val id: Int,
        val image: String,
        val name: String,
        val species: String,
        val status: String,
        val type: String,
    )

    @OptIn(ExperimentalPagingApi::class)
    val charactersInfoList = Pager(
        config = PagingConfig(AppModule.PAGE_SIZE, 0),
        remoteMediator = RickAndMortyRepository.CharactersDataRemoteMediator(
            rickAndMortyRepository.rickAndMortyService,
            rickAndMortyRepository.rickAndMortyDB
        )
    ) {
        rickAndMortyRepository.rickAndMortyDB.charactersDao().getCharactersPaged()
    }.flow.flowOn(Dispatchers.IO).map { page ->
        page.map {
            CharacterInfo(it.id, it.image, it.name, it.species, it.status, it.type)
        }
    }
}