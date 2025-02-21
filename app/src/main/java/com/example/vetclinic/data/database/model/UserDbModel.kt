package com.example.vetclinic.data.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.vetclinic.CodeReview

@Entity(
    tableName = "users",
    indices = [Index(
        value = ["email"],
        unique = true
    )] // Какие уникальные и по каким достаём значения?
)
data class UserDbModel(
    @PrimaryKey
    @CodeReview("Если в будущем есть вероятность, что будешь переменовывать, то лучше @ColumnInfo")
    val uid: String,
    val userName: String,
    val userLastName: String,
    val petName: String,
    val phoneNumber: String,
    val email: String
)

// Тут все поля всегда точно не null?
// Если может быть несколько питомцев у одного юзера, то лучше отдельную таблицу Pet
// и связать через @Relation
// Сейчас получается так:
// uid = 101 userName = Anna petName = Murzik
// Как добавить второго питомца для юзера?