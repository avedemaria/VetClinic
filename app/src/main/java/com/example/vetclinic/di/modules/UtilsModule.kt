package com.example.vetclinic.di.modules

import com.example.vetclinic.domain.entities.pet.PetInputData
import com.example.vetclinic.domain.entities.user.UserInputData
import com.example.vetclinic.utils.AgeUtils
import com.example.vetclinic.utils.FieldValidator
import com.example.vetclinic.utils.Validator
import com.example.vetclinic.utils.impl.AgeUtilsImpl
import com.example.vetclinic.utils.impl.FieldValidatorImpl
import com.example.vetclinic.utils.impl.PetValidatorImpl
import com.example.vetclinic.utils.impl.UserValidatorImpl
import dagger.Binds
import dagger.Module
import jakarta.inject.Singleton

@Module
interface UtilsModule {

    @Binds
    @Singleton
    fun bindAgeUtils(impl: AgeUtilsImpl): AgeUtils

    @Binds
    @Singleton
    fun bindUserValidator(impl: UserValidatorImpl): Validator<UserInputData>

    @Binds
    @Singleton
    fun bindPetValidator(impl: PetValidatorImpl): Validator<PetInputData>

    @Binds
    @Singleton
    fun bindFieldValidator(impl: FieldValidatorImpl): FieldValidator

}