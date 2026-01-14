# Jackson Instant Serialization Troubleshooting

## Current Error
```
com.fasterxml.jackson.databind.exc.InvalidDefinitionException: 
Java 8 date/time type `java.time.Instant` not supported by default
```

## Steps to Fix

### 1. Restart the Application
**IMPORTANT**: After making configuration changes, you MUST restart the Spring Boot application for changes to take effect.

```bash
# Stop the current application (Ctrl+C or kill the process)
# Then restart:
./mvnw spring-boot:run

# OR if using IDE, stop and restart the application
```

### 2. Check Startup Logs
When the application starts, you should see these messages:

```
========================================
✅ JacksonConfig is being loaded!
========================================
✅ Creating Primary ObjectMapper bean with JavaTimeModule
✅ JavaTimeModule registered
✅ WRITE_DATES_AS_TIMESTAMPS disabled
✅ Creating Jackson2ObjectMapperBuilder
========================================
JACKSON CONFIGURATION VERIFICATION
========================================
✅ SUCCESS: JavaTimeModule is properly configured!
✅ Instant serialized as: "2026-01-14T10:30:45.123Z"
✅ Registered modules: [com.fasterxml.jackson.datatype.jsr310.JavaTimeModule]
========================================
```

### 3. If You DON'T See These Messages

**Problem**: JacksonConfig is not being loaded

**Solutions**:

#### Option A: Check Package Structure
Ensure `JacksonConfig.java` is in the correct package:
```
src/main/java/com/helpnearby/config/JacksonConfig.java
```

#### Option B: Verify @Configuration Annotation
The class should have `@Configuration` annotation:
```java
@Configuration
public class JacksonConfig {
    // ...
}
```

#### Option C: Force Component Scan
Add to `HelpNearByApplication.java`:
```java
@SpringBootApplication
@ComponentScan(basePackages = "com.helpnearby")
public class HelpNearByApplication {
    // ...
}
```

### 4. Verify Dependency is in pom.xml

Check that this dependency exists:
```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```

### 5. Clean and Rebuild

```bash
# Clean Maven build
./mvnw clean

# Rebuild
./mvnw compile

# Run
./mvnw spring-boot:run
```

### 6. Test the Fix

Once the application is running with the correct startup messages:

```bash
# Test 1: Verify Jackson configuration
curl http://localhost:8080/api/test/instant

# Expected response:
# {
#   "instant": "2026-01-14T10:30:45.123Z",
#   "localDateTime": "2026-01-14T10:30:45.123",
#   "message": "If you see this, Java 8 time serialization is working!"
# }

# Test 2: Test the actual endpoint
curl http://localhost:8080/api/requests

# Should return data without errors
```

## Common Issues

### Issue 1: Configuration Not Loading
**Symptom**: No startup messages about JacksonConfig
**Solution**: 
- Verify file is in `src/main/java/com/helpnearby/config/`
- Check `@Configuration` annotation is present
- Restart application

### Issue 2: Dependency Not Found
**Symptom**: ClassNotFoundException for JavaTimeModule
**Solution**:
```bash
./mvnw clean install
```

### Issue 3: Multiple ObjectMapper Beans
**Symptom**: Conflicting bean definitions
**Solution**: The `@Primary` annotation on the ObjectMapper bean should resolve this

### Issue 4: Still Getting Instant Error After Restart
**Symptom**: Error persists even after configuration
**Possible Causes**:
1. Application not fully restarted
2. Using cached/old JAR file
3. IDE not rebuilding properly

**Solution**:
```bash
# Full clean rebuild
./mvnw clean package -DskipTests

# Run the newly built JAR
java -jar target/helpnearby-0.0.1-SNAPSHOT.jar
```

## Verification Checklist

- [ ] `jackson-datatype-jsr310` dependency in pom.xml
- [ ] `JacksonConfig.java` exists in `src/main/java/com/helpnearby/config/`
- [ ] `@Configuration` annotation present on JacksonConfig
- [ ] `@Primary` annotation on objectMapper() method
- [ ] Application fully restarted (not just hot-reload)
- [ ] Startup logs show "JacksonConfig is being loaded!"
- [ ] Startup logs show "JavaTimeModule registered"
- [ ] Test endpoint `/api/test/instant` returns JSON with dates
- [ ] Main endpoint `/api/requests` works without errors

## Alternative: Manual Configuration in Application Class

If the configuration still doesn't load, add this directly to your main application class:

```java
@SpringBootApplication
public class HelpNearByApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelpNearByApplication.class, args);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
```

## Debug Mode

Enable debug logging to see what's happening:

Add to `application.properties`:
```properties
logging.level.com.helpnearby.config=DEBUG
logging.level.org.springframework.context=DEBUG
logging.level.com.fasterxml.jackson=DEBUG
```

## Final Notes

- **MUST RESTART**: Configuration changes require a full application restart
- **Check Logs**: Always check startup logs for the verification messages
- **Test First**: Use `/api/test/instant` endpoint before testing main endpoints
- **Clean Build**: If all else fails, do a clean build and restart

## Success Indicators

When everything is working correctly:
1. ✅ Startup logs show JacksonConfig loading
2. ✅ `/api/test/instant` returns properly formatted dates
3. ✅ `/api/requests` returns data without serialization errors
4. ✅ Dates appear as ISO-8601 strings (not timestamps)