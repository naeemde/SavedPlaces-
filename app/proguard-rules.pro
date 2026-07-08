# أضف هنا أي قواعد Proguard خاصة بمشروعك.
# راجع الرابط التالي لمزيد من التفاصيل:
#   https://developer.android.com/studio/build/shrink-code

# احتفظ بأسماء الأعمدة الخاصة بـ Room عند تفعيل تصغير الكود مستقبلاً
-keep class com.savedplaces.app.data.** { *; }
