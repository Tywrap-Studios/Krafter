import dev.kordex.gradle.plugins.docker.file.*
import dev.kordex.gradle.plugins.kordex.DataCollection

plugins {
	distribution

	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.kotlin.serialization)

	alias(libs.plugins.detekt)

	alias(libs.plugins.kordex.docker)
	alias(libs.plugins.kordex.plugin)
	alias(libs.plugins.ksp.plugin)
}

group = "org.tywrapstudios.krafter"
version = "0.1.0-SNAPSHOT"

repositories {
	maven {
		name = "JitPack"
		url = uri("https://jitpack.io")
	}
	maven {
		name = "QuiltMC (Snapshots)"
		url = uri("https://maven.quiltmc.org/repository/snapshot/")
		// We need this because Quilt's Maven repo is... special I guess
		metadataSources {
			gradleMetadata()
			ignoreGradleMetadataRedirection()
		}
	}
}

dependencies {
	detektPlugins(libs.detekt)

	implementation(libs.kotlin.stdlib)
	implementation(libs.kx.ser)

	// Logging dependencies
	implementation(libs.groovy)
	implementation(libs.jansi)
	implementation(libs.logback)
	implementation(libs.logback.groovy)
	implementation(libs.logging)

	implementation(libs.bbapi)
	implementation(libs.bundles.cozy.modules)
	implementation(project(":module-ama"))
	implementation(project(":module-log-parser"))
	implementation(libs.bundles.database)
	implementation(libs.rcon)
	implementation(libs.excelkt)
}

// Configure distributions plugin
distributions {
	main {
		distributionBaseName = project.name

		contents {
			// Copy the LICENSE file into the distribution
			from("LICENSE")

			// Exclude src/main/dist/README.md
			exclude("README.md")
		}
	}
}

kordEx {
	kordExVersion = libs.versions.kordex.asProvider()

	bot {
		// See https://docs.kordex.dev/data-collection.html
		dataCollection(DataCollection.Standard)

		mainClass = "org.tywrapstudios.krafter.AppKt"

		voice = false
	}

	module("dev-unsafe")
	module("pluralkit")
	module("func-phishing")
	module("func-tags")
	module("func-welcome")
	// Currently unavailable
//    module("func-minecraft")

	i18n {
		classPackage = "org.tywrapstudios.krafter.i18n"
		translationBundle = "krafter.strings"
		outputDirectory = File("${project.projectDir}/src/main/kotlin")
	}
}

detekt {
	buildUponDefaultConfig = true

	config.from(rootProject.files("detekt.yml"))

	ignoreFailures = true
}

// Automatically generate a Dockerfile. Set `generateOnBuild` to `false` if you'd prefer to manually run the
// `createDockerfile` task instead of having it run whenever you build.
docker {
	// Create the Dockerfile in the root folder.
	file(rootProject.file("Dockerfile"))

	commands {
		// Each function (aside from comment/emptyLine) corresponds to a Dockerfile instruction.
		// See: https://docs.docker.com/reference/dockerfile/

		from("openjdk:21-jdk-slim")

		emptyLine()

		comment("Create required directories")
		runShell("mkdir -p /bot/plugins")
		runShell("mkdir -p /bot/data")
		runShell("mkdir -p /dist/out")

		emptyLine()

		// Add volumes for locations that you need to persist. This is important!
		comment("Declare required volumes")
		volume("/bot/data")  // Storage for data files
		volume("/bot/plugins")  // Plugin ZIP/JAR location

		emptyLine()

		comment("Copy the distribution files into the container")
		copy("build/distributions/${project.name}-${project.version}.tar", "/dist")

		emptyLine()

		comment("Extract the distribution files, and prepare them for use")
		runShell("tar -xf /dist/${project.name}-${project.version}.tar -C /dist/out")

		if (file("src/main/dist/plugins").isDirectory) {
			runShell("mv /dist/out/${project.name}-${project.version}/plugins/* /bot/plugins")
		}

		runShell("chmod +x /dist/out/${project.name}-${project.version}/bin/$name")

		emptyLine()

		comment("Clean up unnecessary files")
		runShell("rm /dist/${project.name}-${project.version}.tar")

		emptyLine()

		comment("Set the correct working directory")
		workdir("/bot")

		emptyLine()

		comment("Run the distribution start script")
		entryPointExec("/dist/out/${project.name}-${project.version}/bin/$name")
	}
}
