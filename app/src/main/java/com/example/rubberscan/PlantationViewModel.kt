package com.example.rubberscan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rubberscan.db.AppDatabase
import com.example.rubberscan.db.entity.Plantation
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlantationViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getInstance(app).plantationDao()
    private val _userId = MutableStateFlow<String?>(null)
    val plantation: StateFlow<Plantation?> = _userId
        .flatMapLatest { id ->
        if (id == null) flowOf(null) else dao.observedPlantation(id)}
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setUser(userId: String?) {_userId.value = userId}
    fun save(plantation: Plantation, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            dao.upsert(plantation)
            onDone()
        }
    }
}