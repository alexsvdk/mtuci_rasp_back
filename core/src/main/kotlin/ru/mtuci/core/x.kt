import ru.mtuci.di.getRepository
import ru.mtuci.di.koin
import ru.mtuci.models.BaseDocument

inline fun <reified T : BaseDocument> T.save() = koin.getRepository<T>().save(this)
inline fun <reified T : BaseDocument> T.remove() = this.id?.let { koin.getRepository<T>().remove(it) }
