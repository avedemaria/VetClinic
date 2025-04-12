package com.example.vetclinic.data.repositoryImpl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.example.vetclinic.domain.DialogDataStore
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

class DialogDataStoreImpl @Inject constructor(private val dataStore: DataStore<Preferences>) :
    DialogDataStore {

    override suspend fun getLastShownDialog(): Long? {
        return dataStore.data.first()[KEY_LAST_DIALOG]
    }

    override suspend fun putLastShowDialog(lastDialog: Long) {
        dataStore.edit { preferences ->
            preferences[KEY_LAST_DIALOG] = lastDialog
        }
    }


    override suspend fun getDisableDialogForeverFlag(): Boolean {
       return dataStore.data.first()[KEY_IS_DIALOG_DISABLED]?:false
    }

    override suspend fun putDisableDialogForeverFlag() {
        dataStore.edit { preferences ->
            preferences[KEY_IS_DIALOG_DISABLED] = true
        }
    }

    companion object {
        private val KEY_LAST_DIALOG = longPreferencesKey("lastShownDialog")
        private val KEY_IS_DIALOG_DISABLED = booleanPreferencesKey("isDialogDisabled")
    }
}