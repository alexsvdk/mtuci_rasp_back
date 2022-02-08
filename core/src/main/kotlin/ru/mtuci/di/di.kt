package ru.mtuci.di

import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.qualifier.TypeQualifier
import ru.mtuci.core.Repository
import ru.mtuci.models.*

val koin = startKoin {
    CoreModules.mongo
    CoreModules.repositories
}.koin


inline fun <reified T : BaseDocument> Koin.getRepository() = get<Repository<T>>(
    qualifier = TypeQualifier(T::class)
)

