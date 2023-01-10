package com.scally_p.github_search.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

open class Repositories : RealmObject() {

    @SerializedName("total_count")
    var totalCount: Int? = null

    @SerializedName("incomplete_results")
    var incompleteResults: Boolean? = null

    var items: RealmList<Repository>? = null
}