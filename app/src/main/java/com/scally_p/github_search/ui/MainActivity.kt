package com.scally_p.github_search.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scally_p.github_search.databinding.ActivityMainBinding
import com.scally_p.github_search.ui.adapter.RepositoryAdapter
import com.scally_p.github_search.ui.adapter.RepositoryLoadStateAdapter
import com.scally_p.github_search.ui.data.UiAction
import com.scally_p.github_search.ui.data.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val tag: String = MainActivity::class.java.name

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        bindState(
            uiState = viewModel.state,
            uiActions = viewModel.accept
        )
    }

    override fun onResume() {
        super.onResume()
        binding.shimmerFrameLayout.startShimmer()
    }

    override fun onPause() {
        binding.shimmerFrameLayout.stopShimmer()
        binding.shimmerFrameLayout.isVisible = false
        super.onPause()
    }

    private fun bindState(
        uiState: StateFlow<UiState>,
        uiActions: (UiAction) -> Unit
    ) {
        println("Check --- here 1")

        val repositoryAdapter = RepositoryAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = repositoryAdapter.withLoadStateHeaderAndFooter(
            header = RepositoryLoadStateAdapter { repositoryAdapter.retry() },
            footer = RepositoryLoadStateAdapter { repositoryAdapter.retry() }
        )
        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            uiState = uiState,
            onScrollChanged = uiActions,
            repositoryAdapter = repositoryAdapter
        )
    }

    private fun bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        println("Check --- here 2")

        binding.searchTxt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepositoriesFromInput(onQueryChanged)
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding.searchTxt.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepositoriesFromInput(onQueryChanged)
                hideKeyboard()
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            println("Check --- here 3")

            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(binding.searchTxt::setText)
        }
    }

    private fun updateRepositoriesFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        println("Check --- here 4")
        binding.searchTxt.text.trim().let {
            if (it.isNotEmpty()) {
                println("Check --- here 5")
                binding.recyclerView.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun bindList(
        uiState: StateFlow<UiState>,
        onScrollChanged: (UiAction.Scroll) -> Unit,
        repositoryAdapter: RepositoryAdapter,
    ) {
        println("Check --- here 6")

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = repositoryAdapter.loadStateFlow
            .distinctUntilChangedBy { it.refresh }
            .map { it.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        val pagingData = uiState
            .map { it.pagingData }
            .distinctUntilChanged()

        lifecycleScope.launch {
            println("Check --- here 7")

            combine(shouldScrollToTop, pagingData, ::Pair)
                .distinctUntilChangedBy { it.second }
                .collectLatest { (shouldScroll, pagingData) ->
                    repositoryAdapter.submitData(pagingData)
                    if (shouldScroll) binding.recyclerView.scrollToPosition(0)
                }
        }

        lifecycleScope.launch {
            println("Check --- here 8")

            repositoryAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && repositoryAdapter.itemCount == 0
                binding.emptyList.isVisible = isListEmpty
                binding.recyclerView.isVisible = !isListEmpty
                binding.shimmerFrameLayout.isVisible = loadState.source.refresh is LoadState.Loading
                binding.retry.isVisible = loadState.source.refresh is LoadState.Error

                println("Check --- here 9")

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        this@MainActivity,
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun hideKeyboard() {
        println("Check --- here 10")

        inputMethodManager.hideSoftInputFromWindow(binding.searchTxt.windowToken, 0)
        binding.searchTxt.clearFocus()
    }
}