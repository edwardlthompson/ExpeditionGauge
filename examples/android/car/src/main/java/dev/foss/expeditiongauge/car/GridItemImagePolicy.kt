package dev.foss.expeditiongauge.car

import androidx.car.app.model.CarIcon

/**
 * Car App Library [androidx.car.app.model.GridItem] requires exactly one of
 * setImage or setLoading(true). We always set an image — never loading.
 */
object GridItemImagePolicy {
    fun needsFallback(image: CarIcon?): Boolean = image == null

    fun resolve(image: CarIcon?): CarIcon = image ?: CarIcon.APP_ICON
}
