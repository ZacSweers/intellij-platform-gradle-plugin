// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.intellij.platform.gradle.provider

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import org.jetbrains.intellij.platform.gradle.model.ProductInfo
import org.jetbrains.intellij.platform.gradle.model.launchFor
import org.jetbrains.intellij.platform.gradle.utils.asPath
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.io.path.absolutePathString

/**
 * Obtains the architecture of the provided Java Runtime executable by requesting the list of its internal properties.
 *
 * It is used to properly pick the [ProductInfo.Launch] when calling the [ProductInfo.launchFor] helper method.
 */
abstract class ExecutableArchValueSource : ValueSource<String, ExecutableArchValueSource.Parameters> {

    @get:Inject
    abstract val execOperations: ExecOperations

    interface Parameters : ValueSourceParameters {
        /**
         * Java Runtime executable.
         */
        val executable: RegularFileProperty
    }

    override fun obtain() = ByteArrayOutputStream().use { os ->
        execOperations.exec {
            commandLine(
                parameters.executable.get().asPath.absolutePathString(),
                "-XshowSettings:properties",
                "-version",
            )
            errorOutput = os
        }

        os.toString().lines()
            .find { it.trim().startsWith("os.arch") }
            ?.substringAfter(" = ")
    }
}
