# Mareano GeoExplorer

These instructions describe how to build the modified GeoExplorer  

## Getting a copy of the application

To get a copy of the application source code, use git:

    you@prompt:~$ git clone https://github.com/Norwegian-marine-datacentre/mareano.git

## Dependencies

Currently (May 2015) the application uses GeoExplorer 4.1

To pull in the correct GeoExplorer dependencies:

    you@prompt:~$ cd mareano/
    you@prompt:~/mareano$ git submodule update --init --recursive

## Building WAR file for deployment

    you@prompt:~$ cd mareano/
    you@prompt:~/mareano$ mvn -DskipTests clean install

At the end of a successful build the WAR file should be located:

    mareano/target/mareano.war