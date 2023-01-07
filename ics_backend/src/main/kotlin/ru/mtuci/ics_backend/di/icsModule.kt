package ru.mtuci.ics_backend.di

import org.koin.dsl.module
import ru.mtuci.ics_backend.calculator.IcsCalculator
import ru.mtuci.ics_backend.storage.IcsStorage
import ru.mtuci.ics_backend.storage.S3IcsStorage

val icsModule = module {

    single<IcsStorage> {
        S3IcsStorage()
    }

    single {
        IcsCalculator(get(), get(), get())
    }
}