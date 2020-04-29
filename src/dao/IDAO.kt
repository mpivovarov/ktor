package my.ktor.dao
import org.jooq.*
import org.jooq.impl.DSL
import javax.sql.DataSource

abstract class IDAO(ds: DataSource) {
    protected val db: DSLContext = DSL.using(ds, SQLDialect.POSTGRES)
}