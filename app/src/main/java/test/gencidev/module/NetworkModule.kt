package test.gencidev.module

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import test.gencidev.BuildConfig
import test.gencidev.common.preferences.AccountManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import test.gencidev.network.ResponseInterceptor
import test.gencidev.network.services.DayOffServices
import java.io.IOException
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    private val timeOut: Int by lazy {
        90
    }

    private val responseInterceptor by lazy {
        ResponseInterceptor
    }

    private val jsonChecker by lazy {
        object : Converter.Factory() {
            override fun responseBodyConverter(
                type: Type,
                annotations: Array<Annotation>,
                retrofit: Retrofit
            ): Converter<ResponseBody, *>? {
                return Converter<ResponseBody, Any> { responseBody ->
                    val delegate = retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
                    try {
                        delegate.convert(responseBody)
                    } catch (error: Exception) {
                        try {
                            throw IOException()
                        } catch (error: Exception) {
                            throw IOException(error.message)
                        }
                    }
                }
            }
        }
    }

    @Provides
    @Singleton
    fun providesMoshi(): Moshi {
        return Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .build()
    }

    var gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(jsonChecker)
            .build()
            .also {
                responseInterceptor.retrofit = it
            }
    }

    @Provides
    @Singleton
    fun providesOkhttpClient(
    ): OkHttpClient {

        val builder = OkHttpClient.Builder()
            .addInterceptor(responseInterceptor)
            .addNetworkInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .connectTimeout(timeOut.toLong(), TimeUnit.SECONDS)
            .readTimeout(timeOut.toLong(), TimeUnit.SECONDS)
            .writeTimeout(timeOut.toLong(), TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )

        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun providesDayOffService(retrofit: Retrofit): DayOffServices {
        return retrofit.create(DayOffServices::class.java)
    }

}