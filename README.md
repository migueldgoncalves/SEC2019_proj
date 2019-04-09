# SEC2019_proj
Projecto de SEC

## Instalar o pteidlibj.jar

Pôr o .jar na pasta root do projecto

mvn install:install-file -Dfile=pteidlibj.jar -DgroupId=tecnico -DartifactId=cclib -Dversion=1.0 -Dpackaging=jar

O groupId, o artifactId e a version podem ser mudados livremente, desde que se actualize a pom raiz com isso.

Depois já se pode fazer mvn clean install