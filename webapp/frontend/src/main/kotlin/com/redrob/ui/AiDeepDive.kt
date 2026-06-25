package com.redrob.ui

import emotion.react.css
import org.w3c.dom.EventSource
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.useEffect
import react.useEffectOnce
import react.useRef
import react.useState
import web.html.HTMLDivElement

external interface AiDeepDiveProps : Props {
    var candidateId: String
}

val AiDeepDive = FC<AiDeepDiveProps> { props ->
    val (text, setText) = useState("")
    val (status, setStatus) = useState("connecting")
    val logRef = useRef<HTMLDivElement>(null)

    useEffectOnce {
        val es = EventSource("/api/candidates/${props.candidateId}/deepdive")
        
        es.addEventListener("token", { e ->
            val data = e.asDynamic().data as? String ?: ""
            setText { it + data }
        })
        
        es.addEventListener("done", { _ ->
            setStatus("done")
            es.close()
        })
        
        es.addEventListener("info", { e ->
            val data = e.asDynamic().data as? String ?: ""
            setText { it + "\n" + data }
            setStatus("done")
            es.close()
        })
        
        es.addEventListener("error", { e ->
            val data = e.asDynamic().data as? String ?: "stream error"
            setText { it + "\n✗ " + data }
            setStatus("error")
            es.close()
        })


    }

    // Auto-scroll to the bottom as text arrives
    useEffect(text.length) {
        val el = logRef.current
        if (el != null) {
            el.scrollTop = el.scrollHeight.toDouble()
        }
    }

    div {
        css {
            val s = asDynamic()
            s.marginTop = "14px"
            s.background = "#181818" // neutral dark assessment card
            s.border = "1px solid ${Theme.borderSoft}"
            s.borderRadius = "12px"
            s.padding = "20px"
            s.boxShadow = "inset 0 2px 10px rgba(0,0,0,0.4)"
        }

        div {
            css {
                val s = asDynamic()
                s.display = "flex"
                s.alignItems = "center"
                s.gap = "8px"
                s.marginBottom = "14px"
                s.borderBottom = "1px solid rgba(148,163,184,0.18)"
                s.paddingBottom = "10px"
            }
            span {
                css {
                    val s = asDynamic()
                    s.width = "9px"
                    s.height = "9px"
                    s.borderRadius = "50%"
                    s.display = "inline-block"
                    s.background = when (status) {
                        "done" -> Theme.green
                        "error" -> Theme.red
                        else -> Theme.yellow
                    }
                }
            }
            span {
                css {
                    val s = asDynamic()
                    s.fontWeight = "700"
                    s.letterSpacing = "0.6px"
                    s.textTransform = "uppercase"
                    s.color = Theme.primary
                    s.fontSize = "12px"
                    s.fontFamily = Theme.bodyFont
                }
                +"AI Assessment"
            }
        }

        div {
            ref = logRef
            css {
                val s = asDynamic()
                s.maxHeight = "440px"
                s.overflowY = "auto"
                s.wordBreak = "break-word"
            }
            // Single, always-rendered content node. Using dangerouslySetInnerHTML on a node
            // that never swaps element shape guarantees the markdown re-renders live on every
            // token (the previous version swapped a text node for an html node and only
            // applied formatting on a fresh mount).
            div {
                asDynamic().className = "md-content"
                val html = if (text.isEmpty()) {
                    "<span style=\"color:#34D399\">Analyzing candidate against the job description…</span>"
                } else {
                    renderMarkdown(text)
                }
                val obj = js("{}")
                obj.__html = html
                asDynamic().dangerouslySetInnerHTML = obj
            }
            if (status != "done" && status != "error") {
                span {
                    css {
                        val s = asDynamic()
                        s.display = "inline-block"
                        s.width = "8px"
                        s.height = "15px"
                        s.background = Theme.green
                        s.marginLeft = "2px"
                        s.verticalAlign = "middle"
                        s.animation = "blink 1s step-end infinite"
                    }
                }
            }
        }
    }
}

/**
 * Minimal but robust Markdown → HTML converter for the streamed assessment.
 * Handles headings (#/##/###), bold, inline code, blank-line paragraphs, and nested
 * unordered/ordered lists. Styling is supplied by the `.md-content` rules in index.html.
 */
private fun renderMarkdown(src: String): String {
    fun esc(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    fun inline(s: String): String {
        var t = esc(s)
        t = t.replace(Regex("\\*\\*(.+?)\\*\\*"), "<strong>$1</strong>")
        t = t.replace(Regex("`([^`]+?)`"), "<code>$1</code>")
        return t
    }

    val lines = src.replace("\r", "").split("\n")
    val out = StringBuilder()
    val listStack = ArrayList<Pair<Int, String>>() // (indent, tag)
    val para = StringBuilder()

    fun flushPara() {
        if (para.isNotEmpty()) {
            out.append("<p>").append(inline(para.toString().trim())).append("</p>")
            para.setLength(0)
        }
    }
    fun closeListsTo(indent: Int) {
        while (listStack.isNotEmpty() && listStack.last().first > indent) {
            out.append("</li></").append(listStack.removeAt(listStack.size - 1).second).append(">")
        }
    }
    fun closeAllLists() {
        while (listStack.isNotEmpty()) {
            out.append("</li></").append(listStack.removeAt(listStack.size - 1).second).append(">")
        }
    }

    for (raw in lines) {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) {
            flushPara(); closeAllLists(); continue
        }

        val heading = Regex("^(#{1,3})\\s+(.*)$").find(trimmed)
        if (heading != null) {
            flushPara(); closeAllLists()
            val level = heading.groupValues[1].length
            out.append("<h$level>").append(inline(heading.groupValues[2])).append("</h$level>")
            continue
        }

        val item = Regex("^(\\s*)([-*]|\\d+\\.)\\s+(.*)$").find(raw)
        if (item != null) {
            flushPara()
            val indent = item.groupValues[1].length
            val tag = if (item.groupValues[2].endsWith(".")) "ol" else "ul"
            val content = item.groupValues[3]
            if (listStack.isEmpty() || indent > listStack.last().first) {
                out.append("<$tag><li>")
                listStack.add(indent to tag)
            } else {
                closeListsTo(indent)
                if (listStack.isEmpty()) {
                    out.append("<$tag><li>")
                    listStack.add(indent to tag)
                } else {
                    out.append("</li><li>")
                }
            }
            out.append(inline(content))
            continue
        }

        // Plain text: continuation of an open list item, else a paragraph line.
        if (listStack.isNotEmpty()) {
            out.append(' ').append(inline(trimmed))
        } else {
            if (para.isNotEmpty()) para.append(' ')
            para.append(trimmed)
        }
    }
    flushPara(); closeAllLists()
    return out.toString()
}

