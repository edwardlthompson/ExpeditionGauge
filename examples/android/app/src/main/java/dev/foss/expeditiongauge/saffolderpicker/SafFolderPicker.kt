package dev.foss.expeditiongauge.saffolderpicker

import android.content.Intent
import android.net.Uri

/** Persistable ACTION_OPEN_DOCUMENT_TREE for session exports. */
object SafFolderPicker {
    fun persistFlags(): Int =
        Intent.FLAG_GRANT_READ_URI_PERMISSION or
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION

    fun treeIntent(): Intent =
        Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).addFlags(persistFlags())

    fun isTreeUri(uri: String): Boolean =
        uri.contains("/tree/") || uri.startsWith("content://") && uri.contains("tree")

    fun displayName(uri: Uri): String =
        uri.lastPathSegment?.substringAfterLast(':').orEmpty().ifBlank { uri.toString() }
}
