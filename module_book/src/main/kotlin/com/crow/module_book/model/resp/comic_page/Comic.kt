package com.crow.module_book.model.resp.comic_page

import com.squareup.moshi.Json


data class Comic(

    @Json(name =  "name")
    val mName: String,

    @Json(name =  "path_word")
    val mPathWord: String,

    @Json(name =  "restrict")
    val mRestrict: Restrict,

    @Json(name =  "uuid")
    val mUuid: String
)