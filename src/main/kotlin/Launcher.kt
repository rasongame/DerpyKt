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
        }
    }

    suspend fun getReleaseInfo(url: String): VersionManifest {
        val response: HttpResponse = this.httpClient.get(url)
        return format.decodeFromString(response.readText())
    }
    suspend fun getReleases(): VersionsManifest {
        val releases = if (!File("$minecraftHome/version_manifest.json").exists()) {
            val response: HttpResponse =
                this.httpClient.get("https://launchermeta.mojang.com/mc/game/version_manifest.json")
            format.decodeFromString<VersionsManifest>(response.readText())
        } else {
            val file = File("$minecraftHome/version_manifest.json")
            format.decodeFromString(file.readText())
        }
        return releases

    }
    fun checkCurrentOSInAllowList(r: LibraryInfo, os: String): Boolean {
        // возращает true если эта либа необходима текущей
        r.rules.forEach {
            if (it.action == "allow" && it.os["name"] == os || it.os["name"] == os) {
                return true
            }
        }
        return true
    }
    suspend fun downloadLibraries(m: VersionManifest) {
        val currentOS = getOS()
        if(FileSystems.getDefault().getPath("$minecraftHome/libraries").listDirectoryEntries().isNotEmpty()) {
            println("Libraries downloaded?")
            return
        }
        m.libraries.forEach { it ->

            when (currentOS) {
                OS.WINDOWS -> {
                    if (it.downloads.classifiers.natives_windows.url.isNotBlank() && checkCurrentOSInAllowList(it, "windows")) {
                            val native = File("$minecraftHome/libraries/${it.downloads.classifiers.natives_windows.path}")
                            native.parentFile.mkdirs()
                            httpClient.downloadFile(
                                native,
                                it.downloads.classifiers.natives_windows.url
                            ) { success ->
                                println("Downloading native-jar ${it.name}.... ${if (!success) "FAIL!" else "SUCCESS!"}")
                            }
                    }
                }
                OS.LINUX -> {
                    if (it.downloads.classifiers.natives_linux.url.isNotEmpty() &&!checkCurrentOSInAllowList(it, "linux")) {
                        var native = File("$minecraftHome/libraries/${it.downloads.classifiers.natives_linux.path}")
                        native.parentFile.mkdirs()
                        httpClient.downloadFile(native, it.downloads.classifiers.natives_linux.url) { success ->
                            println("Downloading native-jar ${it.name}.... ${if (!success) "FAIL!" else "SUCCESS!"}")
                        }
                    }
                }
                OS.MAC -> {
                    if (it.downloads.classifiers.natives_macos.url.isNotEmpty() && !checkCurrentOSInAllowList(it, "osx")) {
                        File("$minecraftHome/libraries/${it.downloads.classifiers.natives_macos.path}").parentFile.mkdirs()
                        httpClient.downloadFile(
                            File("$minecraftHome/libraries/${it.downloads.classifiers.natives_macos.path}"),
                            it.downloads.classifiers.natives_macos.url
                        ) { success ->
                            println("Downloading native-jar ${it.name}.... ${if (!success) "FAIL!" else "SUCCESS!"}")
                        }
                    }
                }
                else -> println("Unused")
            }
            File("$minecraftHome/libraries/${it.downloads.artifact.path}").parentFile.mkdirs()
            if (checkCurrentOSInAllowList(it, convertOsName(currentOS))) {
                httpClient.downloadFile(
                    File("$minecraftHome/libraries/${it.downloads.artifact.path}"),
                    it.downloads.artifact.url
                )
                { success ->
                    println("Downloading ${it.name}.... ${if (!success) "FAIL!" else "SUCCESS!"}")
                }
            }
        }

        println("End of downloading libraries.")
    }

    suspend fun downloadClient(m: VersionManifest) {
        println("Downloading client ${m.id}")
        downloadLibraries(m)


    }
    suspend fun downloadReleasesToFileSystem() {
        if(File("$minecraftHome/version_manifest.json").exists()) { return }
        val response: HttpResponse =
            this.httpClient.get("https://launchermeta.mojang.com/mc/game/version_manifest.json")
            File("$minecraftHome/version_manifest.json").printWriter().use { out ->
                out.println(response.readText())
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