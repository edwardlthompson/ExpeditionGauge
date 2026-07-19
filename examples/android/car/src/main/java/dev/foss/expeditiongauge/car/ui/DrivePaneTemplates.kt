package dev.foss.expeditiongauge.car.ui

import android.content.res.Configuration
import androidx.car.app.CarContext
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import dev.foss.expeditiongauge.car.AaDisplaySpec
import dev.foss.expeditiongauge.car.DriveHudContent
import dev.foss.expeditiongauge.car.DriveHudRow

internal object DrivePaneTemplates {
    /** Pane requires ≥1 row; ZWSP keeps chrome free of “Live OK” filler. */
    const val SPACER = "\u200B"

    fun readDisplaySpec(carContext: CarContext): AaDisplaySpec {
        val cfg = carContext.resources.configuration
        val density = carContext.resources.displayMetrics.density
        val limit = runCatching {
            carContext.getCarService(ConstraintManager::class.java)
                .getContentLimit(ConstraintManager.CONTENT_LIMIT_TYPE_PANE)
        }.getOrDefault(AaDisplaySpec.DEFAULT_GRID_LIMIT)
        val night = (cfg.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        return AaDisplaySpec.from(
            widthDp = cfg.screenWidthDp,
            heightDp = cfg.screenHeightDp,
            density = density,
            maxGridItems = limit.coerceAtLeast(1),
            isDarkMode = night || carContext.isDarkMode,
        )
    }

    fun addRows(builder: Pane.Builder, rows: List<DriveHudRow>) {
        val effective = rows.take(1).ifEmpty {
            listOf(DriveHudRow(SPACER, SPACER))
        }
        effective.forEach { row ->
            builder.addRow(
                Row.Builder()
                    .setTitle(row.title.ifBlank { SPACER })
                    .addText(row.text.ifBlank { SPACER })
                    .build(),
            )
        }
    }

    fun pane(content: DriveHudContent): Pane {
        val builder = Pane.Builder().setImage(content.image)
        addRows(builder, content.rows)
        return builder.build()
    }

    fun waitingTemplate(message: String): Template {
        val pane = Pane.Builder()
            .setImage(CarIcon.APP_ICON)
            .addRow(Row.Builder().setTitle("Waiting").addText(message).build())
            .build()
        return PaneTemplate.Builder(pane)
            .setHeaderAction(Action.APP_ICON)
            .build()
    }

    fun rowsFrom(labels: List<DriveHudRow>): List<DriveHudRow> = labels.take(1)
}
