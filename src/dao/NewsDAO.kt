package my.ktor.dao
import generated.tables.News.NEWS
import generated.tables.records.NewsRecord
import model.News
import javax.sql.DataSource


class NewsDAO(ds: DataSource): IDAO(ds) {
    fun create(news: News): Int {
        val nr: NewsRecord = db.newRecord(NEWS, news)
        return nr.store()
    }

    fun read(id: Long): News {
        val res = db.select().from(NEWS).where(NEWS.ID.eq(id)).fetchOne().into(News::class.java)
        return res
    }

    fun readAll(): List<News> {
        val res = db.select().from(NEWS).fetch().into(News::class.java)
        return res
    }

    fun update(news: News): Int {
        val nr: NewsRecord = db.newRecord(NEWS, news)
        return nr.update()
    }

    fun delete(id: Long): Int {
        val res = db.delete(NEWS).where(NEWS.ID.eq(id)).execute()
        return res
    }
}