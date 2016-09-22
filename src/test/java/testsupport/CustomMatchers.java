package testsupport;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import com.energizedwork.swagger.ValidationError;

public class CustomMatchers {

    public static Matcher<ValidationError> validationError(final String propertyPath, final String message) {
        return new TypeSafeDiagnosingMatcher<ValidationError>() {
            @Override
            protected boolean matchesSafely(ValidationError item, Description mismatchDescription) {
                if (!item.getErrorMessage().equals(message)) {
                    mismatchDescription.appendText("message was " + item.getErrorMessage());
                    return false;
                }

                if (!item.getFieldPath().equals(propertyPath)) {
                    mismatchDescription.appendText("field path was " + item.getFieldPath());
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a validation error for field " + propertyPath + " with message " + message);
            }
        };
    }
}
