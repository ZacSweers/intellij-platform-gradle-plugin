// Copyright 2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.intellij.platform.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.provider.ProviderFactory
import org.jetbrains.intellij.platform.gradle.IntelliJPluginConstants.Extensions
import org.jetbrains.intellij.platform.gradle.IntelliJPluginConstants.PLUGIN_SETTINGS_ID
import org.jetbrains.intellij.platform.gradle.extensions.IntelliJPlatformRepositoriesExtension
import org.jetbrains.intellij.platform.gradle.utils.Logger
import javax.inject.Inject

abstract class IntelliJPlatformSettingsPlugin @Inject constructor(
    private val providers: ProviderFactory,
) : Plugin<Settings> {

    private val log = Logger(javaClass)

    override fun apply(settings: Settings) {
        log.info("Configuring plugin: $PLUGIN_SETTINGS_ID")

        @Suppress("UnstableApiUsage")
        with(settings.dependencyResolutionManagement.repositories) {
            configureExtension<IntelliJPlatformRepositoriesExtension>(Extensions.INTELLIJ_PLATFORM, this, providers)
        }
    }
}
