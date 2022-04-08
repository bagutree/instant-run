plugins {
    java
    id("com.zzy.instant.run.plugin")
}

instantRunConfig {
    message.set("...start instant run...")
    tag.set("instant run")
    outputFile.set(File("instantRun.txt"))
}
