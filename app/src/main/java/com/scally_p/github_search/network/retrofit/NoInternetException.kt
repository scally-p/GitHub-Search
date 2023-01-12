package com.scally_p.github_search.network.retrofit

import java.io.IOException

class NoInternetException: IOException("No Internet Connection") {
    override fun toString(): String {
        return "No Internet Connection"
    }
}