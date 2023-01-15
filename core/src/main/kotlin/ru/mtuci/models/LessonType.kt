package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

enum class LessonType {
    @GraphQLDescription("Лекция")
    LECTURE,

    @GraphQLDescription("Практика")
    PRACTICE,

    @GraphQLDescription("Лабораторная работа")
    LABORATORY,

    @GraphQLDescription("Экзамен")
    EXAM,

    @GraphQLDescription("Консультация")
    CONSULTATION,

    @GraphQLDescription("Физическая культура")
    SPORT,
    UNKNOWN,
}