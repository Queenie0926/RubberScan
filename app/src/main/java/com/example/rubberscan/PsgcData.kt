package com.example.rubberscan

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

data class PsgcCity(val city: String, val barangays: List<String>)
data class PsgcProvince(val province: String, val cities: List<PsgcCity>)
data class PsgcRegion(val region: String, val provinces: List<PsgcProvince>)

object PsgcData {
    private var data: List<PsgcRegion>? = null

    suspend fun load(context: Context): List<PsgcRegion> = withContext(Dispatchers.IO) {
        data?.let {return@withContext it}

        val json = context.assets.open("psgc.json").bufferedReader().use { it.readText() }
        val regions = mutableListOf<PsgcRegion>()
        val arr = JSONArray(json)

        for (i in 0 until arr.length()) {
            val rObj = arr.getJSONObject(i)
            val provArr = rObj.getJSONArray("provinces")
            val provinces = mutableListOf<PsgcProvince>()

            for (j in 0 until provArr.length()) {
                val pObj = provArr.getJSONObject(j)
                val cityArr = pObj.getJSONArray("cities")
                val cities = mutableListOf<PsgcCity>()

                for (k in 0 until cityArr.length()) {
                    val cObj =cityArr.getJSONObject(k)
                    val bArr = cObj.getJSONArray("barangays")
                    val barangays = (0 until bArr.length()).map { bArr.getString(it) }
                    cities.add(PsgcCity(cObj.getString("city"), barangays))
                }
                provinces.add(PsgcProvince(pObj.getString("province"), cities))
            }
            regions.add(PsgcRegion(rObj.getString("region"), provinces))
        }
        regions.also { data = it}
    }
}