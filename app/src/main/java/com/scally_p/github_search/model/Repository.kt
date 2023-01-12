package com.scally_p.github_search.model

import com.google.gson.annotations.SerializedName

data class Repository(

    @SerializedName("id")
    var id: Long? = null,

    @SerializedName("node_id")
    var nodeId: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("full_name")
    var fullName: String? = null,

    @SerializedName("owner")
    var owner: Owner? = null,

    @SerializedName("html_url")
    var htmlUrl: String? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("url")
    var url: String? = null,

    @SerializedName("footer")
    var footer: Boolean = false
)