package com.yasincidem.eyedropper.ui.feature.appicons.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yasincidem.eyedropper.ui.feature.appicons.domain.usecase.GetFilteredAppIconsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AppIconsViewModel @Inject constructor(
    private val getFilteredAppIconsUseCase: GetFilteredAppIconsUseCase,
) : ViewModel() {

    private val _appIconUris = MutableStateFlow<List<Uri>>(listOf())
    val appIconUris: StateFlow<List<Uri>> = _appIconUris

    init {
        getAppIconUris()
    }

    private fun getAppIconUris() = viewModelScope.launch {
        _appIconUris.emit(getFilteredAppIconsUseCase())
    }
}