#!/bin/bash

java -cp ~/.m2/repository/org/hsqldb/hsqldb-j5/2.0.0/hsqldb-j5-2.0.0.jar org.hsqldb.Server -database.0 file:db/ -dbname.0 camel

