import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = runBlocking {
    Launcher.createMinecraftHome()
    Launcher.versionsManifest = Downloader.getReleases()
    launch { Downloader.downloadReleasesToFileSystem() }
    val frame = LauncherWindow
    frame.isVisible = true
}