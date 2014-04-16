# jsendnsca

[![Build Status](https://travis-ci.org/Berico-Technologies/jsendnsca.svg)](https://travis-ci.org/Berico-Technologies/jsendnsca)

A fork of the [jsendnsca](https://code.google.com/p/jsendnsca/) library on Google Code.

```
<dependency>
    <groupId>fork.com.googlecode.jsendnsca</groupId>
    <artifactId>jsendnsca</artifactId>
    <version>2.2</version>
</dependency>


<repositories>
    <repository>
        <id>nexus.bericotechnologies.com</id>
        <name>Berico Technologies Nexus</name>
        <url>http://nexus.bericotechnologies.com/content/groups/public</url>
    </repository>
</repositories>
```

## Enhancements

- Added PerfDatum, which represents a single entry in the PerfData spec Nagios/Icinga use.
- Added PerfDatumBuilder to easily construct PerfData
- Modified the MessagePayload to output a correctly formatted body including PerfData.
- Added the project into our Nexus repository.
