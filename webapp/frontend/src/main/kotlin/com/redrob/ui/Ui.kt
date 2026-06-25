package com.redrob.ui

import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span

/** A small rounded tag. */
external interface ChipProps : Props {
    var text: String
    var strong: Boolean
}

val Chip = FC<ChipProps> { props ->
    span {
        css {
            val s = asDynamic()
            s.display = "inline-block"
            s.fontSize = "12px"
            s.lineHeight = "1.4"
            s.padding = "3px 9px"
            s.borderRadius = "999px"
            s.whiteSpace = "nowrap"
            if (props.strong) {
                s.color = Theme.bg
                s.background = Theme.blue
                s.fontWeight = "700"
                s.border = "none"
            } else {
                s.color = Theme.pageText
                s.background = Theme.borderSoft
                s.fontWeight = "600"
                s.border = "1px solid ${Theme.border}"
            }
        }
        +props.text
    }
}

/** A labelled 0..1 progress bar, colored along a fit scale. */
external interface BarProps : Props {
    var label: String
    var value: Double
    var detail: String?
}

val FitBar = FC<BarProps> { props ->
    val pct = (props.value.coerceIn(0.0, 1.0) * 100).toInt()
    div {
        css {
            val s = asDynamic()
            s.marginBottom = "12px"
        }
        div {
            css {
                val s = asDynamic()
                s.display = "flex"
                s.justifyContent = "space-between"
                s.alignItems = "baseline"
                s.marginBottom = "5px"
            }
            span {
                css {
                    val s = asDynamic()
                    s.fontSize = "13px"
                    s.fontWeight = "600"
                    s.color = Theme.pageText
                }
                +props.label
            }
            span {
                css {
                    val s = asDynamic()
                    s.fontSize = "12px"
                    s.color = Theme.dim
                    s.fontFamily = Theme.mono
                }
                +"$pct%"
            }
        }
        div {
            css {
                val s = asDynamic()
                s.height = "10px"
                s.width = "100%"
                s.background = Theme.borderSoft
                s.borderRadius = "999px"
                s.overflow = "hidden"
            }
            div {
                css {
                    val s = asDynamic()
                    s.height = "100%"
                    s.width = "$pct%"
                    s.borderRadius = "999px"
                    s.background = Theme.fitColor(props.value)
                    s.transition = "width 0.6s cubic-bezier(0.22, 1, 0.36, 1)"
                }
            }
        }
        props.detail?.let { d ->
            div {
                css {
                    val s = asDynamic()
                    s.fontSize = "12px"
                    s.color = Theme.faint
                    s.marginTop = "4px"
                }
                +d
            }
        }
    }
}

/** Section heading inside a card. */
external interface LabelProps : Props {
    var text: String
}

val SectionLabel = FC<LabelProps> { props ->
    div {
        css {
            val s = asDynamic()
            s.fontSize = "12px"
            s.fontWeight = "800"
            s.letterSpacing = "1px"
            s.textTransform = "uppercase"
            s.color = Theme.dim
            s.marginBottom = "12px"
            s.marginTop = "24px"
        }
        +props.text
    }
}
