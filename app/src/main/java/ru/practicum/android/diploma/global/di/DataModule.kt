package ru.practicum.android.diploma.global.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.favorites.data.FavoriteRepositoryImpl
import ru.practicum.android.diploma.favorites.data.VacancyDbConvertor
import ru.practicum.android.diploma.favorites.domain.api.FavoriteRepository
import ru.practicum.android.diploma.filter.data.CountryRepositoryImpl
import ru.practicum.android.diploma.filter.data.FilterRepositoryImpl
import ru.practicum.android.diploma.filter.domain.api.CountryRepository
import ru.practicum.android.diploma.filter.domain.api.FilterRepository
import ru.practicum.android.diploma.global.data.network.HhApi
import ru.practicum.android.diploma.global.data.network.NetworkClient
import ru.practicum.android.diploma.global.data.network.dto.RetrofitNetworkClient
import ru.practicum.android.diploma.global.db.AppDatabase
import ru.practicum.android.diploma.global.sharedpreferences.SharedPreferencesFilter
import ru.practicum.android.diploma.global.sharedpreferences.SharedPreferencesFilterImpl
import ru.practicum.android.diploma.global.sharedpreferences.SharedPreferencesFilterImpl.Companion.FILTER_STATE
import ru.practicum.android.diploma.global.util.HeaderInterceptor
import ru.practicum.android.diploma.global.util.NetworkUtil
import ru.practicum.android.diploma.search.data.mapper.FilterMapper
import ru.practicum.android.diploma.search.data.mapper.VacancyMapper
import ru.practicum.android.diploma.search.data.repository.SearchRepositoryImpl
import ru.practicum.android.diploma.search.domain.api.SearchRepository
import ru.practicum.android.diploma.sharing.data.ExternalNavigatorImpl
import ru.practicum.android.diploma.sharing.domain.api.ExternalNavigator
import java.util.concurrent.TimeUnit

val dataModule = module {
    single<HhApi> {
        val client = OkHttpClient.Builder()
            .callTimeout(NetworkUtil.CALL_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NetworkUtil.READ_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(HeaderInterceptor())
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.HH_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HhApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(
            hhApi = get(),
            networkUtil = NetworkUtil(context = androidContext())
        )
    }

    factory<SearchRepository> {
        SearchRepositoryImpl(
            networkClient = get(),
            vacancyMapper = get(),
            filterMapper = get(),
            database = get(),
            vacancyDbConvertor = get()
        )
    }

    factory {
        VacancyMapper()
    }

    factory {
        FilterMapper()
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build()
    }

    factory {
        VacancyDbConvertor()
    }

    factory<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }

    factory<ExternalNavigator> {
        ExternalNavigatorImpl(application = get())
    }

    single<FilterRepository> {
        FilterRepositoryImpl(get())
    }
    single<CountryRepository> {
        CountryRepositoryImpl(get())
    }

    single {
        androidContext().getSharedPreferences(
            FILTER_STATE,
            Context.MODE_PRIVATE
        )
    }

    factory {
        Gson()
    }

    single<SharedPreferencesFilter> {
        SharedPreferencesFilterImpl(sharedPrefs = get(), gson = get())
    }
}
