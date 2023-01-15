import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowCopyAction
import com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency
import shadow.org.apache.tools.zip.ZipEntry
import shadow.org.apache.tools.zip.ZipOutputStream
import com.github.jengelman.gradle.plugins.shadow.transformers.Transformer

plugins {
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.google.devtools.ksp") version "1.8.0-1.0.8"
    id("org.spongepowered.gradle.plugin") version "2.1.1"
}

group = "me.zodd"
version = "0.1.0-SNAPSHOT"

dependencies {
    //annotations
    ksp("dev.zacsweers.autoservice:auto-service-ksp:1.0.0")
    implementation("com.google.auto.service:auto-service-annotations:1.0.1")

    //Kotlin
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(kotlin("reflect"))

    //Configuration
    implementation("org.spongepowered:configurate-extra-kotlin:4.1.2") {
        isTransitive = false
    }

    //Storage
    implementation("org.mongodb:mongodb-driver-sync:4.8.1")
    implementation("org.litote.kmongo:kmongo:4.8.0")

    //tests
    testImplementation(kotlin("test"))
}

tasks.shadowJar {

    val group = "${project.group}.libs"

    relocate("org.bson", "$group.bson")
    relocate("com.mongodb", "$group.com.mongodb")
    relocate("org.spongepowered.configurate.kotlin", "$group.configurate.kotlin")
    relocate("com.fasterxml", "$group.fasterxml")
    relocate("de.undercouch", "$group.undercouch")

    relocate("org.litote", "$group.litote")
    //Update META-INF files for kmongo
    transform(ServiceTransformer())

    exclude("kotlin/**")
}

tasks {
    test {
        useTestNG()
    }
}

sponge {
    apiVersion("8.2.0-SNAPSHOT")
    license("CHANGEME")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("strax") {
        displayName("Strax")
        entrypoint("me.zodd.strax.Strax")
        description("Essentials plugin")
        links {
            homepage("https://github.com/DrZoddiak/Strax")
            source("https://github.com/DrZoddiak/Strax")
            issues("https://github.com/DrZoddiak/Strax/issues")
        }
        contributor("Zodd") {
            description("Lead Developer")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
        dependency("kruntime") {
            version("0.4.0")
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


tasks.withType(JavaCompile::class).configureEach {
    options.apply {
        encoding = "utf-8" // Consistent source file encoding
    }
}

tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

class ServiceTransformer : Transformer {

    private val replaceableGroup = "org.litote.kmongo"

    private val replacementGroup = "me.zodd.libs.litote.kmongo"

    private val servicesPattern = "META-INF/services/$replaceableGroup.**"

    private val serviceEntries = mutableMapOf("" to byteArrayOf())

    private val patternSet = PatternSet().include(servicesPattern)

    fun setPath(path: String) {
        patternSet.setIncludes(mutableListOf(path))
    }

    override fun getName(): String {
        return "ServiceTransformer"
    }

    override fun canTransformResource(element: FileTreeElement?): Boolean {
        val target = if (element is ShadowCopyAction.ArchiveFileTreeElement) element.asFileTreeElement() else element
        return patternSet.asSpec.isSatisfiedBy(target)
    }

    override fun transform(context: TransformerContext) {
        val targetPath = context.path.replace(replaceableGroup, replacementGroup)
        context.`is`.reader().buffered().readLines().forEach { line ->
            val line = line.substringAfterLast(" ").substringBeforeLast("]").takeIf {
                it.startsWith(replaceableGroup)
            }?.replace(replaceableGroup, replacementGroup) ?: return@forEach

            serviceEntries[targetPath] = line.encodeToByteArray()
        }
    }

    override fun hasTransformedResource(): Boolean {
        return serviceEntries.isNotEmpty()
    }

    override fun modifyOutputStream(zos: ZipOutputStream, p1: Boolean) {

        serviceEntries.forEach {
            val entry = ZipEntry(it.key)
            entry.time = TransformerContext.getEntryTimestamp(p1, entry.time)
            zos.putNextEntry(entry)
            org.codehaus.plexus.util.IOUtil.copy(it.value, zos)
            zos.closeEntry()
        }
    }
}