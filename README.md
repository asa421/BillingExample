# BillingExample

  An example of an application architecture for working with a billing system from different systems. Configuration for different systems is carried out in the build settings **gradlr**.

  Since different stores use their own billing system for users, a different assembly has been added for each store in the **gradle** settings. But be careful for different systems, you need additional configuration, read the `gradle.properties` instructions! When changing the application package, do not forget to `import new.application.id...` the necessary classes, mainly the `BuildConfig` and `ViewBinding` assembly settings.

## ImplementationExample:
 - Implementation example with **RxJava** [here asrx](../main/asrx)! <img src="https://reactivex.io/favicon.ico" title="RxJava" alt="RxJava" width="14" height="14"/>
 - Implementation example with **Coroutine&Flow** [here asflow](../main/asflow)! <img src="https://kotlinlang.org/assets/images/favicon.ico?v2" title="Coroutine and Flow" alt="Coroutine and Flow" width="14" height="14"/> (Sorry, still under development.)
 
 ## :hammer_and_wrench: Project Diagram:
 ![alt text][diagram]

[diagram]: ../main/diagrams/billing-system-architecture.png "Project Diagram"

[Project Diagram](https://drive.google.com/file/d/1ks6ILqvp5bVZgsToIOLjIeDZd3oZGEkW/view?usp=sharing)
