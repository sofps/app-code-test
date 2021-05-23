package com.codetest.main.ui

sealed class AddLocationState {

    object Loading : AddLocationState()
    object Success : AddLocationState()
    object Error : AddLocationState()
}
