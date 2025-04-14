package com.example.vetclinic.domain.interfaces

interface DialogDataStore {


   suspend fun getLastShownDialog (): Long?
   suspend fun putLastShowDialog(lastDialog: Long)

   suspend fun getDisableDialogForeverFlag (): Boolean
   suspend fun putDisableDialogForeverFlag ()

}