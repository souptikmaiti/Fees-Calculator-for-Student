package com.example.feecalculator

import android.app.Application
import com.example.feecalculator.data.database.DatabaseFeesCal
import com.example.feecalculator.repository.RepositoryStudentFees
import com.example.feecalculator.viewmodel.ViewModelFactoryReceipt
import com.example.feecalculator.viewmodel.ViewModelFactoryStudent
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton


class MVVMApplication: Application(), KodeinAware {

    override val kodein:Kodein = Kodein.lazy {
        import(androidXModule(this@MVVMApplication))

        bind() from singleton { DatabaseFeesCal(instance()) }
        bind() from singleton { RepositoryStudentFees(instance()) }
        bind() from provider { ViewModelFactoryStudent(instance()) }
        bind() from provider { ViewModelFactoryReceipt(instance()) }
    }
}