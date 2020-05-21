package com.joshualorett.querysuggestions

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Return schedulers for the app.
 * Created by Joshua on 5/18/2020.
 */
class AppSchedulerProvider : SchedulerProvider {
    override val ui: Scheduler
        get() = AndroidSchedulers.mainThread()
    override val io: Scheduler
        get() = Schedulers.io()
}