// java
package com.example.dears;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.dears.data.model.Pet;

public class PetUIHelper {

    public static void updateHappinessBar(View fillBar, Pet pet, Context context, int meterMax) {
        if (fillBar == null) {
            Log.w("PetUIHelper", "updateHappinessBar: fillBar is null");
            return;
        }
        if (pet == null || meterMax <= 0) {
            Log.w("PetUIHelper", "updateHappinessBar: invalid pet or meterMax");
            return;
        }

        // Run after layout so measurements are available
        fillBar.post(() -> {
            try {
                // Prefer the parent (container) width as the full meter width
                View parent = (fillBar.getParent() instanceof View) ? (View) fillBar.getParent() : null;
                int totalWidth = (parent != null) ? parent.getWidth() : 0;

                // Fallbacks if parent width not available yet
                if (totalWidth <= 0) {
                    totalWidth = fillBar.getWidth();
                }
                if (totalWidth <= 0) {
                    ViewGroup.LayoutParams lpParent = (parent != null) ? parent.getLayoutParams() : null;
                    if (lpParent != null && lpParent.width > 0) {
                        totalWidth = lpParent.width;
                    } else {
                        ViewGroup.LayoutParams lpFill = fillBar.getLayoutParams();
                        totalWidth = (lpFill != null && lpFill.width > 0) ? lpFill.width : 0;
                    }
                }

                if (totalWidth <= 0) {
                    // can't determine a meaningful full width; abort
                    Log.w("PetUIHelper", "updateHappinessBar: cannot determine totalWidth");
                    return;
                }

                float fraction = Math.max(0f, Math.min(1f, (float) pet.getHappinessMeter() / (float) meterMax));
                int newWidth = (int) (totalWidth * fraction);

                ViewGroup.LayoutParams params = fillBar.getLayoutParams();
                if (params != null) {
                    params.width = newWidth;
                    fillBar.setLayoutParams(params);
                } else {
                    Log.w("PetUIHelper", "updateHappinessBar: fillBar has null LayoutParams");
                }
            } catch (Exception e) {
                Log.w("PetUIHelper", "updateHappinessBar failed: " + e.getMessage());
            }
        });
    }
}
