import Downloader
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries

val format = Json { ignoreUnknownKeys = true }
object Launcher {
    var javaHome: String = System.getProperty("java.home")
    var javaMemoryLimit: String = "1536M"
    var minecraftHome: Path = FileSystems.getDefault().getPath(System.getProperty("user.home"), "DerpyLauncher")
    lateinit var javaArgs: ArrayList<String>
    var httpClient = HttpClient(CIO)
    lateinit var versionsManifest: VersionsManifest
    fun createMinecraftHome() {
        if (!minecraftHome.exists()) {
            println("home dir for derpy not found. creating..")
            Files.createDirectory(minecraftHome)
            Files.createDirectory(minecraftHome.resolve("libraries"))
            Files.createDirectory(minecraftHome.resolve("natives"))
            Files.createDirectory(minecraftHome.resolve("assets"))
        }
    }


    fun generateParams() {
        
    }

    fun start() {

    }
}

suspend fun HttpClient.downloadFile(file: File, url: String, callback: suspend (boolean: Boolean) -> Unit) {
    val call = request<HttpResponse> {
        url(url)
        method = HttpMethod.Get
    }
    if (!call.status.isSuccess()) {
        callback(false)
    }
    call.content.copyAndClose(file.writeChannel())
    return callback(true)
}