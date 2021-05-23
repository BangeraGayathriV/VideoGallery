package com.gayathri.videogallery

import com.gayathri.videogallery.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    viewModel {
        MainViewModel(get())
    }
}