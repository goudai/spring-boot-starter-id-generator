## dependency

* zookeeper 3.4.10

# Usage

### Install or deploy to private Repository

* mvn clean install or mvn clean deploy

## id generator

* add dependency to maven
```xml
<dependency>
    <groupId>io.github.goudai</groupId>
    <artifactId>spring-boot-starter-id-generator-zookeeper</artifactId>
    <version>1.0.2</version>
</dependency>

```

 * using on spring boo 
 
```yaml
# application.yml
goudai:
  id:
    generator:
      zookeeper:
        zookeeper-servers: ${ZOO_SERVERS:localhost:2181}
``` 
```java
public class XxxController {

    @Autowired
    IdGenerator idGenerator;

    @GetMapping("idString")
    public String idString() {
        return idGenerator.nextIdAsString();
    }

    @GetMapping("idLong")
    public Long idLong() {
        return idGenerator.nextIdAsLong();
    }
}
```
 
