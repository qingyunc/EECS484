package project2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

/*
    The StudentFakebookOracle class is derived from the FakebookOracle class and implements
    the abstract query functions that investigate the database provided via the <connection>
    parameter of the constructor to discover specific information.
*/
public final class StudentFakebookOracle extends FakebookOracle {
    // [Constructor]
    // REQUIRES: <connection> is a valid JDBC connection
    public StudentFakebookOracle(Connection connection) {
        oracle = connection;
    }

    @Override
    // Query 0
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the total number of users for which a birth month is listed
    //        (B) Find the birth month in which the most users were born
    //        (C) Find the birth month in which the fewest users (at least one) were born
    //        (D) Find the IDs, first names, and last names of users born in the month
    //            identified in (B)
    //        (E) Find the IDs, first names, and last name of users born in the month
    //            identified in (C)
    //
    // This query is provided to you completed for reference. Below you will find the appropriate
    // mechanisms for opening up a statement, executing a query, walking through results, extracting
    // data, and more things that you will need to do for the remaining nine queries
    public BirthMonthInfo findMonthOfBirthInfo() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            // Step 1
            // ------------
            // * Find the total number of users with birth month info
            // * Find the month in which the most users were born
            // * Find the month in which the fewest (but at least 1) users were born
            ResultSet rst = stmt.executeQuery(
                    "SELECT COUNT(*) AS Birthed, Month_of_Birth " + // select birth months and number of uses with that birth month
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth IS NOT NULL " + // for which a birth month is available
                            "GROUP BY Month_of_Birth " + // group into buckets by birth month
                            "ORDER BY Birthed DESC, Month_of_Birth ASC"); // sort by users born in that month, descending; break ties by birth month

            int mostMonth = 0;
            int leastMonth = 0;
            int total = 0;
            while (rst.next()) { // step through result rows/records one by one
                if (rst.isFirst()) { // if first record
                    mostMonth = rst.getInt(2); //   it is the month with the most
                }
                if (rst.isLast()) { // if last record
                    leastMonth = rst.getInt(2); //   it is the month with the least
                }
                total += rst.getInt(1); // get the first field's value as an integer
            }
            BirthMonthInfo info = new BirthMonthInfo(total, mostMonth, leastMonth);

            // Step 2
            // ------------
            // * Get the names of users born in the most popular birth month
            rst = stmt.executeQuery(
                    "SELECT User_ID, First_Name, Last_Name " + // select ID, first name, and last name
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth = " + mostMonth + " " + // born in the most popular birth month
                            "ORDER BY User_ID"); // sort smaller IDs first

            while (rst.next()) {
                info.addMostPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 3
            // ------------
            // * Get the names of users born in the least popular birth month
            rst = stmt.executeQuery(
                    "SELECT User_ID, First_Name, Last_Name " + // select ID, first name, and last name
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth = " + leastMonth + " " + // born in the least popular birth month
                            "ORDER BY User_ID"); // sort smaller IDs first

            while (rst.next()) {
                info.addLeastPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 4
            // ------------
            // * Close resources being used
            rst.close();
            stmt.close(); // if you close the statement first, the result set gets closed automatically

            return info;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new BirthMonthInfo(-1, -1, -1);
        }
    }

    @Override
    // Query 1
    // -----------------------------------------------------------------------------------
    // GOALS: (A) The first name(s) with the most letters
    //        (B) The first name(s) with the fewest letters
    //        (C) The first name held by the most users
    //        (D) The number of users whose first name is that identified in (C)
    public FirstNameInfo findNameInfo() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
                    FirstNameInfo info = new FirstNameInfo();
                    int count = 0;
                    // get first name with most letters
                    ResultSet rst = stmt.executeQuery(
                            "SELECT DISTINCT First_Name " +
                                    "FROM " + UsersTable + " " +
                                    "WHERE LENGTH(First_Name) = (SELECT MAX(LENGTH(First_Name)) FROM " + UsersTable + ") " +
                                    "ORDER BY First_Name");
                    while (rst.next()) {
                        info.addLongName(rst.getString(1));
                    }
                    // get first name with fewest letters
                    rst = stmt.executeQuery(
                            "SELECT DISTINCT First_Name " +
                                    "FROM " + UsersTable + " " +
                                    "WHERE LENGTH(First_Name) = (SELECT MIN(LENGTH(First_Name)) FROM " + UsersTable + ") " +
                                    "ORDER BY First_Name");
                    while (rst.next()) {
                        info.addShortName(rst.getString(1));
                    }
                    // get first name held by most users
                    rst = stmt.executeQuery(
                            "SELECT First_Name, COUNT(*) AS Count " +
                                    "FROM " + UsersTable + " " +
                                    "GROUP BY First_Name " +
                                    "ORDER BY Count DESC, First_Name ASC");
                    while(rst.next()) {
                        if (rst.isFirst()) {
                            info.addCommonName(rst.getString(1));
                            info.setCommonNameCount(rst.getInt(2));
                            count = rst.getInt(2);
                        }else{
                            if (rst.getInt(2) == count) {
                                info.addCommonName(rst.getString(1));
                            }else{
                                break;
                            }
                        }
                    }
                    
