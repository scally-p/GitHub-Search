package com.scally_p.github_search.model

import com.google.gson.annotations.SerializedName

data class Owner(

    var id: Int? = null,

    var login: String? = null,

    @SerializedName("node_id")
    var nodeId: String? = null,

    @SerializedName("avatar_url")
    var avatarUrl: String? = null,

    @SerializedName("gravatar_id")
    var gravatarId: String? = null,

    var url: String? = null,

    @SerializedName("html_url")
    var htmlUrl: String? = null
)