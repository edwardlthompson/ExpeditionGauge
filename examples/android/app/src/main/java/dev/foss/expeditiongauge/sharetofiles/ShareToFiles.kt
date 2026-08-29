package dev.foss.expeditiongauge.sharetofiles

import android.content.Intent

/** Prefer Files / DocumentsUI over social apps when sharing exports. */
object ShareToFiles {
    const val DOCUMENTS_UI = "com.android.documentsui"

    fun prefersFiles(packageName: String): Boolean {
        val name = packageName.lowercase()
        return name.contains("documentsui") ||
            name.endsWith(".files") ||
            name.contains("filemanager")
    }

    fun decorate(send: Intent, title: String): Intent {
        send.addCategory(Intent.CATEGORY_DEFAULT)
        send.putExtra(Intent.EXTRA_TITLE, title)
        send.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        return send
    }

    fun chooser(send: Intent, title: String): Intent {
        val decorated = decorate(send, title)
        val files = Intent(decorated).setPackage(DOCUMENTS_UI)
        return Intent.createChooser(decorated, title).apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(files))
        }
    }
}
