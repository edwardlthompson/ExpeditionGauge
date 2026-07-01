package dev.foss.expeditiongauge.car.ui

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import dev.foss.expeditiongauge.car.CarAppBridgeRegistry
import dev.foss.expeditiongauge.car.CarTelemetryHost

class TelemetryPaneScreen(carContext: CarContext) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val bridge = CarAppBridgeRegistry.bridge
        if (bridge == null || !bridge.isAndroidAutoEnabled()) {
            return PaneTemplate.Builder(
                Pane.Builder()
                    .addRow(
                        Row.Builder()
                            .setTitle("ExpeditionGauge")
                            .addText("Enable Android Auto in phone Settings")
                            .build(),
                    )
                    .build(),
            ).build()
        }

        val rows = CarTelemetryHost.buildRows(
            metrics = bridge.metricValues(),
            allowlist = bridge.allowedMetricKeys(),
        )
        val paneBuilder = Pane.Builder()
        if (rows.isEmpty()) {
            paneBuilder.addRow(
                Row.Builder()
                    .setTitle("Waiting for telemetry")
                    .addText("Start sensors on phone")
                    .build(),
            )
        } else {
            rows.forEach { row ->
                paneBuilder.addRow(
                    Row.Builder()
                        .setTitle(row.title)
                        .addText(row.value)
                        .build(),
                )
            }
        }

        val recordAction = if (bridge.isRecording()) {
            Action.Builder()
                .setTitle("Stop")
                .setOnClickListener {
                    bridge.stopRecording()
                    invalidate()
                }
                .build()
        } else {
            Action.Builder()
                .setTitle("Record")
                .setOnClickListener {
                    bridge.startRecording()
                    invalidate()
                }
                .build()
        }

        val markAction = Action.Builder()
            .setTitle("Mark")
            .setOnClickListener {
                bridge.markEvent()
                invalidate()
            }
            .build()

        val actionStrip = ActionStrip.Builder()
            .addAction(recordAction)
            .addAction(markAction)
            .build()

        return PaneTemplate.Builder(paneBuilder.build())
            .setTitle("ExpeditionGauge")
            .setHeaderAction(Action.APP_ICON)
            .setActionStrip(actionStrip)
            .build()
    }
}
