package com.redrob.ui

import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span

external interface JdPanelProps : Props {
    var jd: JobDescription
    var onRerun: () -> Unit
}

val JdPanel = FC<JdPanelProps> { props ->
    val jd = props.jd
    div {
        css {
            val s = asDynamic()
            s.display = "flex"
            s.flexDirection = "column"
            s.gap = "16px"
            s.animation = "${Theme.slideInRight} 0.5s ease" // wait, this is left panel, don't slide from right.
        }

        // Job Context Card
        div {
            css {
                val s = asDynamic()
                s.background = Theme.card
                s.border = "1px solid ${Theme.borderSoft}"
                s.borderRadius = "12px"
                s.padding = "20px"
            }
            div {
                css {
                    val s = asDynamic()
                    s.display = "flex"
                    s.alignItems = "center"
                    s.gap = "10px"
                    s.marginBottom = "12px"
                }
                span {
                    css {
                        val s = asDynamic()
                        s.width = "20px"
                        s.height = "20px"
                        s.background = "linear-gradient(45deg, ${Theme.purple}, ${Theme.blue})"
                        s.borderRadius = "4px"
                        s.display = "inline-flex"
                        s.alignItems = "center"
                        s.justifyContent = "center"
                        s.color = "white"
                        s.fontSize = "12px"
                    }
                    +""
                }
                span {
                    css {
                        val s = asDynamic()
                        s.fontSize = "16px"
                        s.fontWeight = "700"
                        s.color = Theme.pageText
                        s.fontFamily = Theme.headingFont
                    }
                    +jd.jobTitle
                }
            }
            div {
                css {
                    val s = asDynamic()
                    s.color = Theme.dim
                    s.fontSize = "13px"
                    s.marginBottom = "12px"
                    s.display = "flex"
                    s.alignItems = "center"
                    s.gap = "6px"
                }
                span { +"🏢" }
                +jd.company
            }
            // Real role tags derived from the JD
            div {
                css {
                    val s = asDynamic()
                    s.display = "flex"
                    s.gap = "8px"
                    s.flexWrap = "wrap"
                }
                val workMode = jd.location.workMode
                    ?.split(Regex("[\\s(]"))?.firstOrNull { it.isNotBlank() }
                    ?.replaceFirstChar { it.uppercase() }
                if (!workMode.isNullOrBlank()) {
                    Chip { text = workMode; strong = true }
                }
                if (jd.experience.idealMax > 0) {
                    Chip { text = "${jd.experience.idealMin}–${jd.experience.idealMax} yrs"; strong = false }
                }
            }
            if (jd.summary.isNotBlank()) {
                div {
                    css {
                        val s = asDynamic()
                        s.marginTop = "12px"
                        s.paddingTop = "12px"
                        s.borderTop = "1px solid ${Theme.borderSoft}"
                        s.fontSize = "12.5px"
                        s.lineHeight = "1.55"
                        s.color = Theme.dim
                        s.display = "-webkit-box"
                        s["-webkit-line-clamp"] = "3"
                        s["-webkit-box-orient"] = "vertical"
                        s.overflow = "hidden"
                    }
                    +jd.summary
                }
            }
        }

        // Constraints Section
        div {
            css {
                val s = asDynamic()
                s.background = Theme.card
                s.border = "1px solid ${Theme.borderSoft}"
                s.borderRadius = "12px"
                s.padding = "16px"
            }
            div {
                css {
                    val s = asDynamic()
                    s.display = "flex"
                    s.justifyContent = "space-between"
                    s.alignItems = "center"
                    s.marginBottom = "16px"
                }
                SectionLabel { text = "CONSTRAINTS"; }
            }
            div {
                css {
                    val s = asDynamic()
                    s.display = "flex"
                    s.flexDirection = "column"
                    s.gap = "12px"
                }
                ConstraintRow { label = "Experience"; value = "${jd.experience.idealMin} - ${jd.experience.idealMax} Years" }
                ConstraintRow { label = "Notice Period"; value = "< ${jd.location.noticePeriodIdealDays} Days" }
                ConstraintRow { label = "Location"; value = jd.location.preferred.joinToString(", ") }
            }
        }

        // Skill Weightage
        div {
            css {
                val s = asDynamic()
                s.background = Theme.card
                s.border = "1px solid ${Theme.borderSoft}"
                s.borderRadius = "12px"
                s.padding = "16px"
            }
            SectionLabel { text = "SKILL WEIGHTAGE" }
            div {
                css {
                    val s = asDynamic()
                    s.display = "flex"
                    s.flexDirection = "column"
                    s.gap = "10px"
                }
                val maxWeight = jd.mustHave.maxOfOrNull { it.weight } ?: 1.0
                jd.mustHave.forEach { group ->
                    SkillWeightRow { skill = group.category; weight = group.weight; this.maxWeight = maxWeight }
                }
            }
        }

        // Red Flags
        if (jd.disqualifiers.isNotEmpty()) {
            div {
                css {
                    val s = asDynamic()
                    s.background = Theme.card
                    s.border = "1px solid ${Theme.borderSoft}"
                    s.borderRadius = "12px"
                    s.padding = "16px"
                }
                div {
                    css {
                        val s = asDynamic()
                        s.fontSize = "12px"
                        s.fontWeight = "800"
                        s.letterSpacing = "1px"
                        s.textTransform = "uppercase"
                        s.color = Theme.red
                        s.marginBottom = "12px"
                    }
                    +"RED FLAGS (DISQUALIFIERS)"
                }
                div {
                    css {
                        val s = asDynamic()
                        s.display = "flex"
                        s.flexDirection = "column"
                        s.gap = "8px"
                    }
                    jd.disqualifiers.forEach { d ->
                        div {
                            css {
                                val s = asDynamic()
                                s.fontSize = "13px"
                                s.color = Theme.dim
                                s.display = "flex"
                                s.alignItems = "center"
                                s.gap = "8px"
                            }
                            span {
                                css {
                                    val s = asDynamic()
                                    s.color = Theme.red
                                }
                                +"⚠"
                            }
                            +d.description
                        }
                    }
                }
            }
        }

        // Summary Recap
        div {
            css {
                val s = asDynamic()
                s.textAlign = "center"
                s.fontSize = "13px"
                s.color = Theme.dim
                s.padding = "8px 0"
            }
            +"${jd.mustHave.size} must-have skill areas · ${jd.disqualifiers.size} disqualifiers"
        }

        // Primary CTA
        button {
            css {
                val s = asDynamic()
                s.width = "100%"
                s.padding = "14px 24px"
                s.background = Theme.primary
                s.border = "none"
                s.color = "#ffffff"
                s.borderRadius = "8px"
                s.fontWeight = "600"
                s.fontSize = "15px"
                s.cursor = "pointer"
                s.display = "flex"
                s.alignItems = "center"
                s.justifyContent = "center"
                s.gap = "8px"
                s.transition = "all 0.2s"
                val hoverObj = js("{}")
                hoverObj.background = Theme.primaryHover
                s["&:hover"] = hoverObj
            }
            onClick = { props.onRerun() }
            span { +"✨" }
            span { +"Run AI Match" }
        }
        div {
            css {
                val s = asDynamic()
                s.textAlign = "center"
                s.fontSize = "12px"
                s.color = Theme.dim
            }
            +"Run to discover top matching candidates"
        }
    }
}

