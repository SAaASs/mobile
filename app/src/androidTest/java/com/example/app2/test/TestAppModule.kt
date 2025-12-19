package com.example.app2.test

import com.example.core.domain.repository.AccountsRepository
import com.example.core.domain.usecase.AccountUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Singleton
    fun provideRepo(): AccountsRepository = FakeAccountsRepository()

    @Provides
    @Singleton
    fun provideUseCases(repo: AccountsRepository): AccountUseCases =
        AccountUseCases.from(repo)
}
