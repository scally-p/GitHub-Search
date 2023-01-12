package com.scally_p.github_search.model

import com.google.gson.annotations.SerializedName

data class Repository(

    var id: Long? = null,

    @SerializedName("node_id")
    var nodeId: String? = null,

    var name: String? = null,

    @SerializedName("full_name")
    var fullName: String? = null,

    var owner: Owner? = null,

    @SerializedName("html_url")
    var htmlUrl: String? = null,

    var description: String? = null,

    var url: String? = null
)