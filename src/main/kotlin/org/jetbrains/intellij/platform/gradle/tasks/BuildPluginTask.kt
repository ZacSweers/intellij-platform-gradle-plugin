// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.intellij.platform.gradle.tasks

import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.named
import org.gradle.work.DisableCachingByDefault
import org.jetbrains.intellij.platform.gradle.IntelliJPluginConstants.PLUGIN_GROUP_NAME
import org.jetbrains.intellij.platform.gradle.IntelliJPluginConstants.Tasks

/**
 * Assembles a plugin and prepares ZIP archive for deployment.
 *
 * @see [Zip]
 */
@Deprecated(message = "CHECK")
@DisableCachingByDefault(because = "Zip based tasks do not benefit from caching")
abstract class BuildPluginTask : Zip() {

    init {
        group = PLUGIN_GROUP_NAME
        description = "Assembles plugin and prepares ZIP archive for deployment."
    }

    companion object {
        fun register(project: Project) =
            project.registerTask<BuildPluginTask>(Tasks.BUILD_PLUGIN) {
                val prepareSandboxTaskProvider = project.tasks.named<PrepareSandboxTask>(Tasks.PREPARE_SANDBOX)
                val jarSearchableOptionsTaskProvider = project.tasks.named<JarSearchableOptionsTask>(Tasks.JAR_SEARCHABLE_OPTIONS)

                archiveBaseName.convention(prepareSandboxTaskProvider.flatMap { prepareSandboxTask ->
                    prepareSandboxTask.pluginName
                })

                from(prepareSandboxTaskProvider.flatMap { prepareSandboxTask ->
                    prepareSandboxTask.pluginName.map {
                        prepareSandboxTask.destinationDir.resolve(it)
                    }
                })
                from(jarSearchableOptionsTaskProvider.flatMap { jarSearchableOptionsTask ->
                    jarSearchableOptionsTask.archiveFile
                }) {
                    into("lib")
                }
                into(prepareSandboxTaskProvider.flatMap { prepareSandboxTask ->
                    prepareSandboxTask.pluginName
                })

                dependsOn(jarSearchableOptionsTaskProvider)
                dependsOn(prepareSandboxTaskProvider)

//            project.artifacts.add(Dependency.ARCHIVES_CONFIGURATION, this).let { publishArtifact ->
//                extensions.getByType<DefaultArtifactPublicationSet>().addCandidate(publishArtifact)
//                project.components.add(IntelliJPlatformPluginLibrary())
//            }
            }
    }
}