# NATS-demo
# Gradle Migration Guide

This project has been successfully migrated from Maven to Gradle. This guide explains the changes and how to use the new Gradle-based build system.

## Migration Summary

### Files Added
- `build.gradle` - Main Gradle build configuration
- `settings.gradle` - Gradle project settings
- `gradle.properties` - Gradle build properties
- `gradlew` - Gradle wrapper for Unix/Linux/macOS
- `gradlew.bat` - Gradle wrapper for Windows
- `gradle/wrapper/gradle-wrapper.properties` - Gradle wrapper configuration
- `gradle/wrapper/gradle-wrapper.jar` - Gradle wrapper JAR
- `test-iso-conversion-gradle.sh` - Updated test script for Gradle

### Files Removed
- `pom.xml` - Maven configuration (replaced by build.gradle)
- `mvnw` - Maven wrapper (replaced by gradlew)
- `mvnw.cmd` - Maven wrapper for Windows (replaced by gradlew.bat)

## Quick Start

### 1. Build the Project
```bash
./gradlew build
```

### 2. Run the Application
```bash
./gradlew bootRun
```

### 3. Test ISO Conversion
```bash
./gradlew testIsoConversion
```

### 4. Run Full Demo
```bash
./gradlew runDemo
```

## Gradle Commands

### Basic Commands
```bash
# Build the project
./gradlew build

# Clean build artifacts
./gradlew clean

# Run tests
./gradlew test

# Run the application
./gradlew bootRun

# Build JAR file
./gradlew bootJar
```

### Custom Tasks
```bash
# Run the application (custom task)
./gradlew runApp

# Test ISO conversion
./gradlew testIsoConversion

# Start NATS server
./gradlew startNatsServer

# Run full demo with NATS
./gradlew runDemo
```

### Development Commands
```bash
# Continuous build and run
./gradlew bootRun --continuous

# Run with debug logging
./gradlew bootRun --args='--logging.level.com.example.natsdemo=DEBUG'

# Build without tests
./gradlew build -x test
```

## Configuration Comparison

### Maven (pom.xml) → Gradle (build.gradle)

| Maven | Gradle |
|-------|--------|
| `<groupId>` | `group` |
| `<artifactId>` | `rootProject.name` |
| `<version>` | `version` |
| `<java.version>` | `sourceCompatibility` |
| `<dependencies>` | `dependencies` |
| `<build><plugins>` | `plugins` |

### Dependencies Mapping

**Maven:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**Gradle:**
```gradle
implementation 'org.springframework.boot:spring-boot-starter-web'
```

## Testing with Gradle

### Automated Testing
```bash
# Run the Gradle test script
./test-iso-conversion-gradle.sh
```

### Manual Testing
```bash
# Start the application
./gradlew bootRun

# In another terminal, test the endpoints
curl http://localhost:8080/api/iso/health
curl http://localhost:8080/api/iso/sample-iso8583
```

### Custom Test Task
The `testIsoConversion` task automatically:
1. Starts the Spring Boot application
2. Waits for it to be ready
3. Runs the ISO conversion tests
4. Reports results

## Build Configuration

### build.gradle Features
- **Spring Boot Plugin**: Automatic Spring Boot configuration
- **Dependency Management**: Automatic version management
- **Custom Tasks**: Specialized tasks for ISO conversion testing
- **Java 11**: Configured for Java 11 compatibility
- **NATS Integration**: All NATS dependencies included

### gradle.properties
- **Performance**: Parallel builds and caching enabled
- **Memory**: Optimized JVM settings
- **Spring Profiles**: Default profile configuration

## Migration Benefits

### 1. **Performance**
- Faster incremental builds
- Parallel task execution
- Build caching

### 2. **Flexibility**
- Custom tasks for specific workflows
- Better dependency management
- More readable build scripts

### 3. **Integration**
- Better IDE support
- Continuous build support
- Enhanced testing capabilities

### 4. **Maintenance**
- Simpler dependency declarations
- Better error messages
- More intuitive task system

## Troubleshooting

### Common Issues

#### 1. **Permission Denied**
```bash
chmod +x gradlew
```

#### 2. **Gradle Wrapper Not Found**
```bash
# Download wrapper manually
curl -o gradle/wrapper/gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/v7.6.1/gradle/wrapper/gradle-wrapper.jar
```

#### 3. **Build Failures**
```bash
# Clean and rebuild
./gradlew clean build
```

#### 4. **Dependency Issues**
```bash
# Refresh dependencies
./gradlew --refresh-dependencies build
```

### Debug Mode
```bash
# Run with debug output
./gradlew bootRun --debug

# Show task dependencies
./gradlew tasks --all
```

## IDE Integration

### IntelliJ IDEA
1. Open the project
2. Import Gradle project
3. Sync Gradle files
4. Run configurations will be automatically created

### Eclipse
1. Import existing Gradle project
2. Buildship plugin will handle Gradle integration
3. Run configurations available

### VS Code
1. Install Gradle extension
2. Open project folder
3. Use Gradle tasks panel

## Migration Checklist

- [x] Create `build.gradle` with all dependencies
- [x] Configure Spring Boot plugin
- [x] Set up Gradle wrapper
- [x] Create custom tasks for testing
- [x] Update test scripts
- [x] Verify all functionality works
- [x] Document migration changes
- [x] Test on different platforms

## Next Steps

1. **Remove Maven Files**: Delete `pom.xml`, `mvnw`, `mvnw.cmd`
2. **Update CI/CD**: Modify build pipelines to use Gradle
3. **Team Training**: Ensure team members understand Gradle commands
4. **Documentation**: Update project documentation to reflect Gradle usage

## Support

If you encounter issues with the Gradle migration:

1. Check the Gradle documentation: https://gradle.org/docs/
2. Review the build.gradle file for configuration
3. Use `./gradlew --help` for command options
4. Check application logs for runtime issues

The migration is complete and all functionality has been preserved while gaining the benefits of Gradle's build system. 
