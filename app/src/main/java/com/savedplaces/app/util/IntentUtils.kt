package com.savedplaces.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.savedplaces.app.data.Place

/**
 * أدوات مشاركة وملاحة تعتمد بالكامل على تطبيقات مثبتة مسبقاً على الجهاز
 * (خرائط جوجل أو أي تطبيق آخر يدعم روابط geo:) دون أي حاجة لمفتاح API مدفوع.
 */
object IntentUtils {

    /** يفتح تطبيق الملاحة الافتراضي على الجهاز ليقوم بإرشاد المستخدم إلى المكان المحدد. */
    fun openNavigation(context: Context, place: Place) {
        val googleMapsUri = Uri.parse("google.navigation:q=${place.latitude},${place.longitude}")
        val googleMapsIntent = Intent(Intent.ACTION_VIEW, googleMapsUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        if (googleMapsIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(googleMapsIntent)
            return
        }

        // في حال عدم وجود خرائط جوجل، نترك للمستخدم اختيار أي تطبيق خرائط آخر مثبت لديه
        val label = Uri.encode(place.name)
        val geoUri = Uri.parse(
            "geo:${place.latitude},${place.longitude}?q=${place.latitude},${place.longitude}($label)"
        )
        val geoIntent = Intent(Intent.ACTION_VIEW, geoUri)
        if (geoIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(geoIntent)
        }
    }

    /** يفتح قائمة المشاركة القياسية في أندرويد ليشارك المستخدم رابط الموقع لأي تطبيق آخر. */
    fun sharePlace(context: Context, place: Place) {
        val mapsLink =
            "https://www.google.com/maps/search/?api=1&query=${place.latitude},${place.longitude}"
        val shareText = "${place.name}\n$mapsLink"

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, place.name)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(sendIntent, "مشاركة الموقع"))
    }
}
