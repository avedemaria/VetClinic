package com.example.vetclinic.di

import android.app.Application
import com.example.vetclinic.presentation.MainActivity
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.addPetScreen.AddPetFragment
import com.example.vetclinic.presentation.adminScreen.AdminHomeFragment
import com.example.vetclinic.presentation.appointmentsScreen.AppointmentsFragment
import com.example.vetclinic.presentation.appointmentsScreen.archivedAppointmentsScreen.ArchivedAppointmentsFragment
import com.example.vetclinic.presentation.bookAppointmentScreen.BookAppointmentFragment
import com.example.vetclinic.presentation.appointmentsScreen.currentAppointmentsScreen.CurrentAppointmentsFragment
import com.example.vetclinic.presentation.detailedDoctorInfoScreen.DetailedDoctorInfoFragment
import com.example.vetclinic.presentation.doctorsScreen.DoctorsFragment
import com.example.vetclinic.presentation.mainScreen.homeScreen.HomeFragment
import com.example.vetclinic.presentation.loginScreen.LoadingFragment
import com.example.vetclinic.presentation.loginScreen.LoginFragment
import com.example.vetclinic.presentation.mainScreen.MainFragment
import com.example.vetclinic.presentation.profileScreen.petScreen.PetFragment
import com.example.vetclinic.presentation.PetInput
import com.example.vetclinic.presentation.servicesScreen.PlainServicesFragment
import com.example.vetclinic.presentation.profileScreen.ProfileFragment
import com.example.vetclinic.presentation.registrationScreen.RegistrationFragment
import com.example.vetclinic.presentation.sendResetLinkScreen.SendResetLinkFragment
import com.example.vetclinic.presentation.mainScreen.homeScreen.ServicesListFragment.ServicesWithDepFragment
import com.example.vetclinic.presentation.settingsScreen.SettingsFragment
import com.example.vetclinic.presentation.updatePasswordScreen.UpdatePasswordFragment
import com.example.vetclinic.presentation.profileScreen.userScreen.UserFragment
import com.example.vetclinic.presentation.registrationScreen.UserInput
import dagger.BindsInstance
import dagger.Component
import jakarta.inject.Singleton

@Singleton
@Component(
    modules = [DataModule::class, DomainModule::class, ViewModelModule::class,
        WorkerBindsModule::class]
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

    fun inject(userFragment: UserFragment)

    fun inject(petFragment: PetFragment)

    fun inject(addPetFragment: AddPetFragment)

    fun inject(settingsFragment: SettingsFragment)

    fun inject(sendResetLinkFragment: SendResetLinkFragment)

    fun inject(updatePasswordFragment: UpdatePasswordFragment)

    fun inject(bookAppointmentFragment: BookAppointmentFragment)

    fun inject(appointmentsFragment: AppointmentsFragment)

    fun inject(currentAppointmentsFragment: CurrentAppointmentsFragment)

    fun inject(archivedAppointmentsFragment: ArchivedAppointmentsFragment)

    fun inject(adminHomeFragment: AdminHomeFragment)


    @Component.Factory
    interface AppComponentFactory {
        fun create(
            @BindsInstance application: Application,
        ): AppComponent
    }
}