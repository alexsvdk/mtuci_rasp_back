package ru.mtuci.di

import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.module
import org.litote.kmongo.KMongo
import ru.mtuci.core.*
import ru.mtuci.db.MongoDisciplinesRepository
import ru.mtuci.db.MongoGroupsRepository
import ru.mtuci.db.MongoRepositoryFactory
import ru.mtuci.db.MongoTeachersRepository
import ru.mtuci.models.*

object CoreModules {

    val repositories = module {

        ///Group
        single<GroupsRepository> { MongoGroupsRepository(get()) }
        single<BaseRepository<Group>>(TypeQualifier(Group::class)) { get<MongoGroupsRepository>() }

        ///Teachers
        single<TeachersRepository> { MongoTeachersRepository(get()) }
        single<BaseRepository<Teacher>>(TypeQualifier(Teacher::class)) { get<MongoTeachersRepository>() }

        ///Disciplines
        single<DisciplinesRepository> { MongoDisciplinesRepository(get()) }
        single<BaseRepository<Discipline>>(TypeQualifier(Discipline::class)) { get<DisciplinesRepository>() }

        createRepo<RegularLesson>()
        createRepo<Room>()
    }

    val mongo = module {
        single { KMongo.createClient().getDatabase("mtuci-rasp") }
        single <RepositoryFactory> { MongoRepositoryFactory(get()) }
    }

}

private inline fun <reified T : BaseDocument> org.koin.core.module.Module.createRepo() = single(
    TypeQualifier(T::class)
) {
    get<RepositoryFactory>().createRepository(T::class)
}