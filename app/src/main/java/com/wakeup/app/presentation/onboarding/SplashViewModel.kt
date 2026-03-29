package com.wakeup.app.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.wakeup.app.domain.usecase.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    fun isOnboardingCompleted(): Boolean {
        return runBlocking {
            getSettingsUseCase.isOnboardingCompleted()
        }
    }

    fun hasPermissions(): Boolean {
        // TODO: Check actual permissions
        return false
    }
}
