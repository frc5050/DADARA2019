# DeepSpace2019

Team 5050's 2019 Robot code for Clefairy. This README is a guide for every step from installing to running.

## Setup instructions

### General

* Ensure that you have the necessary prerequisites setup (WPILib)
* Make sure that you have git (or a git client such as GitHub Desktop) installed
  * For Windows users, Git for Windows can be found [here](https://git-scm.com/download/win)
* Clone this repository
  * To clone from a terminal/Command Prompt/PowerShell
    1) Change directories to wherever you would like the project to be placed by entering `cd C:/Users/YOUR_USER_NAME/robotics`
    2) Enter the command `git clone https://github.com/CowTown5050/DeepSpace2019.git`
    3) To clone a particular branch, the command is simply `git clone -b THE_BRANCH_NAME_HERE https://github.com/CowTown5050/DeepSpace2019.git`
  * If using another application such as GitHub Desktop, there should be a `clone` button somewhere on their interface.

### Code Style

More detail can be found in the saved configuration file for IntelliJ, but the general outline is as follows:
* Local and instance variables are in lowerCamelCase
* Class names, enum names, and file names are in UpperCamelCase
* Constants and enum entries are named in ALL_UPPER_CASE
    * If you don't know if something is a true constant, please ask before assuming.
        * [Google's Java Style Guide](), which we largely adhere to, says that
        > Constants are static final fields whose contents are deeply immutable and whose methods have no detectable side effects. This includes primitives, Strings, immutable types, and immutable collections of immutable types. If any of the instance's observable state can change, it is not a constant. Merely intending to never mutate the object is not enough."
        

### Building, Deploying, and Testing

* VS Code
    * `Ctrl+Shift+P` brings up the action bar at the top, simply select `>WPILib: Build Robot Code`, or `>WPILib: Deploy Robot Code` respectively
* IntelliJ IDEA
    * There should be pre-made configurations near the upper mid-right area of the screen, where you can simply select them and click the `Run` button
* All
    * `gradlew build` 
    * `gradlew deploy` 
    * `gradlew test` 
    * Note for Linux users, `gradlew` will probably have to be replaced with `./gradlew`
    
    
[//]: # (TODO add information on changing team number, updating rio, radio, talons, sparks, and the different packages and testing setup)