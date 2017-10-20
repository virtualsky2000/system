package system;

import java.sql.Connection;

import system.db.jdbc.AbstractDao;

public class MMsgDao extends AbstractDao<MMsg> {

    public MMsgDao(Connection conn) {
        super(conn);
    }

//    protected MMsg mapRow(ResultSet rs, Class<MMsg> clazz) throws SQLException {
//        MMsg msg = new MMsg();
//
//        msg.setMsgCd(rs.getString("MSGCD"));
//        msg.setMsg(rs.getString("MSG"));
//
//        return msg;
//    }

}
