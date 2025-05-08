package com.example.vetclinic.domain.repository

interface DialogDataStore {


   suspend fun getLastShownDialog (): Long?
   suspend fun putLastShowDialog(lastDialog: Long)

   suspend fun getDisableDialogForeverFlag (): Boolean
   suspend fun putDisableDialogForeverFlag ()

}