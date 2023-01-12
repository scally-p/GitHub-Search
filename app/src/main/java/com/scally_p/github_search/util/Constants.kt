package com.scally_p.github_search.util

object Constants {

    object Urls {
        const val SERVER = "https://api.github.com/"
    }

    object Api {
        const val IN_QUALIFIER = "in:name,description"
        const val LAST_SEARCH_QUERY: String = "last_search_query"
        const val DEFAULT_QUERY = "Android"
        const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
    }

    object Paging {
        const val PAGE_START = 1
        const val PAGE_SIZE = 30
        const val VISIBLE_THRESHOLD = 3
    }
}