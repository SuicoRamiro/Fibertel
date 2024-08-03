package com.example.fibertel.activities

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fibertel.ApiClient
import com.example.fibertel.R
import com.example.fibertel.adapter.PlanAdapter
import com.example.fibertel.model.Plan
import com.example.fibertel.model.UserManager
import com.example.fibertel.network.ApiEndpoints
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ServiciosDisponibles : AppCompatActivity() {

    private lateinit var planAdapter: PlanAdapter
    private lateinit var tvNoPlanes: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servicios_disponibles)

        val userManager = UserManager(this)
        val user = userManager.currentUser

        if (user == null) {
            Log.e("ServiciosDisponibles", "User is null")
            return
        }

        val btnRetroceder: ImageButton = findViewById(R.id.btn_retroceder)
        btnRetroceder.setOnClickListener {
            finish()
        }

        tvNoPlanes = findViewById(R.id.tv_no_planes)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPlanes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        planAdapter = PlanAdapter(mutableListOf())
        recyclerView.adapter = planAdapter

        fetchPlanes()
    }

    private fun fetchPlanes() {
        val request = ApiClient.createRequest(ApiEndpoints.PLANES)

        ApiClient.getClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ServiciosDisponibles", "Error fetching plans", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("ServiciosDisponibles", "Unexpected code $response")
                    return
                }

                response.body()?.let { responseBody ->
                    val responseString = responseBody.string()
                    Log.d("ServiciosDisponibles", "Response: $responseString")

                    try {
                        val jsonObject = JSONObject(responseString)
                        val jsonArray = jsonObject.getJSONArray("data")
                        runOnUiThread {
                            parsePlanes(jsonArray)
                        }
                    } catch (e: Exception) {
                        Log.e("ServiciosDisponibles", "Error parsing plans", e)
                    }
                } ?: run {
                    Log.e("ServiciosDisponibles", "Response body is null")
                }
            }
        })
    }

    private fun parsePlanes(jsonArray: JSONArray) {
        val plans = mutableListOf<Plan>()
        for (i in 0 until jsonArray.length()) {
            try {
                val planObject = jsonArray.getJSONObject(i)
                val plan = Plan(
                    name = planObject.getString("name"),
                    public_id = planObject.getInt("public_id"),
                    ceil_down_kbps = planObject.getInt("ceil_down_kbps"),
                    ceil_up_kbps = planObject.getInt("ceil_up_kbps"),
                    price = planObject.getString("price"),
                    created_at = planObject.getString("created_at"),
                    updated_at = planObject.getString("updated_at")
                )
                plans.add(plan)
            } catch (e: Exception) {
                Log.e("ServiciosDisponibles", "Error parsing plan item", e)
            }
        }

        runOnUiThread {
            if (plans.isEmpty()) {
                tvNoPlanes.visibility = TextView.VISIBLE
            } else {
                tvNoPlanes.visibility = TextView.GONE
                planAdapter.updatePlans(plans)
            }
        }
    }
}
