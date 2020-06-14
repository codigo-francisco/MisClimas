package com.rockbass.misclimas

import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import com.rockbass.misclimas.views.PlaceFragment

@RunWith(AndroidJUnit4::class)
class TestPlace{

    @Test
    fun probarInsercionCiudadPlace(){
        val placeFragment = PlaceFragment()
        val scenario = launchFragmentInContainer<PlaceFragment>()

    }

}