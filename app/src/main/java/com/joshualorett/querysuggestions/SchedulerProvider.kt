package com.joshualorett.querysuggestions

import io.reactivex.Scheduler

/**
 * Returns schedulers.
 * Created by Joshua on 5/18/2020.
 */
interface SchedulerProvider {
    val ui: Scheduler
    val io: Scheduler
}