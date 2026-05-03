# Adding New Modules to LLD Project

This guide explains how to add a new module to the LLD project following the established patterns.

## Steps to Add a New Module

### 1. Create Module Directory Structure

Create a new directory at the project root:
```bash
mkdir -p /LLD/YourModule/src/main/java/com/lld/yourmodule
```

### 2. Create pom.xml

Create a `pom.xml` file in the module directory with the following structure:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.lld</groupId>
        <artifactId>lld-parent</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <artifactId>yourmodule</artifactId>
    <packaging>jar</packaging>
    <name>YourModule</name>
    
    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>1.18.30</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

**Important:**
- Use lowercase `artifactId` (e.g., `yourmodule`)
- Use TitleCase `name` (e.g., `YourModule`)

### 3. Update Parent pom.xml

Add your module to the parent `pom.xml` at `/LLD/pom.xml`:

```xml
<modules>
    <module>ParkingLot</module>
    <module>TicTacToe</module>
    <module>ChessEngine</module>
    <module>Elevator</module>
    <module>InventoryManagement</module>
    <module>YourModule</module>
</modules>
```

### 4. Create IntelliJ Module File

Create `.iml` file at `/LLD/YourModule/yourmodule.iml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/src/main/java" isTestSource="false" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
    <orderEntry type="library" name="Maven: org.projectlombok:lombok:1.18.30" level="project" />
  </component>
</module>
```

### 5. Update .idea/modules.xml

Add your module to `/LLD/.idea/modules.xml`:

```xml
<module fileurl="file://$PROJECT_DIR$/YourModule/yourmodule.iml" filepath="$PROJECT_DIR$/YourModule/yourmodule.iml" />
```

### 6. Update .idea/compiler.xml

Add your module to the annotation processing profile in `/LLD/.idea/compiler.xml`:

```xml
<module name="yourmodule" />
```

### 7. Create Run Configuration

Create `/LLD/.idea/runConfigurations/YourModule.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<component name="ProjectRunConfigurationManager">
  <configuration default="false" name="YourModule" type="Application" factoryName="Application" nameIsGenerated="true">
    <option name="MAIN_CLASS_NAME" value="com.lld.yourmodule.Main" />
    <module name="yourmodule" />
    <option name="WORKING_DIRECTORY" value="$PROJECT_DIR$/YourModule" />
    <extension name="coverage">
      <pattern>
        <option name="PATTERN" value="com.lld.yourmodule.*" />
        <option name="ENABLED" value="true" />
      </pattern>
    </extension>
    <method v="2">
      <option name="Make" enabled="true" />
    </method>
  </configuration>
</component>
```

### 8. Create Standard Files

Create the following files in your module directory:

- **README.md** - Module documentation
- **Sequence.puml** - PlantUML sequence diagram
- **UMLClass.puml** - PlantUML class diagram

### 9. Implement Main Class

Create `Main.java` at `src/main/java/com/lld/yourmodule/Main.java` with:

- Demo/manual mode interface
- Logging setup pointing to `../../logs/YourModule/`
- Standard package structure

### 10. Use Lombok

Use Lombok annotations for getters/setters instead of manual implementations:

```java
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YourClass {
    private String field;
}
```

### 11. Logging Configuration

Set up logging to use the common logs folder:

```java
private static void setupLogging() {
    try {
        String logPath = System.getProperty("user.dir") + "/../../logs/YourModule/yourmodule.log";
        FileHandler fileHandler = new FileHandler(logPath, true);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
    } catch (IOException e) {
        System.err.println("Failed to setup logging: " + e.getMessage());
    }
}
```

### 12. Verify Build

Compile the project to ensure everything works:

```bash
mvn clean compile
```

## Naming Conventions

- **Module directory**: TitleCase (e.g., `YourModule`)
- **artifactId**: lowercase (e.g., `yourmodule`)
- **Package name**: lowercase (e.g., `com.lld.yourmodule`)
- **README.md**: `README.md`
- **PlantUML files**: `Sequence.puml`, `UMLClass.puml`

## Folder Structure

```
YourModule/
├── README.md
├── Sequence.puml
├── UMLClass.puml
├── pom.xml
├── yourmodule.iml
└── src/
    └── main/
        └── java/
            └── com/
                └── lld/
                    └── yourmodule/
                        └── Main.java
```

## Common Patterns

All modules should follow these patterns:
- Demo/manual mode interface in Main class
- Centralized logging to `../../logs/ModuleName/`
- Lombok for getters/setters
- Standard Maven structure
- Java 17 compatibility
- Parent pom inheritance
