package com.example.rubberscan

import com.example.rubberscan.db.dao.SensorReadingDao

// ── Risk output ────────────────────────────────────────────
enum class RiskLevel { LOW, MODERATE, HIGH, UNKNOWN }

// ── Per-disease favorable environmental ranges ─────────────
// Literature-backed thresholds (see project_env_thresholds memory).
// These describe SUSTAINED favorable conditions; risk is computed over a
// time window, not a single scan-instant reading.
data class DiseaseThreshold(
    val key: String,
    val label: String,
    val tempMin: Float,
    val tempMax: Float,
    val humidityMin: Float
)

val diseaseThresholds = listOf(
    // Pestalotiopsis LFD: 25–30 °C (halts >32 °C), RH > 85 %
    DiseaseThreshold("plfd", "Pestalotiopsis LFD", 25f, 30f, 85f),
    // Anthracnose: 26–32 °C, RH > 90 % with prolonged leaf wetness
    DiseaseThreshold("anthracnose", "Anthracnose", 26f, 32f, 90f),
    // Powdery Mildew: 25–28 °C, very high humidity (night dew). Note: also
    // favored by dry foliage/no rain — DHT22 can't capture that, so temp+RH
    // is a proxy only.
    DiseaseThreshold("mildew", "Powdery Mildew", 25f, 28f, 90f),
    // Algal Leaf Spot: 20–32 °C, RH > 70 %, frequent rain
    DiseaseThreshold("algal", "Algal Leaf Spot", 20f, 32f, 70f)
)

// ── Windowed cumulative risk ───────────────────────────────
// Counts the share of logged readings in the window that fell inside the
// disease's favorable range, then maps that ratio to a risk tier.
suspend fun computeRisk(
    dao: SensorReadingDao,
    disease: DiseaseThreshold,
    windowHours: Int = 48
): RiskLevel {
    val since = System.currentTimeMillis() - windowHours * 60L * 60L * 1000L
    val readings = dao.readingsSince(since)
    if (readings.isEmpty()) return RiskLevel.UNKNOWN

    val favorable = readings.count { r ->
        r.temperature in disease.tempMin..disease.tempMax &&
            r.humidity >= disease.humidityMin
    }
    val ratio = favorable.toFloat() / readings.size
    return when {
        ratio >= 0.5f -> RiskLevel.HIGH      // favorable ≥50% of the window
        ratio >= 0.2f -> RiskLevel.MODERATE  // favorable 20–50%
        else          -> RiskLevel.LOW
    }
}

// Convenience: percentage of the window that was favorable (for UI copy like
// "favorable 62% of the last 48h").
suspend fun favorablePercent(
    dao: SensorReadingDao,
    disease: DiseaseThreshold,
    windowHours: Int = 48
): Int {
    val since = System.currentTimeMillis() - windowHours * 60L * 60L * 1000L
    val readings = dao.readingsSince(since)
    if (readings.isEmpty()) return 0
    val favorable = readings.count { r ->
        r.temperature in disease.tempMin..disease.tempMax &&
            r.humidity >= disease.humidityMin
    }
    return (favorable * 100) / readings.size
}
