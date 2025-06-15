package com.example.vetclinic.di

import android.app.Application
import com.example.vetclinic.MainActivity
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.di.modules.DataModule
import com.example.vetclinic.di.modules.DataSourceModule
import com.example.vetclinic.di.modules.DomainModule
import com.example.vetclinic.di.modules.UtilsModule
import com.example.vetclinic.di.modules.ViewModelModule
import com.example.vetclinic.di.modules.WorkerBindsModule
import com.example.vetclinic.presentation.screens.adminScreen.AdminHomeFragment
import com.example.vetclinic.presentation.screens.loginScreen.LoadingFragment
import com.example.vetclinic.presentation.screens.loginScreen.LoginFragment
import com.example.vetclinic.presentation.screens.loginScreen.registrationScreen.RegistrationFragment
import com.example.vetclinic.presentation.screens.loginScreen.registrationScreen.UserInput
import com.example.vetclinic.presentation.screens.mainScreen.MainFragment
import com.example.vetclinic.presentation.screens.mainScreen.appointmentsScreen.AppointmentsFragment
import com.example.vetclinic.presentation.screens.mainScreen.appointmentsScreen.archivedAppointmentsFragment.ArchivedAppointmentsFragment
import com.example.vetclinic.presentation.screens.mainScreen.appointmentsScreen.currentAppointmentsFragment.CurrentAppointmentsFragment
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.HomeFragment
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.servicesListFragment.ServicesWithDepFragment
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.doctorsListFragment.DoctorsFragment
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.doctorsListFragment.bookAppointmentFragment.BookAppointmentFragment
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.doctorsListFragment.detailedDoctorInfoFragment.DetailedDoctorInfoFragment
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.doctorsListFragment.plainServicesListFragment.PlainServicesFragment
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.ProfileFragment
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment.PetFragment
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment.addPetFragment.AddPetFragment
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.userFragment.UserFragment
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.userFragment.settingsFragment.SettingsFragment
import com.example.vetclinic.presentation.screens.sendResetLinkScreen.SendResetLinkFragment
import com.example.vetclinic.presentation.screens.updatePasswordScreen.UpdatePasswordFragment
import com.example.vetclinic.presentation.widgets.PetInput
import dagger.BindsInstance
import dagger.Component
import jakarta.inject.Singleton

@Singleton
@Component(
    modules = [DataModule::class, DomainModule::class, ViewModelModule::class,
        WorkerBindsModule::class, UtilsModule::class, DataSourceModule::class]
)
interface AppComponent {


    fun inject(application: VetClinicApplication)

    fun inject(activity: MainActivity)

    fun inject(loadingFragment: LoadingFragment)

    fun inject(mainFragment: MainFragment)

    fun inject(homeFragment: HomeFragment)

    fun inject(registrationFragment: RegistrationFragment)

    fun inject(userInput: UserInput)

    fun inject(petInput: PetInput)

    fun inject(loginFragment: LoginFragment)

    fun inject(doctorsFragment: DoctorsFragment)

    fun inject(detailedDoctorInfoFragment: DetailedDoctorInfoFragment)

    fun inject(servicesWithDepFragment: ServicesWithDepFragment)

    fun inject(plainServicesFragment: PlainServicesFragment)

    fun inject(profileFragment: ProfileFragment)

    fun inject(userComposeFragment: UserFragment)

    fun inject(petFragment: PetFragment)

    fun inject(addPetFragment: AddPetFragment)

    fun inject(settingsFragment: SettingsFragment)

    fun inject(sendResetLinkFragment: SendResetLinkFragment)

    fun inject(updatePasswordFragment: UpdatePasswordFragment)

    fun inject(bookAppointmentFragment: BookAppointmentFragment)

    fun inject(appointmentsFragment: AppointmentsFragment)

    fun inject(currentAppointmentsFragment: CurrentAppointmentsFragment)

    fun inject(archivedAppointmentsFragment: ArchivedAppointmentsFragment)

    fun inject(adminHomeComposeFragment: AdminHomeFragment)


    @Component.Factory
    interface AppComponentFactory {
        fun create(
            @BindsInstance application: Application,
        ): AppComponent
    }
}