package com.example.restaurantreview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.restaurantreview.databinding.ActivityMainBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }


    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvReview.layoutManager = layoutManager

        getRestaurant()
    }

    private fun getRestaurant() {

        val client = AsyncHttpClient()
        val url = "https://restaurant-api.dicoding.dev/detail/uewq1zg2zlskfw1e867"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable
            ) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)


                    val restaurant = responseObject.getJSONObject("restaurant")
                    val name = restaurant.getString("name")
                    val description = restaurant.getString("description")
                    val image = restaurant.getString("pictureId")
                    val getJsonCustomerReviews = restaurant.getJSONArray("customerReviews")
                    val listReview = ArrayList<String>()
                    for (i in 0 until getJsonCustomerReviews.length()){
                        val name = getJsonCustomerReviews.getJSONObject(i).getString("name")
                        val review = getJsonCustomerReviews.getJSONObject(i).getString("review")
                        listReview.add("""$name - $review""".trimIndent())

                    }
                    val adapter = ReviewAdapter(listReview)
                    binding.rvReview.adapter = adapter








                    binding.tvTitle.text = name
                    binding.tvDescription.text = description
                    Glide.with(this@MainActivity)
                        .load("https://restaurant-api.dicoding.dev/images/large/${image}")
                        .into(binding.ivPicture)


                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        })

    }
}