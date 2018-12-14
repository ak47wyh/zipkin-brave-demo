import com.alibaba.dubbo.DemoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Created by Administrator on 2017/8/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class TestConsumer {

    Logger logger = LoggerFactory.getLogger(TestConsumer.class);

    @Autowired
    private DemoService demoService;

    @Test
    public void test1(){
        logger.info("consumer test start");
        demoService.sayHello("hello consumer");
        logger.info("consumer test end");
    }
}
