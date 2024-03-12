package com.example.dz2.networking

import android.util.Log
import com.example.dz2.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


interface ProductService {

    @GET("products/")
    suspend fun searchProducts(
        @Query("q") query: String? = null,
        @Query("skip") skip: Int,
        @Query("limit") limit: Int
    ): ProductSearchResponse

    @GET("/products/categories")
    suspend fun getAllCategories(): List<String>

    @GET("/products/categories/{category}")
    suspend fun searchByCategory(
        @Path("category") category: String
    ): ProductSearchResponse

    companion object {
        fun create(): ProductService {
            val okHttpClient = configureToIgnoreCertificate(OkHttpClient.Builder())
                .build()

            return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(ProductService::class.java)
        }

        //проблема с SSL
        //javax.net.ssl.SSLHandshakeException: java.security.cert.CertPathValidatorException: Trust anchor for certification path not found.
        private fun configureToIgnoreCertificate(builder: OkHttpClient.Builder): OkHttpClient.Builder {
            Log.e("Ignore Ssl Certificate", "")
            try {

                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }
                )
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier(HostnameVerifier { hostname: String?, session: SSLSession? -> true })
            } catch (e: Exception) {
                Log.e("Exception$e", e.toString())
            }
            return builder
        }
    }
}