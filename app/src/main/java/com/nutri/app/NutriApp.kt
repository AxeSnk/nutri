package com.nutri.app

import android.app.Application
import com.nutri.app.di.AppGraph

/** Точка входа. Держит единый граф зависимостей (без Hilt — MVP прост и явный). */
class NutriApp : Application() {
    lateinit var appGraph: AppGraph
        private set

    override fun onCreate() {
        super.onCreate()
        appGraph = AppGraph(this)
    }
}
