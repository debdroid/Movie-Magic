package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import com.moviemagic.dpaul.android.app.R;
import groovy.transform.CompileStatic

@CompileStatic
class ColorPalleteSwatch {
    //TODO: Not used. delete before release
    public static int PALLETE_PRIMARY_COLOR
    public static int PALLETE_PRIMARY_DARK_COLOR
    public static int PALLETE_ACCENT_COLOR
    public static int PALLETE_TITLE_COLOR
    public static int PALLETE_BODY_TEXT_COLOR

    public ColorPalleteSwatch(Bitmap bitmap, Context context) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette p) {
                Palette.Swatch vibrantSwatch = p.getVibrantSwatch()
                Palette.Swatch lightVibrantSwatch = p.getLightVibrantSwatch()
                Palette.Swatch darkVibrantSwatch = p.getDarkVibrantSwatch()
                Palette.Swatch mutedSwatch = p.getMutedSwatch()
                Palette.Swatch mutedLightSwatch = p.getLightMutedSwatch()
                Palette.Swatch mutedDarkSwatch = p.getDarkMutedSwatch()
                final boolean pickSwatchColorFlag = false
                //Pick primary, primaryDark, title and body text color
                if (vibrantSwatch) {
                    PALLETE_PRIMARY_COLOR = vibrantSwatch.getRgb()
                    PALLETE_TITLE_COLOR = vibrantSwatch.getTitleTextColor()
                    PALLETE_BODY_TEXT_COLOR = vibrantSwatch.getBodyTextColor()
                    //Produce Dark color by changing the value (3rd parameter) of HSL value
                    float[] primaryHsl = vibrantSwatch.getHsl()
                    primaryHsl[2] = primaryHsl[2] * 0.9f
                    PALLETE_PRIMARY_DARK_COLOR = Color.HSVToColor(primaryHsl)
                    pickSwatchColorFlag = true
                } else if (lightVibrantSwatch) { //Try another swatch
                    PALLETE_PRIMARY_COLOR = lightVibrantSwatch.getRgb()
                    PALLETE_TITLE_COLOR = lightVibrantSwatch.getTitleTextColor()
                    PALLETE_BODY_TEXT_COLOR = lightVibrantSwatch.getBodyTextColor()
                    //Produce Dark color by changing the value (3rd parameter) of HSL value
                    float[] primaryHsl = lightVibrantSwatch.getHsl()
                    primaryHsl[2] = primaryHsl[2] * 0.9f
                    PALLETE_PRIMARY_DARK_COLOR = Color.HSVToColor(primaryHsl)
                    pickSwatchColorFlag = true
                } else if (darkVibrantSwatch) { //Try last swatch
                    PALLETE_PRIMARY_COLOR = darkVibrantSwatch.getRgb()
                    PALLETE_TITLE_COLOR = darkVibrantSwatch.getTitleTextColor()
                    PALLETE_BODY_TEXT_COLOR = darkVibrantSwatch.getBodyTextColor()
                    //Produce Dark color by changing the value (3rd parameter) of HSL value
                    float[] primaryHsl = darkVibrantSwatch.getHsl()
                    primaryHsl[2] = primaryHsl[2] * 0.9f
                    PALLETE_PRIMARY_DARK_COLOR = Color.HSVToColor(primaryHsl)
                    pickSwatchColorFlag = true
                } else { //Fallback to default
                    PALLETE_PRIMARY_COLOR = ContextCompat.getColor(context, R.color.primary)
                    PALLETE_PRIMARY_DARK_COLOR = ContextCompat.getColor(context, R.color.primary_dark)
                    PALLETE_TITLE_COLOR = ContextCompat.getColor(context, R.color.white_color)
                    PALLETE_BODY_TEXT_COLOR = ContextCompat.getColor(context, R.color.grey_color)
                    //This is needed as we are not going pick accent colour if falling back
                    PALLETE_ACCENT_COLOR = ContextCompat.getColor(context, R.color.accent)
                }
                //Pick accent color only if Swatch color is picked, otherwise do not pick accent color
                if (pickSwatchColorFlag) {
                    if (mutedSwatch) {
                        PALLETE_ACCENT_COLOR = mutedSwatch.getRgb()
                    } else if (mutedLightSwatch) { //Try another swatch
                        PALLETE_ACCENT_COLOR = mutedLightSwatch.getRgb()
                    } else if (mutedDarkSwatch) { //Try last swatch
                        PALLETE_ACCENT_COLOR = mutedDarkSwatch.getRgb()
                    } else { //Fallback to default
                        PALLETE_ACCENT_COLOR = ContextCompat.getColor(context, R.color.accent)
                    }
                }
            }
        })
    }
}