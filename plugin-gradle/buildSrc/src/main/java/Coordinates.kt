object PluginCoordinates {
    const val ID = "com.zzy.instant.run.plugin"
    const val GROUP = "com.zzy.instant.run"
    const val VERSION = "1.0.0"
    const val IMPLEMENTATION_CLASS = "com.zzy.instant.run.plugin.TemplatePlugin"
}

object PluginBundle {
    const val VCS = "https://github.com/cortinico/kotlin-gradle-plugin-template"
    const val WEBSITE = "https://github.com/cortinico/kotlin-gradle-plugin-template"
    const val DESCRIPTION = "增量编译插件"
    const val DISPLAY_NAME = "加快项目构建，秒运行"
    val TAGS = listOf(
        "plugin",
        "gradle",
        "instant run"
    )
}

