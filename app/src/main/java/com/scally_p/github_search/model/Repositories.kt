package com.scally_p.github_search.model

import com.google.gson.annotations.SerializedName

data class Repositories(

    @SerializedName("total_count")
    var totalCount: Int = 0,

    var items: List<Repository>? = null
)