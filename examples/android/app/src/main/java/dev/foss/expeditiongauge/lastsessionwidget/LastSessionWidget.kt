package dev.foss.expeditiongauge.lastsessionwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import dev.foss.expeditiongauge.MainActivity
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.appshortcuts.AppShortcuts

class LastSessionWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val store = LastSessionStore(context)
        val label = LastSessionLabel.text(store.name(), store.startMs())
        val launch = Intent(context, MainActivity::class.java).apply {
            action = AppShortcuts.ACTION_LIBRARY
        }
        val pending = PendingIntent.getActivity(
            context,
            0,
            launch,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        ids.forEach { id ->
            val views = RemoteViews(context.packageName, R.layout.last_session_widget)
            views.setTextViewText(R.id.last_session_label, label)
            views.setOnClickPendingIntent(R.id.last_session_label, pending)
            manager.updateAppWidget(id, views)
        }
    }
}
