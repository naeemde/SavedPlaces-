package com.savedplaces.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * يمثل مكاناً واحداً محفوظاً: اسمه، وإحداثياته، وتاريخ حفظه.
 */
@Entity(tableName = "places")
data class Place(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: Long = System.currentTimeMillis()
)
