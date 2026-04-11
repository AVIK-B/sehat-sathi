package com.ashaworker.triage.di

import android.content.Context
import androidx.room.Room
import com.ashaworker.triage.data.db.AppDatabase
import com.ashaworker.triage.data.db.QuestionResponseDao
import com.ashaworker.triage.data.db.VisitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "asha_triage.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideVisitDao(db: AppDatabase): VisitDao = db.visitDao()

    @Provides
    fun provideQuestionResponseDao(db: AppDatabase): QuestionResponseDao = db.questionResponseDao()
}
