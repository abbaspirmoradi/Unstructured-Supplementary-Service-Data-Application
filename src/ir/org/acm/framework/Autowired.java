package ir.org.acm.framework;

/**
 * this annotation used for injected objects
 */
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    String name();
}