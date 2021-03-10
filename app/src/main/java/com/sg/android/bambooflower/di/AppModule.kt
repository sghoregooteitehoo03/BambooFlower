package com.sg.android.bambooflower.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFireAuth() =
        FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFireStore() =
        FirebaseFirestore.getInstance()
}