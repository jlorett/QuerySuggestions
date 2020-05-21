package com.joshualorett.querysuggestions

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 * Returns schedulers for testing.
 * Created by Joshua on 5/20/2020.
 */
class TestSchedulerProvider: SchedulerProvider {
    override val ui: Scheduler
        get() = Schedulers.trampoline()
    override val io: Scheduler
        get() = Schedulers.trampoline()
}