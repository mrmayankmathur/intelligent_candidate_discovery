package com.redrob.ui

import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.dom.svg.ReactSVG.svg
import react.dom.svg.ReactSVG.circle

external interface ResultsPaneProps : Props {
    var jd: JobDescription?
    var results: List<RankedCandidate>
    var loading: Boolean
    var searched: Boolean
    var onSearch: () -> Unit
    var onOpen: (String) -> Unit
}

val ResultsPane = FC<ResultsPaneProps> { props ->


    div {
        css {
            val s = asDynamic()
            s.display = "flex"
            s.justifyContent = "space-between"
            s.alignItems = "center"
            s.marginBottom = "16px"
        }
        div {
            css {
                val s = asDynamic()
                s.fontSize = "14px"
                s.color = Theme.dim
            }
            if (props.searched && !props.loading) {
                +"${props.results.size} Candidates Found"
            }
        }
        if (props.searched && props.results.isNotEmpty()) {
            div {
                css {
                    val s = asDynamic()
                    s.fontSize = "13px"
                    s.color = Theme.dim
                }
                +"Ranked by match score"
            }
        }
    }

    when {
        props.loading -> Notice { text = "Loading ranked candidates…" }
        !props.searched -> Notice { text = "Press 'Run AI Match' on the left to reveal the engine's ranked shortlist for this role." }
        props.results.isEmpty() -> Notice { text = "No results available." }
        else -> {
            div {
                css {
                    val s = asDynamic()
                    s.display = "flex"
                    s.flexDirection = "column"
                    s.gap = "16px"
                }
                props.results.forEach { item ->
                    CandidateCard {
                        key = item.ranking.candidateId.unsafeCast<react.Key>()
                        this.item = item
                        this.onOpen = props.onOpen
                    }
                }

            }
        }
    }
}

private external interface NoticeProps : Props {
    var text: String
}

private val Notice = FC<NoticeProps> { props ->
    div {
        css {
            val s = asDynamic()
            s.marginTop = "20px"
            s.padding = "40px 24px"
            s.textAlign = "center"
            s.color = Theme.dim
            s.fontSize = "14px"
            s.background = Theme.card
            s.border = "1px dashed ${Theme.borderSoft}"
            s.borderRadius = "14px"
        }
        +props.text
    }
}

external interface CandidateCardProps : Props {
    var item: RankedCandidate
    var onOpen: (String) -> Unit
}

