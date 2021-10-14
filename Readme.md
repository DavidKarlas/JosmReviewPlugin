
# JOSM plugin for reviewing changes before upload

Main montivation for this plugin was semi-automatic import of [addresses/buildings in Slovenia](https://wiki.openstreetmap.org/wiki/Slovenia_Address_Import). We created [tool](https://github.com/DavidKarlas/GursAddressesForOSM/tree/master/OsmGursBuildingImport) that generated changeset for mapper to use for import, but mapper needs to review all buildings before uploading, because source can have demolished buildings or misaligned/wrong size...

Reviewing changes was hard, hence this plugin was created which allows quick navigation between changes on ways/relations ignoring untagged nodes.

## How to install

The Review plugin can be installed via JOSM plugin manager.
  * Open Preferences -> Plugins
  * Search for the plugin "Review Changes" and install it

## How to use

 1) After installing plugin new pad should appear "Review List" in bottom-right corner of window. If not use "Windows"->"Review List" in main bar of window to toggle.
 1) Once you want to review changes you made, click on "Start Review" button, list will be filled with all changes.
 1) Click on item in list and use Up and Down keyboard buttons to move quickly between items in list.
 1) Optionally you can use Spacebar key to toggle state(Reviewed/Not Reviewed) of item.

## How to develop

We use [Gradle plugin for developing JOSM plugins](https://github.com/floscher/gradle-josm-plugin) which simplifies things...
  * Install Gradle https://gradle.org/install/
  * Run `gradle w` to create wrapper in repo
  * Run `./gradlew run` to compile and run JOSM which loads plugin

### Debugging

  * Run `./gradlew debug`
  * In VSCode start debugging

What this will do is start JOSM with listening for debugger to connect on port 2019, and VSCode will connect to that port and start debugging.

## License
Since [JOSM](https://github.com/JOSM/josm) itself is GPL 3.0 it makes sense for this plugin to be too.