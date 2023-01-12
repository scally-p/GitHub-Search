package com.scally_p.github_search.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scally_p.github_search.data.local.repository.RepositoriesRepository
import com.scally_p.github_search.model.Repository
import com.scally_p.github_search.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: RepositoriesRepository,
) : ViewModel() {

    private val tag: String = MainActivityViewModel::class.java.name

    private val repositoriesEmitter = MutableLiveData<MutableList<Repository>>()
    private val errorMessage = MutableLiveData<String>()
    private val loading = MutableLiveData<Boolean>()
    private var mTotalCount = 0
    private var mCurrentCount = 0
    private var mCurrentPage = 0

    var totalCount: Int
        get() {
            return mTotalCount
        }
        set(value) {
            mTotalCount = value
        }

    var currentCount: Int
        get() {
            return mCurrentCount
        }
        set(value) {
            mCurrentCount = value
        }

     var currentPage: Int
        get() {
            return mCurrentPage
        }
        set(value) {
            mCurrentPage = value
        }

    var repositories: MutableList<Repository>
        get() {
            return repositoriesEmitter.value ?: ArrayList()
        }
        set(repositories) {
            repositoriesEmitter.value = repositories
        }

    init {
        searchRepositories(Constants.Api.DEFAULT_QUERY)
    }

    fun reset() {
        mTotalCount = 0
        mCurrentCount = 0
        mCurrentPage = 0
        println("size size -- 1: ${repositoriesEmitter.value?.size}")
        repositoriesEmitter.value = emptyList<Repository>().toMutableList()
        println("size size -- 2: ${repositoriesEmitter.value?.size}")
    }

    fun searchRepositories(query: String?) {
        if (query.isNullOrEmpty()) return
        loading.value = true

        viewModelScope.launch {
            val result = repository.searchRepositories(query, currentPage + 1)
            if (result.isSuccess) {
                totalCount = result.getOrNull()?.totalCount ?: 0
                currentPage += 1
                repositories = result.getOrNull()?.items ?: ArrayList()
                loading.value = false
            } else {
                result.exceptionOrNull()?.printStackTrace()
                onError(
                    "Message: ${result.exceptionOrNull()?.message}\nLocalizedMessage: ${result.exceptionOrNull()?.localizedMessage}"
                )
            }
        }
    }

    fun observeRepositoriesLiveData(): MutableLiveData<MutableList<Repository>> {
        return repositoriesEmitter
    }

    fun observeErrorMessage(): MutableLiveData<String> {
        return errorMessage
    }

    fun observeLoading(): MutableLiveData<Boolean> {
        return loading
    }

    private fun onError(message: String) {
        errorMessage.postValue(message)
        loading.postValue(false)
    }
}