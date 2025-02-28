package test;

import org.jmock.Mockery;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class JMockFieldExtension implements AfterEachCallback {

    // 각 테스트 종료 후, 모든 Mockery 멤버 필드에 대해 assertIsSatisfied()를 호출합니다.
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Object testInstance = context.getRequiredTestInstance();
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if (field.getType() == Mockery.class) {
                field.setAccessible(true);
                Object value = field.get(testInstance);
                if (value instanceof Mockery) {
                    ((Mockery) value).assertIsSatisfied();
                }
            }
        }
    }
}
