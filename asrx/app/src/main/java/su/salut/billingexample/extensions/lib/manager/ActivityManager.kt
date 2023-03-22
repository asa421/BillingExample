package su.salut.billingexample.extensions.lib.manager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * The manager monitors activity changes.
 *
 * @date 14.01.2022
 * @author asa421
 */
class ActivityManager {
    // Keep track of current activity
    private var currentActivityWeakReference: WeakReference<Activity>? = null
    // ActivityLifecycleCallback methods
    private val activityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { }
        override fun onActivityStarted(activity: Activity) { setActivity(activity) }
        override fun onActivityResumed(activity: Activity) { setActivity(activity) }
        override fun onActivityPaused(activity: Activity) { }
        override fun onActivityStopped(activity: Activity) { }
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }
        override fun onActivityDestroyed(activity: Activity) {
            // We free up resources, we no longer need it
            if (getActivity() == activity) currentActivityWeakReference = null
        }
    }

    private fun setActivity(activity: Activity) {
        currentActivityWeakReference = WeakReference(activity)
    }

    fun getActivity(): Activity? {
        return currentActivityWeakReference?.get()
    }

    fun register(application: Application) {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }
}