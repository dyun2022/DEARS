// java
// ToastMatcher.java (app/src/androidTest/java/com/example/dears/ToastMatcher.java)
package com.example.dears;

import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import androidx.test.espresso.Root;
import androidx.test.espresso.util.EspressoOptional;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ToastMatcher extends TypeSafeMatcher<Root> {
    @Override
    public void describeTo(Description description) {
        description.appendText("is toast");
    }

    @Override
    protected boolean matchesSafely(Root root) {
        // handle optional params safely
        EspressoOptional<WindowManager.LayoutParams> paramsOpt = root.getWindowLayoutParams();
        View decor = root.getDecorView();
        if (decor == null) return false;

        IBinder windowToken = decor.getWindowToken();
        IBinder appToken = decor.getApplicationWindowToken();

        // prefer the TYPE_TOAST check when available
        if (paramsOpt.isPresent()) {
            WindowManager.LayoutParams params = paramsOpt.get();
            if (params != null && params.type == WindowManager.LayoutParams.TYPE_TOAST) {
                return windowToken == appToken;
            }
        }

        // fallback: some environments don't expose layout params for toast roots;
        // use the standard token equality check which is commonly used for toasts
        if (windowToken != null && appToken != null) {
            return windowToken == appToken;
        }

        return false;
    }
}
