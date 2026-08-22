import sys

# Root build.gradle.kts
content = open('build.gradle.kts', 'r', encoding='utf-8').read()
if "org.jetbrains.kotlin.plugin.serialization" not in content:
    content = content.replace("id(\"org.jetbrains.kotlin.android\") version \"2.0.21\" apply false", "id(\"org.jetbrains.kotlin.android\") version \"2.0.21\" apply false\n    id(\"org.jetbrains.kotlin.plugin.serialization\") version \"2.0.21\" apply false")
open('build.gradle.kts', 'w', encoding='utf-8').write(content)

# App build.gradle.kts
content = open('app/build.gradle.kts', 'r', encoding='utf-8').read()
if "org.jetbrains.kotlin.plugin.serialization" not in content:
    content = content.replace("id(\"com.google.dagger.hilt.android\")", "id(\"com.google.dagger.hilt.android\")\n    id(\"org.jetbrains.kotlin.plugin.serialization\")")

if "kotlinx-serialization-json" not in content:
    content = content.replace("implementation(\"io.github.jan-tennert.supabase:postgrest-kt:2.0.0\")", "implementation(\"io.github.jan-tennert.supabase:postgrest-kt:2.0.0\")\n    implementation(\"org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3\")")
open('app/build.gradle.kts', 'w', encoding='utf-8').write(content)

