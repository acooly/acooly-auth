
package cn.acooly.auth.test;
import org.junit.Test;


/**
 * @author qiubo
 */
public class DemoTest extends TestBase {
    @Test
    public void testController() throws Exception {
        assertGetBodyIs("hello", "hello world");
    }

    @Test
    public void testStaticResouce() throws Exception {
        assertGetBodyIs("demo.html", "demo");
    }

    @Test
    public void testNotFound() throws Exception {
        assertGetBodyContains("xxxx", "status=404");
    }
}
