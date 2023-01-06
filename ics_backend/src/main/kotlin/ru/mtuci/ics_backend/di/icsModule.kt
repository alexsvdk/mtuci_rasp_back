package ru.mtuci.ics_backend.di

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.smithy.kotlin.runtime.http.Url
import org.koin.dsl.module
import ru.mtuci.Config
import ru.mtuci.ics_backend.calculator.IcsCalculator
import ru.mtuci.ics_backend.storage.IcsStorage
import ru.mtuci.ics_backend.storage.S3IcsStorage

val icsModule = module {

    single {
        S3Client {
            endpointUrl = Url.parse(Config.S3_ENDPOINT)
            region = Config.S3_REGION
            credentialsProvider = StaticCredentialsProvider {
                accessKeyId = Config.S3_ACCESS_KEY
                secretAccessKey = Config.S3_SECRET_KEY
            }
        }
    }

    single<IcsStorage> {
        S3IcsStorage(get())
    }

    single {
        IcsCalculator(get(), get(), get())
    }
}