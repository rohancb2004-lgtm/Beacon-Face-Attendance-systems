package com.example.beaconfaceattendance

import android.content.Context
import android.os.Handler
import android.util.Log
import org.altbeacon.beacon.*


class BeaconHelper(context: Context) {

    private val beaconManager = BeaconManager.getInstanceForApplication(context)

    init {
        beaconManager.beaconParsers.add(
            BeaconParser().setBeaconLayout(
                "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
            )
        )
    }

    fun startScanning(onResult: (Boolean) -> Unit) {

        beaconManager.removeAllRangeNotifiers()

        var isHandled = false

        beaconManager.addRangeNotifier { beacons, _ ->

            if (isHandled) return@addRangeNotifier

            if (beacons.isNotEmpty()) {

                val beacon = beacons.first()
                val rssi = beacon.rssi

                Log.d("BEACON", "RSSI: $rssi")

                if (rssi > -90) {

                    Log.d("BEACON", "✅ Inside classroom")

                    isHandled = true
                    beaconManager.stopRangingBeacons(region)
                    onResult(true)

                } else {
                    Log.d("BEACON", "❌ Too far")
                }
            }
        }

        beaconManager.startRangingBeacons(region)

        Handler().postDelayed({
            if (!isHandled) {
                isHandled = true
                beaconManager.stopRangingBeacons(region)
                onResult(false)
            }
        }, 3000)
    }

    companion object {
        val region = Region(
            "myRegion",
            Identifier.parse("11223344-5566-7788-99AA-BBCCDDEEFF00"), // YOUR UUID
            null,
            null
        )
    }
}