// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Dentro de buildscript si está presente:

plugins {
    alias(libs.plugins.android.application) apply false
    id ("com.android.library") version "8.2.0" apply false
    id ("com.google.gms.google-services") version "4.4.0" apply false  // Google Services plugin
}