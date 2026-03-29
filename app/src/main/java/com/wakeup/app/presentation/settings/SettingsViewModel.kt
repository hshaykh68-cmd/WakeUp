package com.wakeup.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakeup.app.domain.usecase.GetSettingsUseCase
import com.wakeup.app.domain.usecase.SaveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase
) : ViewModel() {

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _use24Hour = MutableStateFlow(false)
    val use24Hour: StateFlow<Boolean> = _use24Hour.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _isPremium.value = getSettingsUseCase.isPremiumUser()
            _use24Hour.value = getSettingsUseCase.getUse24HourFormat()
        }
    }

    fun setPremiumUser(isPremium: Boolean) {
        viewModelScope.launch {
            saveSettingsUseCase.setPremiumUser(isPremium)
            _isPremium.value = isPremium
        }
    }

    fun setUse24HourFormat(use24Hour: Boolean) {
        viewModelScope.launch {
            saveSettingsUseCase.setUse24HourFormat(use24Hour)
            _use24Hour.value = use24Hour
        }
    }
}
