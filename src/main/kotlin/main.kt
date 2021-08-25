import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = runBlocking {
    Launcher.createMinecraftHome()
    Launcher.versionsManifest = Launcher.getReleases()
    launch { Launcher.downloadReleasesToFileSystem() }
    val frame = LauncherWindow
    frame.isVisible = true
}