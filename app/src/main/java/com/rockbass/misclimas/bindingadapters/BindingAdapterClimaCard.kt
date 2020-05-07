package com.rockbass.misclimas.bindingadapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.rockbass.misclimas.R

@BindingAdapter("weather")
fun bindSrcImageViewCard(imageView: ImageView, weather: String){
    val idImageResource: Int = when(weather){
        "clear" -> R.drawable.ic_clear_day
        "pcloudy", "mcloudy", "cloudy" -> R.drawable.ic_cloudy
        "humid" -> R.drawable.ic_humid_day
        "lightrain", "oshower","ishower",
        "rain" -> R.drawable.ic_rain
        "lightsnow", "rainsnow",
        "snow" -> R.drawable.ic_snow
        "ts", "tsrain" -> R.drawable.ic_ts
        else -> R.drawable.mapbox_ic_offline
    }

    imageView.setImageResource(idImageResource)
}

/*
val idImageResource: Int = when(weather){
        "clearday" -> R.drawable.ic_clear_day
        "clearnight" -> R.drawable.ic_clear_night
        "pcloudyday", "mcloudyday", "cloudyday",
        "pcloudynight", "mcloudynight", "cloudynight"
            -> R.drawable.ic_cloudy
        "humidday" -> R.drawable.ic_humid_day
        "humidnight" -> R.drawable.ic_humid_night
        "lightrainday", "lightrainnight", "oshowerday",
        "oshowernight", "ishowernight", "ishowerday",
        "rainday", "rainnght" -> R.drawable.ic_rain
        "lightsnowday", "lightsnownight",
        "rainsnowday", "rainsnownight",
        "snowday", "snownight" -> R.drawable.ic_snow
        else -> R.drawable.mapbox_ic_offline
    }
 */