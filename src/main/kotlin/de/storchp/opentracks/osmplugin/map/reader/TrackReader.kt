package de.storchp.opentracks.osmplugin.map.reader

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import de.storchp.opentracks.osmplugin.map.model.Track
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

object TrackReader {
    private val TAG: String = Track::class.java.simpleName

    const val ID = "_id"
    const val NAME = "name"
    const val DESCRIPTION = "description"
    const val CATEGORY = "category"
    const val STARTTIME = "starttime"
    const val STOPTIME = "stoptime"
    const val TOTALDISTANCE = "totaldistance"
    const val TOTALTIME = "totaltime"
    const val MOVINGTIME = "movingtime"
    const val AVGSPEED = "avgspeed"
    const val AVGMOVINGSPEED = "avgmovingspeed"
    const val MAXSPEED = "maxspeed"
    const val MINELEVATION = "minelevation"
    const val MAXELEVATION = "maxelevation"
    const val ELEVATIONGAIN = "elevationgain"

    val PROJECTION = arrayOf(
        ID,
        NAME,
        DESCRIPTION,
        CATEGORY,
        STARTTIME,
        STOPTIME,
        TOTALDISTANCE,
        TOTALTIME,
        MOVINGTIME,
        AVGSPEED,
        AVGMOVINGSPEED,
        MAXSPEED,
        MINELEVATION,
        MAXELEVATION,
        ELEVATIONGAIN
    )

    /** Reads the Tracks from the Content Uri */
    fun readTracks(
        resolver: ContentResolver,
        data: Uri,
        categoryFilter: String? = null
    ): List<Track> = buildList {
        try {
            val selection = categoryFilter?.let { "$CATEGORY = ?" }
            val selectionArgs = categoryFilter?.let { arrayOf(it) }

            resolver.query(data, PROJECTION, selection, selectionArgs, null)?.use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(ID))
                    val trackname = cursor.getString(cursor.getColumnIndexOrThrow(NAME))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION))
                    val category = cursor.getString(cursor.getColumnIndexOrThrow(CATEGORY))
                    val startTime = cursor.getLong(cursor.getColumnIndexOrThrow(STARTTIME))
                    val stopTime = cursor.getLong(cursor.getColumnIndexOrThrow(STOPTIME))
                    val totalDistance = cursor.getDouble(cursor.getColumnIndexOrThrow(TOTALDISTANCE))
                    val totalTime = cursor.getLong(cursor.getColumnIndexOrThrow(TOTALTIME))
                    val movingTime = cursor.getLong(cursor.getColumnIndexOrThrow(MOVINGTIME))
                    val avgSpeed = cursor.getDouble(cursor.getColumnIndexOrThrow(AVGSPEED))
                    val avgMovingSpeed = cursor.getDouble(cursor.getColumnIndexOrThrow(AVGMOVINGSPEED))
                    val maxSpeed = cursor.getDouble(cursor.getColumnIndexOrThrow(MAXSPEED))
                    val minElevation = cursor.getDouble(cursor.getColumnIndexOrThrow(MINELEVATION))
                    val maxElevation = cursor.getDouble(cursor.getColumnIndexOrThrow(MAXELEVATION))
                    val elevationGain = cursor.getDouble(cursor.getColumnIndexOrThrow(ELEVATIONGAIN))

                    add(
                        Track(
                            id = id,
                            name = trackname,
                            description = description,
                            category = category,
                            startTime = Instant.ofEpochMilli(startTime),
                            stopTime = Instant.ofEpochMilli(stopTime),
                            totalDistanceMeter = totalDistance,
                            totalTime = totalTime.milliseconds,
                            movingTime = movingTime.milliseconds,
                            avgSpeedMeterPerSecond = avgSpeed,
                            avgMovingSpeedMeterPerSecond = avgMovingSpeed,
                            maxSpeedMeterPerSecond = maxSpeed,
                            minElevationMeter = minElevation,
                            maxElevationMeter = maxElevation,
                            elevationGainMeter = elevationGain
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "No permission to read track")
        } catch (e: Exception) {
            Log.e(TAG, "Reading track failed", e)
        }
    }
}