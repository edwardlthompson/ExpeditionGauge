package dev.foss.expeditiongauge.storageautodelete

/** Cap policy for deleting oldest unprotected sessions. */
object StorageAutoDelete {
    fun needsPrune(usedBytes: Long, allowedBytes: Long): Boolean =
        allowedBytes > 0L && usedBytes >= allowedBytes

    fun canDeleteOldest(hasUnprotected: Boolean, overCap: Boolean): Boolean =
        overCap && hasUnprotected
}
