package com.rhseung.glance.util

data class Vec2D(var x: Int, var y: Int) {
    operator fun plus(other: Vec2D): Vec2D {
        return Vec2D(x + other.x, y + other.y);
    }

    operator fun minus(other: Vec2D): Vec2D {
        return Vec2D(x - other.x, y - other.y);
    }

    operator fun times(other: Int): Vec2D {
        return Vec2D(x * other, y * other);
    }

    operator fun div(other: Int): Vec2D {
        return Vec2D(x / other, y / other);
    }

    operator fun rem(other: Int): Vec2D {
        return Vec2D(x % other, y % other);
    }

    operator fun unaryMinus(): Vec2D {
        return Vec2D(-x, -y);
    }

    operator fun get(index: Int): Int {
        return if (index == 0) x else y;
    }

    operator fun set(index: Int, value: Int): Vec2D {
        return if (index == 0)
            Vec2D(value, y);
        else
            Vec2D(x, value);
    }
}