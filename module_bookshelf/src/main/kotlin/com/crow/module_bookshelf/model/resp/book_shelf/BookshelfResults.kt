package com.crow.module_bookshelf.model.resp.book_shelf


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookshelfResults(

    @Json(name = "b_folder")
    val mBFolder: Boolean,

    @Json(name = "comic")
    val mComic: Comic,

    @Json(name = "folder_id")
    val mFolderId: Any?,

    @Json(name = "last_browse")
    val mLastBrowse: LastBrowse?,

    @Json(name = "name")
    val mName: Any?,

    @Json(name = "uuid")
    val mUuid: Int
)