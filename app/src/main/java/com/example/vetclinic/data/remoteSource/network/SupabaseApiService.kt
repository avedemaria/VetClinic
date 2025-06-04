package com.example.vetclinic.data.remoteSource.network


import com.example.vetclinic.data.remoteSource.network.model.AppointmentDto
import com.example.vetclinic.data.remoteSource.network.model.AppointmentWithDetailsDto
import com.example.vetclinic.data.remoteSource.network.model.CanBookTimeSlotParams
import com.example.vetclinic.data.remoteSource.network.model.DayDto
import com.example.vetclinic.data.remoteSource.network.model.DayWithTimeSlotsDto
import com.example.vetclinic.data.remoteSource.network.model.DepartmentDto
import com.example.vetclinic.data.remoteSource.network.model.DoctorDto
import com.example.vetclinic.data.remoteSource.network.model.PetDto
import com.example.vetclinic.data.remoteSource.network.model.ServiceDto
import com.example.vetclinic.data.remoteSource.network.model.TimeSlotDto
import com.example.vetclinic.data.remoteSource.network.model.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query


interface SupabaseApiService {

    @GET("rest/v1/users")
    suspend fun getUserFromSupabaseDbById(@Query("uid") userId: String):
            Response<List<UserDto>>

    @GET("rest/v1/pets")
    suspend fun getPetsFromSupabaseDb(@Query("user_id") userId: String):
            Response<List<PetDto>>


    @POST("rest/v1/users")
    suspend fun addUser(@Body user: UserDto): Response<Unit>

    @POST("rest/v1/pets")
    suspend fun addPet(@Body petDto: PetDto): Response<Unit>

    @PATCH("rest/v1/users")
    suspend fun updateUser(
        @Query("uid") userId: String,
        @Body updatedUser: UserDto,
    ): Response<Unit>

    @PATCH("rest/v1/pets")
    @Headers("Prefer: return=representation")
    suspend fun updatePet(
        @Query("pet_id") petId: String,
        @Body updatedPet: PetDto,
    ): Response<List<PetDto>>

    @DELETE("rest/v1/pets")
    @Headers("Prefer: return=representation")
    suspend fun deletePet(
        @Query("pet_id") petId: String,
    ): Response<List<PetDto>>


    @GET("rest/v1/departments?select=*")
    suspend fun getDepartments(): Response<List<DepartmentDto>>


    @GET("rest/v1/doctors?select=*")
    suspend fun getDoctors(): Response<List<DoctorDto>>


    @GET("rest/v1/services?select=*")
    suspend fun getServices(): Response<List<ServiceDto>>

    @GET("rest/v1/services")
    suspend fun getServicesByDepartmentId(
        @Query("department_id") departmentId: String,
    ): Response<List<ServiceDto>>


    @POST("rest/v1/appointments")
    suspend fun addAppointment(@Body appointmentCreateDto: AppointmentDto): Response<Unit>


    @GET("rest/v1/days")
    suspend fun getDaysWithTimeSlots(
        @Query("select") select: String = "*,time_slots!inner(*)",
        @Query("time_slots.is_booked") isBooked: String = "eq.false",
        @Query("time_slots.doctor_id") doctorId: String,
        @Query("time_slots.service_id") serviceId: String,
        @Query("date") dateRange: String,
        @Query("order") dateOrder: String = "date.asc",
        @Query("time_slots.order") timeSlotsOrder: String = "start_time.asc",
        @Query("limit") limit: Int = 1000,
    ): Response<List<DayWithTimeSlotsDto>>


    @POST("rest/v1/days?on_conflict=id,date")
    @Headers("Prefer: resolution=merge-duplicates")
    suspend fun insertDays(
        @Body days: List<DayDto>,
    ): Response<Unit>

    @POST("rest/v1/time_slots?on_conflict=id,start_time,end_time,day_id")
    @Headers("Prefer: resolution=merge-duplicates")
    suspend fun insertTimeSlots(
        @Body timeSlots: List<TimeSlotDto>,
    ): Response<Unit>


    @PATCH("rest/v1/time_slots")
    suspend fun updateTimeSlot(
        @Query("id") timeSlotId: String,
        @Body timeSlotDto: TimeSlotDto,
    ): Response<Unit>


    @GET("rest/v1/time_slots")
    suspend fun getTimeSlotById(@Query("id") timeSlotId: String): List<TimeSlotDto>

    @PATCH("rest/v1/appointments")
    suspend fun updateAppointmentStatus(
        @Query("id") appointmentId: String,
        @Body appointmentDto: AppointmentDto,
    ): Response<Unit>

    @GET("rest/v1/rpc/get_full_appointments_for_admin")
    suspend fun getAppointmentsWithDetailsByDate(
        @Query("selected_date") dateTime: String,
        @Query("offsetx") offset: Int,
        @Query("limitx") limit: Int,
    ): Response<List<AppointmentWithDetailsDto>>


    @POST("rest/v1/rpc/get_full_appointments")
    suspend fun getAppointmentWithDetails(@Body queryData: AppointmentQuery):
            Response<List<AppointmentWithDetailsDto>>

    @POST("rest/v1/rpc/delete_user")
    suspend fun deleteUser(): Response<Unit>

}





