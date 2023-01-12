package com.scally_p.github_search.model

import com.google.gson.annotations.SerializedName

open class Repositories(

    @SerializedName("total_count")
    var totalCount: Int = 0,

    @SerializedName("items")
    var items: MutableList<Repository>? = null
)