private external interface ConstraintRowProps : Props {
    var label: String
    var value: String
}
private val ConstraintRow = FC<ConstraintRowProps> { props ->
    div {
        css {
            val s = asDynamic()
            s.display = "flex"
            s.justifyContent = "space-between"
            s.alignItems = "center"
            s.fontSize = "13px"
        }
        div {
            css {
                val s = asDynamic()
                s.display = "flex"
                s.alignItems = "center"
                s.gap = "8px"
                s.color = Theme.dim
            }
            span { +props.label }
        }
        div {
            css {
                val s = asDynamic()
                s.color = Theme.blue
            }
            +props.value
        }
    }
}

private external interface SkillWeightRowProps : Props {
    var skill: String
    var weight: Double
    var maxWeight: Double
}
private val SkillWeightRow = FC<SkillWeightRowProps> { props ->
    div {
        css {
            val s = asDynamic()
            s.display = "flex"
            s.justifyContent = "space-between"
            s.alignItems = "center"
            s.fontSize = "13px"
        }
        span {
            css {
                val s = asDynamic()
                s.color = Theme.pageText
                s.flex = "1"
            }
            +props.skill
        }
        span {
            css {
                val s = asDynamic()
                s.display = "flex"
                s.gap = "4px"
                s.flex = "1"
                s.justifyContent = "center"
            }
            val maxW = if (props.maxWeight > 0) props.maxWeight else 1.0
            val starsCount = kotlin.math.round((props.weight / maxW) * 5.0).toInt().coerceIn(1, 5)
            for (i in 1..5) {
                span {
                    css {
                        val s = asDynamic()
                        s.color = if (i <= starsCount) Theme.primary else Theme.borderSoft
                        s.fontSize = "16px"
                    }
                    +"★"
                }
            }
        }
    }
}
