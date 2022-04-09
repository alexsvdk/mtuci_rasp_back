package ru.mtuci.di

import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.module
import org.litote.kmongo.KMongo
import ru.mtuci.Config
import ru.mtuci.core.*
import ru.mtuci.db.*
import ru.mtuci.models.*

object CoreModules {

    val repositories = module {

        ///Group
        single<GroupsRepository> { MongoGroupsRepository(get()) }
        single<BaseRepository<Group>>(TypeQualifier(Group::class)) { get<GroupsRepository>() }

        ///Teachers
        single<TeachersRepository> { MongoTeachersRepository(get()) }
        single<BaseRepository<Teacher>>(TypeQualifier(Teacher::class)) { get<TeachersRepository>() }

        ///Disciplines
        single<DisciplinesRepository> { MongoDisciplinesRepository(get()) }
        single<BaseRepository<Discipline>>(TypeQualifier(Discipline::class)) { get<DisciplinesRepository>() }

        ///Directions
        single<DirectionsRepository> { MongoDirectionsRepository(get()) }
        single<BaseRepository<Direction>>(TypeQualifier(Direction::class)) { get<DirectionsRepository>() }

        ///Lessons
        single<RegularLessonsRepository> { MongoRegularLessonsRepository(get()) }
        single<BaseRepository<RegularLesson>>(TypeQualifier(RegularLesson::class)) { get<RegularLessonsRepository>() }

        ///Rooms
        single<RoomsRepository> { MongoRoomsRepository(get()) }
        single<BaseRepository<Room>>(TypeQualifier(Room::class)) { get<RoomsRepository>() }
    }

    val mongo = module {
        single { KMongo.createClient(Config.MONGO_URL).getDatabase("mtuci-rasp") }
        single<RepositoryFactory> { MongoRepositoryFactory(get()) }
    }

}