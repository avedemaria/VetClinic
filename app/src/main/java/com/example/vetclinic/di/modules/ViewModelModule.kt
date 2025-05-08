package com.example.vetclinic.di.modules

import androidx.lifecycle.ViewModel
import com.example.vetclinic.di.keys.ViewModelKey
import com.example.vetclinic.presentation.screens.adminScreen.AdminHomeViewModel
import com.example.vetclinic.presentation.screens.loginScreen.LoadingViewModel
import com.example.vetclinic.presentation.screens.loginScreen.LoginViewModel
import com.example.vetclinic.presentation.screens.loginScreen.registrationFragment.RegistrationViewModel
import com.example.vetclinic.presentation.screens.mainScreen.MainViewModel
import com.example.vetclinic.presentation.screens.mainScreen.appointmentsScreen.SharedAppointmentsViewModel
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.HomeViewModel
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.ServicesListFragment.ServiceWithDepViewModel
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.doctorsListFragment.DoctorViewModel
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.doctorsListFragment.bookAppointmentFragment.BookAppointmentViewModel
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.doctorsListFragment.detailedDoctorInfoFragment.DetailedDoctorInfoViewModel
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.doctorsListFragment.plainServicesListFragment.PlainServiceViewModel
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment.PetViewModel
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment.addPetFragment.AddPetViewModel
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.userFragment.UserViewModel
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.userFragment.settingsFragment.SettingsViewModel
import com.example.vetclinic.presentation.screens.sendResetLinkScreen.SendResetLinkViewModel
import com.example.vetclinic.presentation.screens.updatePasswordScreen.UpdatePasswordViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module

interface ViewModelModule {

    @IntoMap
    @ViewModelKey(LoadingViewModel::class)
    @Binds
    fun bindLoadingViewModel(impl: LoadingViewModel): ViewModel

    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    @Binds
    fun bindRegistrationViewModel(impl: RegistrationViewModel): ViewModel

    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    @Binds
    fun bindLoginViewModel(impl: LoginViewModel): ViewModel

    @IntoMap
    @ViewModelKey(MainViewModel::class)
    @Binds
    fun bindMainViewModel(impl: MainViewModel): ViewModel

    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    @Binds
    fun bindMainSharedViewModel(impl: HomeViewModel): ViewModel

    @IntoMap
    @ViewModelKey(DoctorViewModel::class)
    @Binds
    fun bindDoctorViewModel(impl: DoctorViewModel): ViewModel

    @IntoMap
    @ViewModelKey(DetailedDoctorInfoViewModel::class)
    @Binds
    fun bindDetailedDoctorInfoViewModel (infoViewModel: DetailedDoctorInfoViewModel): ViewModel

    @IntoMap
    @ViewModelKey(ServiceWithDepViewModel::class)
    @Binds
    fun bindServiceViewModel(impl: ServiceWithDepViewModel): ViewModel

    @IntoMap
    @ViewModelKey(PlainServiceViewModel::class)
    @Binds
    fun bindPlainServiceViewModel(impl: PlainServiceViewModel): ViewModel

    @IntoMap
    @ViewModelKey(UserViewModel::class)
    @Binds
    fun bindUserViewModel(impl: UserViewModel): ViewModel


    @IntoMap
    @ViewModelKey(PetViewModel::class)
    @Binds
    fun bindPetViewModel(impl: PetViewModel): ViewModel

    @IntoMap
    @ViewModelKey(AddPetViewModel::class)
    @Binds
    fun addPetViewModel(impl: AddPetViewModel): ViewModel


    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    @Binds
    fun bindSettingsViewModel(impl: SettingsViewModel): ViewModel

    @IntoMap
    @ViewModelKey(SendResetLinkViewModel::class)
    @Binds
    fun bindSendResetLinkViewModel(impl: SendResetLinkViewModel): ViewModel

    @IntoMap
    @ViewModelKey(UpdatePasswordViewModel::class)
    @Binds
    fun bindUpdatePasswordViewModel (infoViewModel: UpdatePasswordViewModel): ViewModel

    @IntoMap
    @ViewModelKey(BookAppointmentViewModel::class)
    @Binds
    fun bindBookAppointmentViewModel(impl: BookAppointmentViewModel): ViewModel


    @IntoMap
    @ViewModelKey(AdminHomeViewModel::class)
    @Binds
    fun bindAdminHomeViewModel(impl: AdminHomeViewModel): ViewModel

    @IntoMap
    @ViewModelKey(SharedAppointmentsViewModel::class)
    @Binds
    fun bindDetailedAppointmentsViewModel(impl: SharedAppointmentsViewModel): ViewModel




}
