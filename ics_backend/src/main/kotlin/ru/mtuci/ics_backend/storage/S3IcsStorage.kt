package ru.mtuci.ics_backend.storage

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectAclRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.asByteStream
import aws.smithy.kotlin.runtime.http.Url
import ru.mtuci.Config
import java.io.File

class S3IcsStorage(
    val client: S3Client,
) : IcsStorage {

    private suspend fun getClient(): S3Client {
        return S3Client.fromEnvironment {
            endpointUrl = Url.parse(Config.S3_ENDPOINT)
            region = Config.S3_REGION
            credentialsProvider = StaticCredentialsProvider {
                accessKeyId = Config.S3_ACCESS_KEY
                secretAccessKey = Config.S3_SECRET_KEY
            }
        }
    }

    override suspend fun getUrlById(id: String): String? {
        val request = GetObjectAclRequest {
            bucket = Config.S3_BUCKET
            key = "ics/$id.ics"
        }
        return try {
            getClient().use {
                it.getObjectAcl(request)
            }
            client.config.endpointUrl.toString() + "/" + Config.S3_BUCKET + "/" + "ics/$id.ics"
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun uploadIcs(id: String, ics: File): String {
        val request = PutObjectRequest {
            bucket = Config.S3_BUCKET
            key = "ics/$id.ics"
            metadata = mapOf("Content-Type" to "text/calendar")
            body = ics.asByteStream()
        }
        getClient().use {
            it.putObject(request)
        }
        return client.config.endpointUrl.toString() + "/" + Config.S3_BUCKET + "/" + "ics/$id.ics"
    }

}