package com.example.app2.di

import android.content.Context
import androidx.room.Room
import com.example.app2.data.RoomAccountDataSource
import com.example.app2.data.RoomAccountRepository
import com.example.app2.data.local.room.AccountDao
import com.example.app2.data.local.room.AppDatabase
import com.example.core.domain.repository.AccountsRepository
import com.example.core.domain.usecase.AccountUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- ROOM ---
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

    // --- Domain Repository (ЕДИНЫЙ) ---
    @Provides
    @Singleton
    fun provideAccountsRepository(ds: RoomAccountDataSource): AccountsRepository =
        RoomAccountRepository(ds)

    // --- UseCases ---
    @Provides
    @Singleton
    fun provideAccountUseCases(repo: AccountsRepository): AccountUseCases =
        AccountUseCases.from(repo)
}
