import sys
content = open('app/src/main/java/com/example/basecamp/domain/model/User.kt', 'r', encoding='utf-8').read()
content = content.replace("val email: String", "val email: String,\n    val phone: String? = null,\n    val website: String? = null")
open('app/src/main/java/com/example/basecamp/domain/model/User.kt', 'w', encoding='utf-8').write(content)
