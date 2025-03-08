package com.engineerfred.cartoongan

import android.content.Context
import com.engineerfred.cartoongan.repo.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesRepository(
        @ApplicationContext context: Context
    ): AppRepository = AppRepository(
        context
    )
}