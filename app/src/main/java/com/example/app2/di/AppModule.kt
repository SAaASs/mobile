package com.example.app2.di

import android.content.Context
import androidx.room.Room
import com.example.app2.data.RoomAccountDataSource
import com.example.app2.data.RoomAccountRepository
import com.example.app2.data.local.room.AccountDao
import com.example.app2.data.local.room.AppDatabase
import com.example.core.storage.AccountRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "accounts.db").build()

    @Provides
    fun provideDao(db: AppDatabase): AccountDao = db.accountDao()

    @Provides
    @Singleton
    fun provideDataSource(dao: AccountDao): RoomAccountDataSource =
        RoomAccountDataSource(dao)

    @Provides
    @Singleton
    fun provideAccountRepository(ds: RoomAccountDataSource): AccountRepository =
        RoomAccountRepository(ds)
}
