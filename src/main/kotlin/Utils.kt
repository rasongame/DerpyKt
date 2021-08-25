import java.util.*
import kotlin.math.absoluteValue

enum class OS {
    WINDOWS , LINUX, MAC, SOLARIS
}
fun convertOsName(os: OS?): String {
    return when(os) {
        OS.WINDOWS -> {
            "windows"
        }
        OS.LINUX -> {
            "linux"
        }
        OS.MAC -> {
            "osx"
        }
        else -> "null"
    }
}
fun getOS(): OS? {
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.contains("win") -> {
            OS.WINDOWS
        }
        os.contains("nix") || os.contains("nux") || os.contains("aix") -> {
            OS.LINUX
        }
        os.contains("mac") -> {
            OS.MAC
        }
        os.contains("sunos") -> {
            OS.SOLARIS
        }
        else -> null
    }
}