package com.codetest.main.util

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface BaseSchedulerProvider {
    fun io(): Scheduler
    fun ui(): Scheduler
}

class SchedulerProvider : BaseSchedulerProvider {
    override fun io() = Schedulers.io()
    override fun ui() = AndroidSchedulers.mainThread()
}
