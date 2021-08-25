import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.file.FileSystems

fun main(args: Array<String>): Unit = runBlocking {
    Launcher.minecraftHome = FileSystems.getDefault().getPath("C:\\Users\\Igor\\Desktop\\DerpyLauncher\\")
    Launcher.createMinecraftHome()
    Launcher.versionsManifest = Launcher.getReleases()
    launch { Launcher.downloadReleasesToFileSystem() }
    val frame = LauncherWindow
    frame.isVisible = true
}