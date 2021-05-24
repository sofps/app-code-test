package com.codetest.main.ui

sealed class DeleteLocationState {

    object Loading : DeleteLocationState()
    object Success : DeleteLocationState()

    /**
     * This represents the error state in which the location that could not be removed
     * needs to be reinserted in the same position.
     */
    data class Error(
        val position: Int,
        val location: LocationUI
    ) : DeleteLocationState()
}
