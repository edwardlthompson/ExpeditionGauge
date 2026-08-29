package dev.foss.expeditiongauge.car.aaparkedvoice

/** Spoken Record/Stop confirmation while the vehicle is parked. */
object AaParkedVoice {
    const val START = "Recording"
    const val STOP = "Stopped"

    fun canAnnounce(parked: Boolean): Boolean = parked

    fun phrase(recordingAfter: Boolean): String = if (recordingAfter) START else STOP
}
