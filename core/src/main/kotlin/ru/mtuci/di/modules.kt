package ru.mtuci.di

import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.module
import org.litote.kmongo.KMongo
import ru.mtuci.core.RepositoryFactory
import ru.mtuci.db.MongoRepositoryFactory
import ru.mtuci.models.*

object CoreModules {

    val repositories = module {
        createRepo<Discipline>()
        createRepo<Group>()
        createRepo<RegularLesson>()
        createRepo<Room>()
        createRepo<Teacher>()
    }

    val mongo = module {
        single { KMongo.createClient().getDatabase("mtuci-rasp") }
        single { MongoRepositoryFactory(get()) }
    }

}

private inline fun <reified T : BaseDocument> org.koin.core.module.Module.createRepo() = single(
    TypeQualifier(T::class)
) {
    get<RepositoryFactory>().createRepository(T::class)
}
