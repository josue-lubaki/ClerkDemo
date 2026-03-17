package ca.josuelubaki.ui.clerkdemo

import android.app.Application
import com.clerk.api.Clerk

class ClerkDemoApplication : Application() {
     override fun onCreate() {
        super.onCreate()

         // init clerk
         Clerk.initialize(
             this,
             publishableKey = BuildConfig.CLERK_PUBLISHABLE_KEY
         )
    }
}