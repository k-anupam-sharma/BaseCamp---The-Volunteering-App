import sys
content = open(r'C:\Users\zc\.gemini\antigravity-ide\brain\4fe8e736-4b7d-41c6-a86f-749d4e142f9a\walkthrough.md', 'r', encoding='utf-8').read()
content += """\n
## Adding Google Sign-In

We have implemented a native Google Sign-In flow in the app. However, before you can test it, you **must** configure your Supabase Dashboard to allow Google Logins.

### Required Steps in Supabase Dashboard
1. Go to your [Supabase Dashboard](https://supabase.com/dashboard).
2. Select the `BaseCamp` project.
3. On the left sidebar, click on **Authentication**, then go to **Providers**.
4. Find **Google** in the list and click to expand it.
5. Toggle **Enable Sign in with Google** to ON.
6. Since we are using the secure browser flow (OAuth), you need to provide a **Client ID** and **Client Secret**. 
   * You can get these by creating an OAuth client in the [Google Cloud Console](https://console.cloud.google.com/apis/credentials).
   * For the "Authorized redirect URIs" in Google Cloud, copy the callback URL from your Supabase Dashboard (usually `https://[PROJECT_ID].supabase.co/auth/v1/callback`).
7. Save the configuration in Supabase.
8. Go to **URL Configuration** under Authentication -> Configuration.
9. In the **Site URL** or **Redirect URLs** section, add `basecamp://login-callback`. This tells Supabase to allow redirecting back to our Android app after a successful Google login!

### Testing the Code
You can now run the app. You'll see a white, high-contrast "CONTINUE WITH GOOGLE" button on the Login and Signup pages. Tapping it will open a secure Chrome Custom Tab, log the user in, and jump straight back into the app!"""
open(r'C:\Users\zc\.gemini\antigravity-ide\brain\4fe8e736-4b7d-41c6-a86f-749d4e142f9a\walkthrough.md', 'w', encoding='utf-8').write(content)
