package com.redrob.ui

import emotion.css.keyframes

object Theme {
    const val bg = "var(--bg-color)"
    const val card = "var(--surface-color)"
    const val sidebar = "var(--sidebar-bg)"
    
    const val pageText = "var(--text-primary)"
    const val dim = "var(--text-secondary)"
    const val faint = "var(--text-faint)"
    
    const val border = "var(--border-color)"
    const val borderSoft = "var(--border-soft)"
    
    const val primary = "var(--primary)"
    const val primaryHover = "var(--primary-hover)"
    
    const val green = "var(--success)"
    const val greenBg = "var(--success-bg)"
    
    const val yellow = "var(--warning)"
    const val yellowBg = "var(--warning-bg)"
    
    const val red = "var(--danger)"
    const val redBg = "var(--danger-bg)"
    
    const val blue = "var(--info)"
    const val blueBg = "var(--info-bg)"
    
    const val purple = "var(--purple)"

    // Fonts
    const val headingFont = "'Space Grotesk', sans-serif"
    const val bodyFont = "'Inter', sans-serif"
    const val mono = "'JetBrains Mono', monospace"

    // Helper for fit colors
    fun fitColor(score: Double): String {
        return when {
            score >= 0.8 -> green
            score >= 0.6 -> yellow
            else -> red
        }
    }

    // Animations (emotion css keyframes)
    val fadeUp = keyframes {
        from {
            asDynamic().opacity = 0
            asDynamic().transform = "translateY(10px)"
        }
        to {
            asDynamic().opacity = 1
            asDynamic().transform = "translateY(0)"
        }
    }

    val slideInRight = keyframes {
        from {
            asDynamic().transform = "translateX(100%)"
        }
        to {
            asDynamic().transform = "translateX(0)"
        }
    }

    val pulseGlow = keyframes {
        from {
            asDynamic().boxShadow = "0 0 0 0 rgba(79, 70, 229, 0.4)"
        }
        to {
            asDynamic().boxShadow = "0 0 0 10px rgba(79, 70, 229, 0)"
        }
    }
}
