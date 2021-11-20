import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.olive.oliveProgram

/**
 *  This is a template for a live program.
 *
 *  It uses oliveProgram {} instead of program {}. All code inside the
 *  oliveProgram {} can be changed while the program is running.
 */

fun main() = application {
    configure {
        width = 800
        height = 800
    }
    oliveProgram {
        extend {
            drawer.clear(ColorRGBa.PINK)
        }
    }
}