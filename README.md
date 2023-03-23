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

## LICENSE

    Copyright (c) 2016-present, RxJava Contributors.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
