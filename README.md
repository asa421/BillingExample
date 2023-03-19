# BillingExample

  An example of an application architecture for working with a billing system from different systems. Configuration for different systems is carried out in the build settings **gradlr**.

  Since different stores use their own billing system for users, a different assembly has been added for each store in the **gradle** settings. But be careful for different systems, you need additional configuration, read the **gradle.properties** instructions! When changing the application package, do not forget to `import new.application.id...` the necessary classes, mainly the **BuildConfig** and **ViewBinding** assembly settings.
