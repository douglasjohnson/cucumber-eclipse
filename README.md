# Cucumber-Eclipse

Eclipse plugin for [Cucumber](http://cukes.info).

Installation and further information
====================================

Please head over to the [plugin website](http://cucumber.github.com/cucumber-eclipse) for more information.

After you install the Cucumber-Eclipse plugin, you can use it to run Cucumber-JVM. To do this, you will need to install all the libraries you want to use for Cucumber-JVM into your Eclipse project's build-path libraries. The likely candidates and their locations are in the download target at the [cucumber-java-skeleton](https://github.com/cucumber/cucumber-java-skeleton/blob/master/build.xml) example at GitHub.

Create a new feature file by selecting New => File from the menu and naming it with a ".feature" suffix to bring up the Feature Editor. After typing in the Gherkin code for a test, select Run => Run to invoke Cucumber-JVM on that feature. This will also create a run configuration, which you can modify and rename by selecting Run => Run Configurarations.... Tags are not available in Cucumber-Eclipse, but you can organize your features into directories and select the Feature Path that you want the run configuration to use. You can execute run configurations from the Run => Run History menu.

Another alternative is to use Cucumber-Eclipse for editing feature files and getting the generated step-definition stubs, but then running a Junit file with a @RunWith(cucumber.class) annotation similar to the cucumber-java-skeleton [RunCukesTest.java](https://github.com/cucumber/cucumber-java-skeleton/blob/master/src/test/java/skeleton/RunCukesTest.java). The @CucumberOptions most useful are

* Run the feature or all features below the directory
  ```gherkin
  features = {"featurePath/dir1", "featurePath2/dir/one_more.feature", ...}
  ```

* Run all features with the given tag
  ```gherkin
  tags = {"@tag1", "@tag2", ...}
  ```

* Use the listed formatter
  ```gherkin
  format = "progress"
  ```

* Find the step definition and hooks below the given directory
  ```gherkin
  glue = "my_feature_steps/dir"
  ```

The full option list can be found at [CucumberOptions](https://github.com/cucumber/cucumber-jvm/blob/master/core/src/main/java/cucumber/api/CucumberOptions.java)

Screenshots and Features of the plugin
======================================
Please consult the [wiki](https://github.com/cucumber/cucumber-eclipse/wiki) for a full list for currently available features and screenshots.
eg [Syntax Highlighting](https://github.com/cucumber/cucumber-eclipse/wiki/I18n-Syntax-highlighting)

Build and install from source
=============================

To use the latest features, you can choose to build and install from source.

- Build the plugin using Maven (https://maven.apache.org/) <code>mvn clean install</code>
- Open Eclipse and navigate to Help -> Install New Software... -> Add
- Point to the update-site built in step 1: <code>file:path_to_repo/cucumber.eclipse.p2updatesite/target/repository</code>
- Proceed to install like any other plug-in