import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;
import java.util.Vector;

import org.json.JSONObject;
import org.json.JSONArray;

public class GetData {

    static String prefix = "project3.";

    // You must use the following variable as the JDBC connection
    Connection oracleConnection = null;

    // You must refer to the following variables for the corresponding 
    // tables in your database
    String userTableName = null;
    String friendsTableName = null;
    String cityTableName = null;
    String currentCityTableName = null;
    String hometownCityTableName = null;

    // DO NOT modify this constructor
    public GetData(String u, Connection c) {
        super();
        String dataType = u;
        oracleConnection = c;
        userTableName = prefix + dataType + "_USERS";
        friendsTableName = prefix + dataType + "_FRIENDS";
        cityTableName = prefix + dataType + "_CITIES";
        currentCityTableName = prefix + dataType + "_USER_CURRENT_CITIES";
        hometownCityTableName = prefix + dataType + "_USER_HOMETOWN_CITIES";
    }

    // TODO: Implement this function
    @SuppressWarnings("unchecked")
    public JSONArray toJSON() throws SQLException {

        // This is the data structure to store all users' information
        JSONArray users_info = new JSONArray();
        
        try (Statement stmt = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            // Your implementation goes here....
            Statement stmt2 = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt3 = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet rst_users = null;
            rst_users = stmt.executeQuery("select USER_ID, FIRST_NAME, LAST_NAME, YEAR_OF_BIRTH, MONTH_OF_BIRTH, DAY_OF_BIRTH, GENDER from " + userTableName);
            while(rst_users.next()){
                JSONObject user = new JSONObject();

                user.put("user_id", rst_users.getInt("USER_ID"));
                user.put("first_name", rst_users.getString("FIRST_NAME"));
                user.put("last_name", rst_users.getString("LAST_NAME"));
                user.put("gender", rst_users.getString("GENDER"));
                user.put("YOB", rst_users.getInt("YEAR_OF_BIRTH"));
                user.put("MOB", rst_users.getInt("MONTH_OF_BIRTH"));
                user.put("DOB", rst_users.getInt("DAY_OF_BIRTH"));

                // get friends
                ResultSet rst_friends = null;
                rst_friends = stmt2.executeQuery("select USER2_ID as FRIEND_ID from " + friendsTableName + " where USER1_ID = " + rst_users.getInt("USER_ID"));
                JSONArray friends = new JSONArray();
                while(rst_friends.next()){
                    friends.put(rst_friends.getInt("FRIEND_ID"));
                }
                user.put("friends", friends);
                // get current city
                JSONObject current_city = new JSONObject();
                ResultSet rst_current_city = null;
                rst_current_city = stmt2.executeQuery("select CURRENT_CITY_ID from " + currentCityTableName + " where USER_ID = " + rst_users.getInt("USER_ID"));
                if(rst_current_city.next()){
                    ResultSet rst_current_city_info = null;
                    rst_current_city_info = stmt3.executeQuery("select COUNTRY_NAME, CITY_NAME, STATE_NAME from " + cityTableName + " where CITY_ID = " + rst_current_city.getInt("CURRENT_CITY_ID"));
                    if(rst_current_city_info.next()){
                        current_city.put("country", rst_current_city_info.getString("COUNTRY_NAME"));
                        current_city.put("city", rst_current_city_info.getString("CITY_NAME"));
                        current_city.put("state", rst_current_city_info.getString("STATE_NAME"));
                    }
                }
                user.put("current", current_city);
                // get hometown city
                JSONObject hometown_city = new JSONObject();
                ResultSet rst_hometown_city = null;
                rst_hometown_city = stmt2.executeQuery("select HOMETOWN_CITY_ID from " + hometownCityTableName + " where USER_ID = " + rst_users.getInt("USER_ID"));
                if(rst_hometown_city.next()){
                    ResultSet rst_hometown_city_info = null;
                    rst_hometown_city_info = stmt3.executeQuery("select COUNTRY_NAME, CITY_NAME, STATE_NAME from " + cityTableName + " where CITY_ID = " + rst_hometown_city.getInt("HOMETOWN_CITY_ID"));
                    if(rst_hometown_city_info.next()){
                        hometown_city.put("country", rst_hometown_city_info.getString("COUNTRY_NAME"));
                        hometown_city.put("city", rst_hometown_city_info.getString("CITY_NAME"));
                        hometown_city.put("state", rst_hometown_city_info.getString("STATE_NAME"));
                        
                    }
                }
                user.put("hometown", hometown_city);
                users_info.put(user);
            }
            
            stmt.close();
            stmt2.close();
            stmt3.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return users_info;
    }

    // This outputs to a file "output.json"
    // DO NOT MODIFY this function
    public void writeJSON(JSONArray users_info) {
        try {
            FileWriter file = new FileWriter(System.getProperty("user.dir") + "/output.json");
            file.write(users_info.toString());
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
