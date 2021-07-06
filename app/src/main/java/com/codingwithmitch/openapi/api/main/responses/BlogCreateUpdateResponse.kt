package com.codingwithmitch.openapi.api.main.responses

import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.util.DateUtils
import com.google.gson.annotations.SerializedName

class BlogCreateUpdateResponse(

    @SerializedName("response")
    val response: String,

    @SerializedName("pk")
    val pk: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("slug")
    val slug: String,

    @SerializedName("body")
    val body: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("date_updated")
    val date_updated: String,

    @SerializedName("username")
    val username: String

)

fun BlogCreateUpdateResponse.toBlogPost(): BlogPost {
    return BlogPost(
        pk = pk,
        title = title,
        slug = slug,
        body = body,
        image = image,
        date_updated = DateUtils.convertServerStringDateToLong(
            date_updated
        ),
        username = username
    )
}













