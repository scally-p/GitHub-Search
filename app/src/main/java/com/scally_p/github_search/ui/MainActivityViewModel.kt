package com.scally_p.github_search.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.scally_p.github_search.data.local.repository.RepositoriesRepository
import com.scally_p.github_search.ui.data.UiAction
import com.scally_p.github_search.ui.data.UiModel
import com.scally_p.github_search.ui.data.UiState
import com.scally_p.github_search.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: RepositoriesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val state: StateFlow<UiState>

    val accept: (UiAction) -> Unit

    init {
        val initialQuery: String =
            savedStateHandle[Constants.Api.LAST_SEARCH_QUERY] ?: Constants.Api.DEFAULT_QUERY
        val lastQueryScrolled: String =
            savedStateHandle[Constants.Api.LAST_QUERY_SCROLLED] ?: Constants.Api.DEFAULT_QUERY
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(query = initialQuery)) }
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }

        state = searches
            .flatMapLatest { search ->
                combine(
                    queriesScrolled,
                    searchRepositories(queryString = search.query),
                    ::Pair
                )
                    .distinctUntilChangedBy { it.second }
                    .map { (scroll, pagingData) ->
                        UiState(
                            query = search.query,
                            pagingData = pagingData,
                            lastQueryScrolled = scroll.currentQuery,
                            // If the search query matches the scroll query, the user has scrolled
                            hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
                        )
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun searchRepositories(queryString: String): Flow<PagingData<UiModel>> {
        println("Check --- here 11")
        return repository.getSearchResultStream(queryString)
            .map { pagingData -> pagingData.map { UiModel.RepositoryItem(it) } }
            .map {
                it.insertSeparators { before, after ->
                    if (after == null) {
                        return@insertSeparators null
                    }

                    if (before == null) {
                        return@insertSeparators null
                    }

                    UiModel.SeparatorItem("")
                }
            }
            .cachedIn(viewModelScope)
    }

    override fun onCleared() {
        savedStateHandle[Constants.Api.LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[Constants.Api.LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }
}