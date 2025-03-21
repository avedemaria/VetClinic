package com.example.vetclinic.presentation.adapter.doctorsAdapter

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.example.vetclinic.databinding.ItemServiceBinding
import com.example.vetclinic.domain.entities.Service

class DoctorServicesAdapter(
    private val container: LinearLayout,
    private val inflater: LayoutInflater
) {

    fun setServices(services: List<Service>, allServicesButtonContainer: View) {

        container.removeAllViews()


        container.post {
            val containerHeight = container.height
            var usedHeight = 0



            allServicesButtonContainer.measure(
                View.MeasureSpec.makeMeasureSpec(container.width, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.UNSPECIFIED
            )
            val buttonHeight = allServicesButtonContainer.measuredHeight


            for (service in services) {
                val binding = ItemServiceBinding.inflate(inflater, container, false)

                binding.tvServiceName.text = service.serviceName
                binding.tvServicePrice.text = "${service.price} руб"
                binding.tvServiceDuration.text = "${service.duration} мин"

                binding.root.measure(
                    View.MeasureSpec.makeMeasureSpec(container.width, View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.UNSPECIFIED
                )
                val itemHeight = binding.root.measuredHeight

                if (usedHeight + itemHeight + buttonHeight > containerHeight) {
                    break
                }

                usedHeight += itemHeight
                container.addView(binding.root)
            }


            container.addView(allServicesButtonContainer)
        }
    }

//    fun setServices(services: List<Service>, allServicesButtonContainer: View) {
//        container.removeAllViews() // Очищаем контейнер
//
//        services.forEach { service ->
//            val binding = ItemServiceBinding.inflate(inflater, container, false)
//            binding.tvServiceName.text = service.serviceName
//            binding.tvServicePrice.text = "${service.price} руб"
//            binding.tvServiceDuration.text = "${service.duration} мин"
//            container.addView(binding.root)
//        }
//
//        container.addView(allServicesButtonContainer) // Добавляем кнопку в конце
//    }
}

