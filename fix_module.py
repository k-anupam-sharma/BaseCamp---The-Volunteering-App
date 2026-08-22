import sys
content = open('app/src/main/java/com/example/basecamp/di/SupabaseModule.kt', 'r', encoding='utf-8').read()
content = content.replace("install(Auth)", """install(Auth) {
                scheme = "basecamp"
                host = "login-callback"
            }
            install(io.github.jan.supabase.compose.auth.ComposeAuth)""")
open('app/src/main/java/com/example/basecamp/di/SupabaseModule.kt', 'w', encoding='utf-8').write(content)
