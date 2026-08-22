import sys
content = open('app/src/main/java/com/example/basecamp/presentation/auth/AuthViewModel.kt', 'r', encoding='utf-8').read()

content = content.replace("fun signup(name: String, email: String, password: String, role: String) {", "fun signup(name: String, email: String, password: String, role: String, phone: String? = null, website: String? = null) {")

content = content.replace("""val newUser = User(
                    id = userId,
                    name = name,
                    role = role,
                    email = email
                )""", """val newUser = User(
                    id = userId,
                    name = name,
                    role = role,
                    email = email,
                    phone = phone,
                    website = website
                )""")

open('app/src/main/java/com/example/basecamp/presentation/auth/AuthViewModel.kt', 'w', encoding='utf-8').write(content)
