plugins {
	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.kotlin.serialization)

	alias(libs.plugins.detekt)

	alias(libs.plugins.kordex.plugin)
}

dependencies {
	detektPlugins(libs.detekt)
	detektPlugins(libs.detekt.libraries)

	implementation(libs.logging)

	implementation(libs.kotlin.stdlib)
}

kordEx {
	module("pluralkit")
	module("dev-unsafe")

	i18n {
		classPackage = "org.quiltmc.community.cozy.modules.ama.i18n"
		translationBundle = "ama.strings"
		outputDirectory = File("${project.projectDir}/src/main/kotlin/org/quiltmc/community/cozy/modules/ama/i18n")
	}
}

detekt {
	buildUponDefaultConfig = true

	config.from(rootProject.files("detekt.yml"))

	ignoreFailures = true
}
