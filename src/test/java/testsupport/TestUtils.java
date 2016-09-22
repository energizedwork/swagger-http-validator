package testsupport;

import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.HeaderParameter;
import com.energizedwork.swagger.parameters.ParameterFormat;
import com.energizedwork.swagger.validators.ParameterValidator;

public class TestUtils {

    public static AbstractSerializableParameter<? extends AbstractSerializableParameter<?>> createParameterDef(String type, String format) {
        HeaderParameter param = new HeaderParameter();
        param.setType(type);
        param.setFormat(format);
        param.setName("myParam");
        return param;
    }

    public static ParameterValidator validator(AbstractSerializableParameter<? extends AbstractSerializableParameter<?>> parameter) {
        return ParameterFormat.fromParameterDef(parameter).getValidator();
    }
}
