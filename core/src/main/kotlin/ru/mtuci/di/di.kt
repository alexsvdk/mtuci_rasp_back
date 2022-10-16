package ru.mtuci.di

import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.qualifier.TypeQualifier
import ru.mtuci.core.BaseRepository
import ru.mtuci.models.BaseDocument


val koin = startKoin {
    modules(
        CoreModules.mongo,
        CoreModules.repositories,
    )
}.koin


inline fun <reified T : BaseDocument> Koin.getRepository() = get<BaseRepository<T>>(
    qualifier = TypeQualifier(T::class)
)

