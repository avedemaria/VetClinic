package com.example.vetclinic.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vetclinic.R


class MainFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_selection_container, SelectionFragment())
            .commit()


        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_info_container, InfoFragment())
            .commit()
    }
}