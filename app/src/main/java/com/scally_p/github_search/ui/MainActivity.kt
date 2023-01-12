package com.scally_p.github_search.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.SimpleItemAnimator
import com.scally_p.github_search.R
import com.scally_p.github_search.databinding.ActivityMainBinding
import com.scally_p.github_search.model.Repository
import com.scally_p.github_search.ui.adapter.LockableLinearLayoutManager
import com.scally_p.github_search.ui.adapter.RepositoryAdapter
import com.scally_p.github_search.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val tag: String = MainActivity::class.java.name

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var repositoryAdapter: RepositoryAdapter

    private lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        lifecycleScope.launch {
            prepareViews()

            repeatOnLifecycle(Lifecycle.State.RESUMED) { displayData() }
        }
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

    private fun prepareViews() {
        repositoryAdapter = RepositoryAdapter()

        binding.searchTxt.setText(Constants.Api.DEFAULT_QUERY)
        binding.searchTxt.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
                viewModel.reset()
                repositoryAdapter.clear()
                viewModel.searchRepositories(binding.searchTxt.text.toString().trim())
                true
            } else {
                false
            }
        }

        binding.recyclerView.apply {
            layoutManager = LockableLinearLayoutManager(this@MainActivity)
            adapter = repositoryAdapter
        }
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        (binding.recyclerView.layoutManager as LockableLinearLayoutManager).setRecyclerViewOverScrollListener(
            object : LockableLinearLayoutManager.OverScrollListener {
                override fun onBottomOverScroll() {
                    Log.d(tag, "RecyclerViewOverScrollListener - onBottomOverScroll")
                    if (!repositoryAdapter.footerLoading) {
                        repositoryAdapter.addFooterLoader()
                        binding.recyclerView.post {
                            repositoryAdapter.notifyItemInserted(viewModel.repositories.lastIndex)
                        }

                        if (viewModel.currentCount < viewModel.totalCount) {
                            viewModel.searchRepositories(binding.searchTxt.text.toString().trim())
                        }
                    }
                }

                override fun onTopOverScroll() {
                    Log.d(tag, "RecyclerViewOverScrollListener - onTopOverScroll")
                }
            })
    }

    private fun displayData() {
        viewModel.observeRepositoriesLiveData().observe(this) { repositories ->
            repositoryAdapter.setList(repositories as ArrayList<Repository>)
            viewModel.currentCount = repositoryAdapter.repositories.size
            println("size size -- 3: ${viewModel.currentCount}")

            binding.resultsCount.text = resources.getString(
                R.string.results_count,
                viewModel.currentCount.toString(),
                viewModel.totalCount.toString()
            )
            binding.resultsCount.visibility = if (viewModel.repositories.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.observeLoading().observe(this) { loading ->
            if (loading) {
                if (repositoryAdapter.itemCount == 0) {
                    binding.shimmerFrameLayout.startShimmer()
                    binding.shimmerFrameLayout.isVisible = true
                }
                binding.progressBar.isVisible = true

                binding.emptyList.isVisible = false
            } else {
                binding.shimmerFrameLayout.stopShimmer()
                binding.shimmerFrameLayout.isVisible = false
                binding.progressBar.isVisible = false

                binding.emptyList.isVisible = viewModel.repositories.isEmpty()
            }
        }

        viewModel.observeErrorMessage().observe(this) { message ->
            Log.d(tag, message)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(binding.searchTxt.windowToken, 0)
        binding.searchTxt.clearFocus()
    }
}