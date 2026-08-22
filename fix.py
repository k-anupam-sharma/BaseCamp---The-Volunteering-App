import sys, re
content = open('app/build.gradle.kts', 'r', encoding='utf-8').read()
new_content = re.sub(
    r'defaultConfig \{.*?\}[\s`"A-Za-z=\.\(\)\\$,_]+?\}\s+buildTypes',
    '''defaultConfig {
        applicationId = "com.example.basecamp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val rawUrl = localProperties.getProperty("SUPABASE_URL", "https://PLACEHOLDER.supabase.co").replace("\\"", "")
        val rawKey = localProperties.getProperty("SUPABASE_KEY", "PLACEHOLDER_KEY").replace("\\"", "")
        buildConfigField("String", "SUPABASE_URL", "\\"${rawUrl}\\"")
        buildConfigField("String", "SUPABASE_KEY", "\\"${rawKey}\\"")
    }

    buildTypes''',
    content,
    flags=re.DOTALL
)
open('app/build.gradle.kts', 'w', encoding='utf-8').write(new_content)
