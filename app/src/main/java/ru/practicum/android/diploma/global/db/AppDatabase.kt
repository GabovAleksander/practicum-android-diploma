package ru.practicum.android.diploma.global.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.practicum.android.diploma.global.db.dao.VacancyDao
import ru.practicum.android.diploma.global.db.entity.VacancyEntity
import ru.practicum.android.diploma.global.db.entity._VacancyEntity

@Database(
    version = 1,
    entities = [_VacancyEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vacancyDao(): VacancyDao
}
