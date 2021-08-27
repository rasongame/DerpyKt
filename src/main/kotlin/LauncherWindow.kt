import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.FlowLayout
import java.io.File
import javax.swing.*


object LauncherWindow: JFrame() {
    private lateinit var settingsPanel: JPanel
    private lateinit var launchPanel: JPanel

    init {
        createUI("Derpy")

    }
    private fun createUI(title: String) {

        setTitle(title)
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(400, 500)
        setLocationRelativeTo(null)
        createMainPanel()
        createSettingsPanel()
        this.contentPane = launchPanel


    }
    private fun createMainPanel() {
        this.launchPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        val launch = JButton("Запуск")
        val versionList = JComboBox( Launcher.versionsManifest.versions.map{ it.id }.toTypedArray())
        val nickname = JTextField(16)
        val password = JPasswordField(32)
        val openSettingsButton = JButton("Настройки")
        openSettingsButton.addActionListener { contentPane = settingsPanel; validate(); repaint() }
        launch.addActionListener {
            GlobalScope.launch {
                Downloader.downloadClient(Downloader.getReleaseInfo(Launcher.versionsManifest.versions[0].url))
            }
        }
        launchPanel.add(nickname)
        launchPanel.add(password)
        launchPanel.add(versionList)
        launchPanel.add(launch)
        launchPanel.add(openSettingsButton)



    }
    private fun createSettingsPanel() {
        this.settingsPanel = JPanel(FlowLayout(FlowLayout.CENTER))
        val ramLimitCounter = JTextField(Launcher.javaMemoryLimit)

        var JavaPathLabel = JLabel("Путь к JRE: ${Launcher.javaHome}")
        var javaArgsLabel = JLabel("Параметры JVM: ")
        var javaArgs = JTextField("")
        var memoryLimit = JLabel("Сколько памяти выделить JVM:")
        val javaPathChooseButton = JButton("Выбрать путь к Java")
        val backButton = JButton("Вернуться")
        javaPathChooseButton.addActionListener {
            val fileChooser = JFileChooser()
            fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            val returnVal: Int = fileChooser.showSaveDialog(this)
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                val yourFolder: File = fileChooser.getSelectedFile()
                JavaPathLabel.text = yourFolder.absolutePath
                Launcher.javaHome = yourFolder.absolutePath
            }
        }
        backButton.addActionListener{
            contentPane = launchPanel;
            validate();
            repaint()
        }
        ramLimitCounter.addActionListener {
            val isHavingSuffix = ramLimitCounter.text.endsWith("M", true)
            if (!isHavingSuffix) {
                Launcher.javaMemoryLimit = ramLimitCounter.text.plus("M")
                ramLimitCounter.text = ramLimitCounter.text.plus("M")
            }
        }
        settingsPanel.add(JavaPathLabel)
        settingsPanel.add(javaPathChooseButton)
        settingsPanel.add(memoryLimit)
        settingsPanel.add(ramLimitCounter)
        settingsPanel.add(javaArgsLabel)
        settingsPanel.add(javaArgs)
        settingsPanel.add(backButton)
    }

}