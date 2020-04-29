package model

import java.time.Instant
import javax.persistence.Column

data class News(
    @Column(name = "id")
    var id: Long? = null,
    @Column(name = "date")
    var datetime: Instant = Instant.now(),
    @Column(name = "title")
    var title: String = "",
    @Column(name = "text")
    var textHtml: String = ""
)
