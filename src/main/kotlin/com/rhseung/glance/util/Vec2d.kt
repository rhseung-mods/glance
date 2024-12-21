package com.rhseung.glance.util

data class Vec2d(var x: Int, var y: Int) {
    operator fun plus(other: Vec2d): Vec2d {
        return Vec2d(x + other.x, y + other.y);
    }

    operator fun minus(other: Vec2d): Vec2d {
        return Vec2d(x - other.x, y - other.y);
    }

    operator fun times(other: Int): Vec2d {
        return Vec2d(x * other, y * other);
    }

    operator fun div(other: Int): Vec2d {
        return Vec2d(x / other, y / other);
    }

    operator fun rem(other: Int): Vec2d {
        return Vec2d(x % other, y % other);
    }

    operator fun unaryMinus(): Vec2d {
        return Vec2d(-x, -y);
    }

    operator fun get(index: Int): Int {
        return if (index == 0) x else y;
    }

    operator fun set(index: Int, value: Int): Vec2d {
        return if (index == 0)
            Vec2d(value, y);
        else
            Vec2d(x, value);
    }
}