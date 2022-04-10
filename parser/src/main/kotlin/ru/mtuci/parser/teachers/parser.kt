package ru.mtuci.parser.teachers

import org.jsoup.Jsoup
import ru.mtuci.core.TeachersRepository
import ru.mtuci.di.koin
import save

private val nameRex =
    "([А-Я])([а-я]+) ([А-Я])([а-я]+) ([А-Я])([а-я]+)".toRegex()

fun parseTeachers() {
    try {
        val urls = getRaspUrls()
        val names = urls.map(::processUrl).flatten().toSet()
        publishNames(names)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun getRaspUrls(): Set<String> {
    val page = Jsoup.connect("https://mtuci.ru/").get()
    val table = page.select("a")
    return table.mapNotNull {
        it.attr("href")
    }.filter { it.contains("about_the_university") && !it.contains("news") }.toSet()
}

private fun processUrl(url: String): Set<String> {
    return try {
        Thread.sleep(1000)
        var url = url
        if (!url.contains("http")) {
            url = "https://mtuci.ru$url"
        }
        val html = Jsoup.connect(url).get().html()
        val res = nameRex.findAll(html).map { it.value }.toSet()
        res
    } catch (e: Exception) {
        e.printStackTrace()
        emptySet()
    }
}

private fun publishNames(names: Set<String>) {
    val teachersRepo = koin.get<TeachersRepository>()
    for (name in names) {
        val data = name.split(" ")
        if (data.size != 3) continue

        val teacher = teachersRepo.findByLastNameAndInitials(
            data[0],
            data[1].substring(0, 1),
            data[2].substring(0, 1),
        ) ?: continue

        teacher.firstName = data[1]
        teacher.fathersName = data[2]

        teacher.save()
    }
}
