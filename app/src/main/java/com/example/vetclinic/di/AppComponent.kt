package com.example.vetclinic.di

import android.app.Application
import com.example.vetclinic.presentation.MainActivity
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.fragment.AddPetFragment
import com.example.vetclinic.presentation.fragment.AdminHomeFragment
import com.example.vetclinic.presentation.fragment.AppointmentsFragment
import com.example.vetclinic.presentation.fragment.ArchivedAppointmentsFragment
import com.example.vetclinic.presentation.fragment.BookAppointmentFragment
import com.example.vetclinic.presentation.fragment.CurrentAppointmentsFragment
import com.example.vetclinic.presentation.fragment.DetailedDoctorInfoFragment
import com.example.vetclinic.presentation.fragment.DoctorsFragment
import com.example.vetclinic.presentation.fragment.HomeFragment
import com.example.vetclinic.presentation.fragment.LoadingFragment
import com.example.vetclinic.presentation.fragment.LoginFragment
import com.example.vetclinic.presentation.fragment.MainFragment
import com.example.vetclinic.presentation.fragment.PetFragment
import com.example.vetclinic.presentation.fragment.PetRegistrationForm
import com.example.vetclinic.presentation.fragment.PlainServicesFragment
import com.example.vetclinic.presentation.fragment.ProfileFragment
import com.example.vetclinic.presentation.fragment.RegistrationFragment
import com.example.vetclinic.presentation.fragment.ResetPasswordWithEmailFragment
import com.example.vetclinic.presentation.fragment.ServicesWithDepFragment
import com.example.vetclinic.presentation.fragment.SettingsFragment
import com.example.vetclinic.presentation.fragment.UpdatePasswordFragment
import com.example.vetclinic.presentation.fragment.UserFragment
import com.example.vetclinic.presentation.fragment.UserRegistrationForm
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

    fun inject(userRegistrationForm: UserRegistrationForm)

    fun inject(petRegistrationForm: PetRegistrationForm)

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

    fun inject(resetPasswordWithEmailFragment: ResetPasswordWithEmailFragment)

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