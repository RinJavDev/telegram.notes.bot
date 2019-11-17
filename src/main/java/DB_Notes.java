import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_Notes {
    String userName="rinat";
    String password="6390";
    String connectionURL="jdbc:mysql://localhost:3306/notes";
    Statement statement;
    void connect(){

        try {
            Connection connection= DriverManager.getConnection(connectionURL,userName,password);
            statement = connection.createStatement();

            System.out.println("we're connected");
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Statement getStatement() {
        return statement;
    }
}
