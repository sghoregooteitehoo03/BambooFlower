package com.sg.android.bambooflower.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import com.sg.android.bambooflower.data.database.AppDatabase
import com.sg.android.bambooflower.other.Contents
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    @Named(Contents.PREF_CHECK_FIRST)
    fun provideCheckFirstPref(@ApplicationContext context: Context) =
        context.getSharedPreferences(Contents.PREF_CHECK_FIRST, Context.MODE_PRIVATE)!!


    @Singleton
    @Provides
    fun provideFireAuth() =
        FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFireStore() =
        FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideFunctions() =
        FirebaseFunctions.getInstance()

    @Singleton
    @Provides
    fun provideFireStorage() =
        FirebaseStorage.getInstance()

    @Singleton
    @Provides
    fun getDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "DiaryDatabase"
        ).build()

    @Singleton
    @Provides
    fun getDao(db: AppDatabase) =
        db.getDao()
}