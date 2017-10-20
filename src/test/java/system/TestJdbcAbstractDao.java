package system;

import system.logging.LogManager;
import system.logging.Logger;
import system.utils.ClassUtils;

public class TestJdbcAbstractDao {

    private static Logger log = LogManager.getLogger(TestJdbcAbstractDao.class);

    public static void main(String[] args) throws ClassNotFoundException {

        String path = ClassUtils.getClassFile("org.apache.log4j.Logger").getAbsolutePath();
        log.info(path);
        log.debug(String.class.getResource("String.class"));


//        try {
//            Context context = new InitialContext();
//            DataSource dataSource = (DataSource) context.lookup("java:comp/env/jdbc/datasource");
//
////            Connection conn = DriverManager.getConnection("jdbc:hitachi:hirdb://DBID=22200,DBHOST=10.13.50.102",
////                    "DWKAI02", "DWKAI02");
//
//            try (Connection conn = dataSource.getConnection()) {
//                MMsgDao dao = new MMsgDao(conn);
//                List<MMsg> lstData = dao.query("select * from MMsg_T without lock nowait", MMsg.class);
//
//                log.info(lstData.size());
//            }
//        } catch (SQLException | NamingException e) {
//            e.printStackTrace();
//        }

        log.info("end");
    }

}
