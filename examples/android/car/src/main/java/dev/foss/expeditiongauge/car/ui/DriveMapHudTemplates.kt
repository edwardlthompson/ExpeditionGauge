package dev.foss.expeditiongauge.car.ui

import androidx.car.app.CarContext
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import dev.foss.expeditiongauge.car.AaDisplaySpec
import dev.foss.expeditiongauge.car.CarAppBridge
import dev.foss.expeditiongauge.car.HudStripOrientation

/** Template builders for [DriveMapHudScreen]. */
internal object DriveMapHudTemplates {
    fun navigation(
        carContext: CarContext,
        bridge: CarAppBridge,
        orientation: HudStripOrientation,
        onInvalidate: () -> Unit,
    ): Template {
        val appStrip = when (orientation) {
            HudStripOrientation.COLUMN ->
                DriveMapHudChrome.columnChromeStrip(carContext, bridge, onInvalidate)
            HudStripOrientation.ROW ->
                DriveMapHudChrome.navChromeStrip(carContext, bridge, onInvalidate)
        }
        return NavigationTemplate.Builder()
            .setMapActionStrip(ActionStrip.Builder().addAction(Action.PAN).build())
            .setActionStrip(appStrip)
            .build()
    }

    fun paneFallback(
        carContext: CarContext,
        bridge: CarAppBridge,
        spec: AaDisplaySpec,
        onInvalidate: () -> Unit,
    ): Template {
        val hud = bridge.driveHud(spec)
        val paneBuilder = Pane.Builder().setImage(hud.image)
        DrivePaneTemplates.addRows(paneBuilder, hud.rows)
        paneBuilder.addAction(
            DriveMapHudChrome.levelAction(carContext, bridge, titled = false, onInvalidate),
        )
        return PaneTemplate.Builder(paneBuilder.build())
            .setHeaderAction(Action.APP_ICON)
            .setActionStrip(DriveMapHudChrome.paneChromeStrip(carContext, bridge, onInvalidate))
            .build()
    }
}
