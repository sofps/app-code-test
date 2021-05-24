package com.codetest.util

import com.codetest.main.util.BaseSchedulerProvider
import io.reactivex.schedulers.Schedulers

class TestSchedulerProvider : BaseSchedulerProvider {
    override fun io() = Schedulers.trampoline()
    override fun ui() = Schedulers.trampoline()
}
