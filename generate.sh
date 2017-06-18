#!/bin/bash
# Generate all types of documents

java -jar target/portablexls-jar-with-dependencies.jar -t /Users/sp12571/Workspace/portablexls/src/main/resources/templateInformeSemanalGeneral.xls -s /Users/sp12571/Workspace/portablexls/src/main/resources/summary.csv -d /Users/sp12571/Workspace/portablexls/src/main/resources/dataInformeSemanalGeneral.csv -o informeSemanalGeneral.pdf
java -jar target/portablexls-jar-with-dependencies.jar -t /Users/sp12571/Workspace/portablexls/src/main/resources/templateInformeSemanalCalificaciones.xls -d /Users/sp12571/Workspace/portablexls/src/main/resources/dataInformeSemanalCalificaciones.csv -o informeSemanalCalificaciones0_9.pdf
java -jar target/portablexls-jar-with-dependencies.jar -t /Users/sp12571/Workspace/portablexls/src/main/resources/templateInformeSemanalCalificaciones.xls -i 10,19 -d /Users/sp12571/Workspace/portablexls/src/main/resources/dataInformeSemanalCalificaciones.csv -o informeSemanalCalificaciones10_19.pdf
java -jar target/portablexls-jar-with-dependencies.jar -t /Users/sp12571/Workspace/portablexls/src/main/resources/templateInformeSemanalCalificaciones.xls -i 90,94 -d /Users/sp12571/Workspace/portablexls/src/main/resources/dataInformeSemanalCalificaciones.csv -o informeSemanalCalificaciones90_94.pdf
