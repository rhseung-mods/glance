package com.rhseung.glance.draw

data class Padding(val size: Int) : Drawable {
    companion object {
        val SLOT = Padding(12);
    }
}