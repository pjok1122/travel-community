package project.board.configuration;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class MySqlDialect extends MySQL8Dialect {

    public MySqlDialect() {
        super();
        this.registerFunction("match", new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "match(?1, ?2) against (?3)"));
    }
}