                    rst.close();
                    stmt.close();
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                FirstNameInfo info = new FirstNameInfo();
                info.addLongName("Aristophanes");
                info.addLongName("Michelangelo");
                info.addLongName("Peisistratos");
                info.addShortName("Bob");
                info.addShortName("Sue");
                info.addCommonName("Harold");
                info.addCommonName("Jessica");
                info.setCommonNameCount(42);
                return info;
            */
            return info; // placeholder for compilation
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new FirstNameInfo();
        }
    }

    @Override
   // Query 2
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users without any friends
    //
    // Be careful! Remember that if two users are friends, the Friends table only contains
    // the one entry (U1, U2) where U1 < U2.
    public FakebookArrayList<UserInfo> lonelyUsers() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(15, "Abraham", "Lincoln");
                UserInfo u2 = new UserInfo(39, "Margaret", "Thatcher");
                results.add(u1);
                results.add(u2);
            */
            ResultSet rst = stmt.executeQuery(
                "SELECT User_ID, First_Name, Last_Name " +
                "FROM " + UsersTable + " " +
                " WHERE " +
                "User_ID NOT IN " + 
                "(SELECT DISTINCT User1_id FROM " + 
                FriendsTable + ") " +
                " AND " + 
                "User_ID NOT IN " + 
                "(SELECT DISTINCT User2_id FROM " 
                + FriendsTable + ") " +
                " AND " + 
                "Last_name IS NOT NULL " +
                " ORDER BY User_id ASC "
            );
            
            while(rst.next()) {
                UserInfo user = new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3));
                results.add(user);
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 3
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users who no longer live
    //            in their hometown (i.e. their current city and their hometown are different)
    public FakebookArrayList<UserInfo> liveAwayFromHome() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
                    // get users who no longer live in their hometown order by user id ascending
                    ResultSet rst = stmt.executeQuery(
                            "SELECT USER_ID, FIRST_NAME, LAST_NAME " +
                                    "FROM " + UsersTable + " " +
                                    "WHERE USER_ID IN " +
                            "(SELECT USER_ID " +
                                    "FROM " + CurrentCitiesTable + " " +
                                    "WHERE CURRENT_CITY_ID != (SELECT HOMETOWN_CITY_ID FROM " + HometownCitiesTable + " WHERE USER_ID = "
                                    + CurrentCitiesTable + ".USER_ID)) ORDER BY USER_ID ASC");
                    while (rst.next()) {
                        results.add(new UserInfo(rst.getInt(1), rst.getString(2), rst.getString(3)));
                    }
                    rst.close();
                    stmt.close();
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(9, "Meryl", "Streep");
                UserInfo u2 = new UserInfo(104, "Tom", "Hanks");
                results.add(u1);
                results.add(u2);
            */
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 4
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, links, and IDs and names of the containing album of the top
    //            <num> photos with the most tagged users
    //        (B) For each photo identified in (A), find the IDs, first names, and last names
    //            of the users therein tagged
    public FakebookArrayList<TaggedPhotoInfo> findPhotosWithMostTags(int num) throws SQLException {
        FakebookArrayList<TaggedPhotoInfo> results = new FakebookArrayList<TaggedPhotoInfo>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
                    int count = 0;
                    // find the photos with the most tagged users
                    ResultSet rst2;
                    ResultSet rst = stmt.executeQuery(
                            "SELECT P.PHOTO_ID, P.PHOTO_LINK, A.ALBUM_ID, A.ALBUM_NAME " +
                                    "FROM " + PhotosTable + " P" +
                                    " INNER JOIN " + AlbumsTable + " A " + " ON P.ALBUM_ID = A.ALBUM_ID" +
                                    " INNER JOIN " + TagsTable + " T " + "ON P.PHOTO_ID = T.TAG_PHOTO_ID" +
                                    " GROUP BY P.PHOTO_ID, P.PHOTO_LINK, A.ALBUM_ID, A.ALBUM_NAME" +
                                    " ORDER BY COUNT(T.TAG_SUBJECT_ID) DESC, P.PHOTO_ID ASC");
                    
                    // add the photos to the results
                    while (rst.next() && count < num) {
                        Statement stmt2 = oracle.createStatement(FakebookOracleConstants.AllScroll,
                        FakebookOracleConstants.ReadOnly);
                        PhotoInfo p = new PhotoInfo(rst.getInt(1), rst.getInt(3), rst.getString(2), rst.getString(4));
                        // find the users tagged in the photo
                        rst2 = stmt2.executeQuery(
                                "SELECT U.USER_ID, U.FIRST_NAME, U.LAST_NAME " +
                                        "FROM " + UsersTable + " U" +
                                        " INNER JOIN " + TagsTable + " T " + "ON U.USER_ID = T.TAG_SUBJECT_ID" +
                                        " WHERE T.TAG_PHOTO_ID = " + rst.getInt(1) + " ORDER BY U.USER_ID ASC");
                        TaggedPhotoInfo tp = new TaggedPhotoInfo(p);
                        while (rst2.next()) {
                            tp.addTaggedUser(new UserInfo(rst2.getInt(1), rst2.getString(2), rst2.getString(3)));
                        }
                        results.add(tp);
                        count++;
                        rst2.close();
                        stmt2.close();
                    }
                    rst.close();
                    stmt.close();
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                PhotoInfo p = new PhotoInfo(80, 5, "www.photolink.net", "Winterfell S1");
                UserInfo u1 = new UserInfo(3901, "Jon", "Snow");
                UserInfo u2 = new UserInfo(3902, "Arya", "Stark");
                UserInfo u3 = new UserInfo(3903, "Sansa", "Stark");
                TaggedPhotoInfo tp = new TaggedPhotoInfo(p);
                tp.addTaggedUser(u1);
                tp.addTaggedUser(u2);
                tp.addTaggedUser(u3);
                results.add(tp);
            */
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 5
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, last names, and birth years of each of the two
    //            users in the top <num> pairs of users that meet each of the following
    //            criteria:
    //              (i) same gender
    //              (ii) tagged in at least one common photo
    //              (iii) difference in birth years is no more than <yearDiff>
    //              (iv) not friends
    //        (B) For each pair identified in (A), find the IDs, links, and IDs and names of
    //            the containing album of each photo in which they are tagged together
    public FakebookArrayList<MatchPair> matchMaker(int num, int yearDiff) throws SQLException {
        FakebookArrayList<MatchPair> results = new FakebookArrayList<MatchPair>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
                    // select top num pairs of users and photos that meet criteria
                    // The pairs of users should be reported in (and cut-off based on) descending order by the number of photos in which the two users were tagged together.
                    int counter = 0;
                    ResultSet rst = stmt.executeQuery(
                        "SELECT U1.USER_ID, U1.FIRST_NAME, U1.LAST_NAME, U1.YEAR_OF_BIRTH, U2.USER_ID, U2.FIRST_NAME, U2.LAST_NAME, U2.YEAR_OF_BIRTH, P.PHOTO_ID, P.PHOTO_LINK, A.ALBUM_ID, A.ALBUM_NAME, COUNT(*) FROM " 
                        + UsersTable + " U1" +
                        " INNER JOIN " + UsersTable + " U2 ON U1.USER_ID < U2.USER_ID" +
                        " INNER JOIN " + TagsTable + " T1 ON U1.USER_ID = T1.TAG_SUBJECT_ID" +
                        " INNER JOIN " + TagsTable + " T2 ON U2.USER_ID = T2.TAG_SUBJECT_ID" +
                        " INNER JOIN " + PhotosTable + " P ON T1.TAG_PHOTO_ID = P.PHOTO_ID" +
                        " INNER JOIN " + AlbumsTable + " A ON P.ALBUM_ID = A.ALBUM_ID" +
                        " WHERE U1.GENDER IS NOT NULL AND U2.GENDER IS NOT NULL AND U1.YEAR_OF_BIRTH IS NOT NULL AND U2.YEAR_OF_BIRTH IS NOT NULL" + 
                        " AND U1.GENDER = U2.GENDER AND T1.TAG_PHOTO_ID = T2.TAG_PHOTO_ID " +
                        " AND ABS(U1.YEAR_OF_BIRTH - U2.YEAR_OF_BIRTH) <= " + yearDiff +
                        " AND NOT EXISTS (SELECT * FROM " + FriendsTable + " F WHERE (F.USER1_ID = U1.USER_ID AND F.USER2_ID = U2.USER_ID) OR (F.USER1_ID = U2.USER_ID AND F.USER2_ID = U1.USER_ID))" + 
                        " GROUP BY U1.USER_ID, U1.FIRST_NAME, U1.LAST_NAME, U1.YEAR_OF_BIRTH, U2.USER_ID, U2.FIRST_NAME, U2.LAST_NAME, U2.YEAR_OF_BIRTH, P.PHOTO_ID, P.PHOTO_LINK, A.ALBUM_ID, A.ALBUM_NAME" +
                        " ORDER BY COUNT(*) DESC, U1.USER_ID ASC, U2.USER_ID ASC, P.PHOTO_ID ASC"
                    );
                    while(rst.next() && counter < num){

                        UserInfo u1 = new UserInfo(rst.getInt(1), rst.getString(2), rst.getString(3));
                        UserInfo u2 = new UserInfo(rst.getInt(5), rst.getString(6), rst.getString(7));
                        MatchPair mp = new MatchPair(u1, rst.getInt(4), u2, rst.getInt(8));

                        PhotoInfo p = new PhotoInfo(rst.getInt(9), rst.getInt(11), rst.getString(10), rst.getString(12));

                        mp.addSharedPhoto(p);

                        results.add(mp);
                        counter += 1;
                    }
                    rst.close();
                    stmt.close();

            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(93103, "Romeo", "Montague");
                UserInfo u2 = new UserInfo(93113, "Juliet", "Capulet");
                MatchPair mp = new MatchPair(u1, 1597, u2, 1597);
                PhotoInfo p = new PhotoInfo(167, 309, "www.photolink.net", "Tragedy");
                mp.addSharedPhoto(p);
                results.add(mp);
            */
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 6
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of each of the two users in
    //            the top <num> pairs of users who are not friends but have a lot of
    //            common friends
    //        (B) For each pair identified in (A), find the IDs, first names, and last names
    //            of all the two users' common friends
    public FakebookArrayList<UsersPair> suggestFriends(int num) throws SQLException {
        FakebookArrayList<UsersPair> results = new FakebookArrayList<UsersPair>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {

                    int counter = 0;
                    ResultSet rst2;
                    String bi_FriendsTable = "(SELECT USER1_ID, USER2_ID FROM " + FriendsTable + " UNION (SELECT USER2_ID, USER1_ID FROM " + FriendsTable + "))";

                    // select pairs of users who have common friends
                    // The pairs of users should be reported in descending order by the number of common friends.
                    ResultSet rst = stmt.executeQuery(
                        "SELECT U1.USER_ID, U1.FIRST_NAME, U1.LAST_NAME, U2.USER_ID, U2.FIRST_NAME, U2.LAST_NAME, COUNT(*) FROM " + bi_FriendsTable + " F1" +
                        " INNER JOIN " + bi_FriendsTable + " F2 ON F1.USER1_ID < F2.USER1_ID" +
                        " INNER JOIN " + UsersTable + " U1 ON F1.USER1_ID = U1.USER_ID" +
                        " INNER JOIN " + UsersTable + " U2 ON F2.USER1_ID = U2.USER_ID" +
                        " WHERE F1.USER2_ID = F2.USER2_ID" +
                        " AND NOT EXISTS (SELECT * FROM " + FriendsTable + " F WHERE (F.USER1_ID = U1.USER_ID AND F.USER2_ID = U2.USER_ID) OR (F.USER1_ID = U2.USER_ID AND F.USER2_ID = U1.USER_ID))" +
                        " GROUP BY U1.USER_ID, U1.FIRST_NAME, U1.LAST_NAME, U2.USER_ID, U2.FIRST_NAME, U2.LAST_NAME" +
                        " ORDER BY COUNT(*) DESC, U1.USER_ID ASC, U2.USER_ID ASC"
                    );

                    while(rst.next() && counter < num){
                        Statement stmt2 = oracle.createStatement(FakebookOracleConstants.AllScroll,
                        FakebookOracleConstants.ReadOnly);
                        UserInfo u1 = new UserInfo(rst.getInt(1), rst.getString(2), rst.getString(3));
                        UserInfo u2 = new UserInfo(rst.getInt(4), rst.getString(5), rst.getString(6));
                        UsersPair up = new UsersPair(u1, u2);

                        // select the IDs, first names, and last names of all the two users' common friends
                        rst2 = stmt2.executeQuery(
                            "SELECT U.USER_ID, U.FIRST_NAME, U.LAST_NAME FROM " + bi_FriendsTable + " F1" +
                            " INNER JOIN " + bi_FriendsTable + " F2 ON F1.USER1_ID < F2.USER1_ID" +
                            " INNER JOIN " + UsersTable + " U ON F1.USER2_ID = U.USER_ID" +
                            " WHERE F1.USER1_ID = " + rst.getInt(1) + " AND F2.USER1_ID = " + rst.getInt(4) +
                            " AND F1.USER2_ID = F2.USER2_ID" +
                            " ORDER BY U.USER_ID ASC"
                        );

                        while(rst2.next()){
                            UserInfo u = new UserInfo(rst2.getInt(1), rst2.getString(2), rst2.getString(3));
                            up.addSharedFriend(u);
                        }

                        rst2.close();
                        stmt2.close();
                        results.add(up);
                        counter += 1;
                    }
                    rst.close();
                    stmt.close();

            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(16, "The", "Hacker");
                UserInfo u2 = new UserInfo(80, "Dr.", "Marbles");
                UserInfo u3 = new UserInfo(192, "Digit", "Le Boid");
                UsersPair up = new UsersPair(u1, u2);
                up.addSharedFriend(u3);
                results.add(up);
            */
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 7
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the name of the state or states in which the most events are held
    //        (B) Find the number of events held in the states identified in (A)
    public EventStateInfo findEventStates() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
                    //find the count of events in each state
                    ResultSet rst = stmt.executeQuery(
                        "SELECT C.STATE_NAME, COUNT(*)" +
                        " FROM " + EventsTable + " E" +
                        " INNER JOIN " + CitiesTable + " C ON C.CITY_ID = E.EVENT_CITY_ID" +
                        " GROUP BY C.STATE_NAME" +
                        " ORDER BY COUNT(*) DESC, C.STATE_NAME ASC"
                    );

                    int count = 0;
                    String state = "";
                    if(rst.next()){
                        count = rst.getInt(2);
                        state = rst.getString(1);
                    }
                    EventStateInfo info = new EventStateInfo(count);
                    info.addState(state);

                    while(rst.next()){
                        if(rst.getInt(2) == count){
                            info.addState(rst.getString(1));
                        }else{
                            break;
                        }
                    }

                    rst.close();
                    stmt.close();
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                EventStateInfo info = new EventStateInfo(50);
                info.addState("Kentucky");
                info.addState("Hawaii");
                info.addState("New Hampshire");
                return info;
            */
            return info; // placeholder for compilation
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new EventStateInfo(-1);
        }
    }

    @Override
     // Query 8
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the ID, first name, and last name of the oldest friend of the user
    //            with User ID <userID>
    //        (B) Find the ID, first name, and last name of the youngest friend of the user
    //            with User ID <userID>
    public AgeInfo findAgeInfo(long userID) throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo old = new UserInfo(12000000, "Galileo", "Galilei");
                UserInfo young = new UserInfo(80000000, "Neil", "deGrasse Tyson");
                return new AgeInfo(old, young);
            */
            
            ResultSet rst = stmt.executeQuery(
                "SELECT User_id, First_name, Last_name, " +
                "Year_of_birth, Month_of_birth, Day_of_birth " +
                "FROM " + 
                UsersTable + 
                " " +
                "WHERE User_id in (SELECT User1_ID " + 
                "FROM " + 
                FriendsTable + 
                " " + 
                "WHERE User2_id = " + 
                userID + ") " + 
                "OR User_id in (SELECT User2_ID " + 
                "FROM " + FriendsTable + " " + 
                "WHERE User1_ID = " + 
                userID + ") " + 
                "ORDER BY Year_of_birth DESC, Month_of_birth DESC, Day_of_birth DESC, User_id DESC");
            
            UserInfo y = new UserInfo(-1, "ERROR", "ERROR");
            while(rst.next()){
                if(rst.isFirst()){
                    y = new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3));
                }
            }

            ResultSet rst_1 = stmt.executeQuery(
                "SELECT User_id, First_name, Last_name, " +
                "Year_of_birth, Month_of_birth, Day_of_birth " +
                "FROM " + 
                UsersTable + 
                " " +
                "WHERE User_id in (SELECT User1_ID " + 
                "FROM " + 
                FriendsTable + 
                " " + 
                "WHERE User2_id = " + 
                userID + ") " + 
                "OR User_id in (SELECT User2_ID " + 
                "FROM " + 
                FriendsTable + 
                " " + 
                "WHERE User1_ID = " + 
                userID + ") " + 
                "ORDER BY Year_of_birth ASC, Month_of_birth ASC, Day_of_birth ASC, User_id DESC");
            
            UserInfo o = new UserInfo(-1, "ERROR", "ERROR");
            while(rst_1.next()){
                if(rst_1.isFirst()){
                    o = new UserInfo(rst_1.getLong(1), rst_1.getString(2), rst_1.getString(3));
                }
            }

            rst.close();
            stmt.close();
            return new AgeInfo(o, y);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new AgeInfo(new UserInfo(-1, "ERROR", "ERROR"), new UserInfo(-1, "ERROR", "ERROR"));
        }
    }

    @Override
    // Query 9
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find all pairs of users that meet each of the following criteria
    //              (i) same last name
    //              (ii) same hometown
    //              (iii) are friends
    //              (iv) less than 10 birth years apart
    public FakebookArrayList<SiblingInfo> findPotentialSiblings() throws SQLException {
        FakebookArrayList<SiblingInfo> results = new FakebookArrayList<SiblingInfo>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
                    // find all pairs of users that meet criteria (i) - (iv)
                    ResultSet rst = stmt.executeQuery(
                        "SELECT U1.USER_ID, U1.FIRST_NAME, U1.LAST_NAME, U2.USER_ID, U2.FIRST_NAME, U2.LAST_NAME FROM " 
                    + UsersTable + " U1" + 
                    " INNER JOIN " + UsersTable + " U2 ON U1.USER_ID < U2.USER_ID" +
                    " INNER JOIN " + HometownCitiesTable + " H1 ON U1.USER_ID = H1.USER_ID" +
                    " INNER JOIN " + HometownCitiesTable + " H2 ON U2.USER_ID = H2.USER_ID" +
                    " AND U1.YEAR_OF_BIRTH IS NOT NULL AND U2.YEAR_OF_BIRTH IS NOT NULL" + 
                    " WHERE U1.LAST_NAME = U2.LAST_NAME AND H1.HOMETOWN_CITY_ID = H2.HOMETOWN_CITY_ID AND ABS(U1.YEAR_OF_BIRTH - U2.YEAR_OF_BIRTH) < 10" + 
                    " AND EXISTS (SELECT * FROM " + FriendsTable + " F WHERE (F.USER1_ID = U1.USER_ID AND F.USER2_ID = U2.USER_ID) OR (F.USER1_ID = U2.USER_ID AND F.USER2_ID = U1.USER_ID))" + 
                    " ORDER BY U1.USER_ID ASC, U2.USER_ID ASC"
                    );
                    while (rst.next()) {
                        UserInfo u1 = new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3));
                        UserInfo u2 = new UserInfo(rst.getLong(4), rst.getString(5), rst.getString(6));
                        SiblingInfo si = new SiblingInfo(u1, u2);
                        results.add(si);
                    }
                    rst.close();
                    stmt.close();

            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(81023, "Kim", "Kardashian");
                UserInfo u2 = new UserInfo(17231, "Kourtney", "Kardashian");
                SiblingInfo si = new SiblingInfo(u1, u2);
                results.add(si);
            */
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    // Member Variables
    private Connection oracle;
    private final String UsersTable = FakebookOracleConstants.UsersTable;
    private final String CitiesTable = FakebookOracleConstants.CitiesTable;
    private final String FriendsTable = FakebookOracleConstants.FriendsTable;
    private final String CurrentCitiesTable = FakebookOracleConstants.CurrentCitiesTable;
    private final String HometownCitiesTable = FakebookOracleConstants.HometownCitiesTable;
    private final String ProgramsTable = FakebookOracleConstants.ProgramsTable;
    private final String EducationTable = FakebookOracleConstants.EducationTable;
    private final String EventsTable = FakebookOracleConstants.EventsTable;
    private final String AlbumsTable = FakebookOracleConstants.AlbumsTable;
    private final String PhotosTable = FakebookOracleConstants.PhotosTable;
    private final String TagsTable = FakebookOracleConstants.TagsTable;
}
