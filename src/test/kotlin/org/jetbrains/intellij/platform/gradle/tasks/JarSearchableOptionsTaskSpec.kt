// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.intellij.platform.gradle.tasks

import org.jetbrains.intellij.platform.gradle.IntelliJPluginConstants.Tasks
import org.jetbrains.intellij.platform.gradle.SearchableOptionsSpecBase
import kotlin.io.path.exists
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JarSearchableOptionsTaskSpec : SearchableOptionsSpecBase() {

    @Test
    fun `jar searchable options produces archive`() {
        pluginXml.xml(getPluginXmlWithSearchableConfigurable())
        buildFile.kotlin(
            """
            intellijPlatform {
                buildSearchableOptions = true
            }
            """.trimIndent()
        )
        getTestSearchableConfigurableJava().java(getSearchableConfigurableCode())

        build(Tasks.JAR_SEARCHABLE_OPTIONS)

        buildDirectory.resolve("libsSearchableOptions").let {
            assertTrue(it.exists())
            assertEquals(setOf("/lib/searchableOptions-1.0.0.jar"), collectPaths(it))
        }
    }
}
