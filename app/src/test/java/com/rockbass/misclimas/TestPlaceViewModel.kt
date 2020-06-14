package com.rockbass.misclimas

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import androidx.test.core.app.ApplicationProvider
import com.rockbass.misclimas.viewmodels.PlaceViewModel
import org.junit.Test

@RunWith(JUnit4::class)
class TestPlaceViewModel {
    lateinit var app : Application
    lateinit var placeViewModel: PlaceViewModel

    @Before fun setUp(){
        app = ApplicationProvider.getApplicationContext<Application>()
        //placeViewModel = ViewModelProvider(app).get(PlaceViewModel::class.java)
    }

    @Test fun testInsertCiudad(){

    }
}