val CandidateCard = FC<CandidateCardProps> { props ->
    val r = props.item.ranking
    val p = props.item.summary
    val pct = (r.score.coerceIn(0.0, 1.0) * 100).toInt()
    val name = p?.name ?: r.candidateId
    val initials = name.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString("")

    div {
        css {
            val s = asDynamic()
            s.display = "flex"
            s.justifyContent = "space-between"
            s.alignItems = "center"
            s.background = Theme.card
            s.border = "1px solid ${if (r.rank == 1) Theme.purple else Theme.borderSoft}"
            s.borderRadius = "12px"
            s.padding = "20px 24px"
            s.cursor = "pointer"
            s.position = "relative"
            s.overflow = "hidden"
            s.animation = "${Theme.fadeUp} 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94)"
            
            val hoverObj = js("{}")
            hoverObj.transform = "translateY(-4px)"
            hoverObj.boxShadow = "0 12px 30px rgba(0,0,0,0.08)"
            hoverObj.borderColor = Theme.primary
            s["&:hover"] = hoverObj
        }
        onClick = { props.onOpen(r.candidateId) }

        // Left section: Avatar & Info
        div {
            css {
                val s = asDynamic()
                s.display = "flex"
                s.alignItems = "center"
                s.gap = "20px"
                s.flex = "1"
            }
            
            // Avatar with Rank Badge
            div {
                css {
                    val s = asDynamic()
                    s.position = "relative"
                }
                div {
                    css {
                        val s = asDynamic()
                        s.width = "72px"
                        s.height = "72px"
                        s.borderRadius = "50%"
                        s.background = Theme.borderSoft
                        s.display = "flex"
                        s.alignItems = "center"
                        s.justifyContent = "center"
                        s.color = Theme.dim
                        s.fontWeight = "600"
                    }
                    +initials
                }
                div {
                    css {
                        val s = asDynamic()
                        s.position = "absolute"
                        s.top = "-4px"
                        s.left = "-4px"
                        s.background = if (r.rank == 1) Theme.yellow else if (r.rank == 2) Theme.dim else Theme.primary
                        s.color = "white"
                        s.width = "24px"
                        s.height = "24px"
                        s.borderRadius = "50%"
                        s.display = "flex"
                        s.alignItems = "center"
                        s.justifyContent = "center"
                        s.fontSize = "12px"
                        s.fontWeight = "800"
                        s.border = "2px solid ${Theme.card}"
                    }
                    +"${r.rank}"
                }
            }

            // Info
            div {
                css {
                    val s = asDynamic()
                    s.display = "flex"
                    s.flexDirection = "column"
                    s.gap = "6px"
                }
                div {
                    css {
                        val s = asDynamic()
                        s.fontSize = "18px"
                        s.fontWeight = "700"
                        s.color = Theme.pageText
                    }
                    +(p?.name ?: r.candidateId)
                }
                div {
                    css {
                        val s = asDynamic()
                        s.fontSize = "14px"
                        s.color = Theme.dim
                    }
                    +(p?.currentTitle ?: "Software Engineer")
                }
                div {
                    css {
                        val s = asDynamic()
                        s.display = "flex"
                        s.alignItems = "center"
                        s.gap = "12px"
                        s.fontSize = "12px"
                        s.color = Theme.dim
                        s.marginTop = "2px"
                    }
                    p?.yearsOfExperience?.let { span { +"${it} yrs" } }
                    p?.location?.let {
                        if (it.isNotBlank()) {
                            span {
                                css { asDynamic().color = Theme.faint }
                                +"·"
                            }
                            span { +it }
                        }
                    }
                }
                div {
                    css {
                        val s = asDynamic()
                        s.display = "flex"
                        s.gap = "8px"
                        s.marginTop = "4px"
                    }
                    if (p?.openToWork == true) {
                        Badge { text = "Open to work"; color = Theme.green }
                    }
                    p?.noticePeriodDays?.let {
                        Badge { text = "Notice ${it}d"; color = Theme.yellow }
                    }
                    p?.recruiterResponseRate?.let {
                        if (it > 0) {
                            Badge { text = "${(it * 100).toInt()}% reply rate"; color = Theme.blue }
                        }
                    }
                }
            }
        }

        // Middle Right: Circle Match
        div {
            css {
                val s = asDynamic()
                s.marginRight = "32px"
                s.position = "relative"
                s.width = "72px"
                s.height = "72px"
                s.display = "flex"
                s.alignItems = "center"
                s.justifyContent = "center"
            }
            CircularProgress { value = pct }
            div {
                css {
                    val s = asDynamic()
                    s.position = "absolute"
                    s.display = "flex"
                    s.flexDirection = "column"
                    s.alignItems = "center"
                }
                span {
                    css {
                        val s = asDynamic()
                        s.fontSize = "20px"
                        s.fontWeight = "800"
                        s.color = Theme.pageText
                        s.lineHeight = "1"
                    }
                    +"$pct%"
                }
                span {
                    css {
                        val s = asDynamic()
                        s.fontSize = "10px"
                        s.color = Theme.dim
                        s.textTransform = "uppercase"
                        s.letterSpacing = "1px"
                        s.marginTop = "2px"
                    }
                    +"Match"
                }
            }
        }

        // Far Right: Top Skills
        div {
            css {
                val s = asDynamic()
                s.flex = "0 0 200px"
                s.display = "flex"
                s.flexWrap = "wrap"
                s.gap = "8px"
                s.alignContent = "center"
            }
            p?.topSkills?.take(3)?.forEach { skill ->
                Chip { text = skill; strong = false }
            }
        }
    }
}

external interface CircularProgressProps : Props {
    var value: Int
}

val CircularProgress = FC<CircularProgressProps> { props ->
    val radius = 32
    val circumference = 2 * kotlin.math.PI * radius
    val offset = circumference - (props.value / 100.0) * circumference
    val color = Theme.fitColor(props.value / 100.0)

    svg {
        asDynamic().width = "72"
        asDynamic().height = "72"
        asDynamic().viewBox = "0 0 72 72"
        asDynamic().style = js("{ transform: 'rotate(-90deg)' }")
        circle {
            asDynamic().cx = "36"
            asDynamic().cy = "36"
            asDynamic().r = radius.toString()
            asDynamic().fill = "transparent"
            asDynamic().stroke = Theme.borderSoft
            asDynamic().strokeWidth = "6"
        }
        circle {
            asDynamic().cx = "36"
            asDynamic().cy = "36"
            asDynamic().r = radius.toString()
            asDynamic().fill = "transparent"
            asDynamic().stroke = color
            asDynamic().strokeWidth = "6"
            asDynamic().strokeDasharray = circumference.toString()
            asDynamic().strokeDashoffset = offset.toString()
            asDynamic().strokeLinecap = "round"
            asDynamic().style = js("{ transition: 'stroke-dashoffset 1s ease-in-out' }")
        }
    }
}

external interface BadgeProps : Props {
    var text: String
    var color: String
}

val Badge = FC<BadgeProps> { props ->
    div {
        css {
            val s = asDynamic()
            s.display = "flex"
            s.alignItems = "center"
            s.gap = "4px"
            s.fontSize = "11px"
            s.color = props.color
            s.background = "${props.color}15" // 15 is approx 8% opacity in hex
            s.padding = "4px 8px"
            s.borderRadius = "6px"
        }
        span { +props.text }
    }
}
