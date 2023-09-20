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
            return new FirstNameInfo(); // placeholder for compilation
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
                "SELECT DISTINCT User_ID, First_Name, Last_Name " +
                "From " + UsersTable + " " +
                "WHERE User_ID NOT IN " +
                "(SELECT DISTINCT User_ID1 AS User_ID FROM " + FriendsTable +
                " UNION SELECT DISTINCT User_ID2 AS User_ID FROM " + FriendsTable + ") " +
                "ORDER BY User_ID ASC"
            );
            while(rst.next()) {
                UserInfo u1 = new UserInfo(rst.getInt(1), rst.getString(2), rst.getString(3));
                results.add(u1);
            }

            stmt.close()
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
            ResultSet rst = stmt.executeQuery(
                "SELECT X.* FROM " +
                "(SELECT T.Tag_photo_id, "  +
                "P.Album_id, P.Photo_link, " +
                "A.Album_name"
                " FROM " + 
                "(SELECT Tag_photo_id, " +
                "COUNT(*) NUM " +
                "FROM " + 
                TagsTable + " " +
                "GROUP BY Tag_photo_id ) T " +
                "LEFT JOIN " + PhotosTable + 
                " P on P.Photo_id = T.Tag_photo_id " +
                "LEFT JOIN " + AlbumsTable + 
                " A on A.Album_id = P.Album_id " +
                "WHERE P.Album_id IS NOT NULL AND A.Album_name IS NOT NULL " +
                "ORDER BY T.NUM DESC, T.Tag_photo_id ASC,) X " +
                "WHERE ROW <= " + num);
            
            PhotoInfo photo = new PhotoInfo(0, 0, "", "");
            TaggedPhotoInfo tagphoto = new TaggedPhotoInfo(photo);
            UserInfo user = new UserInfo(0, "", "");

            while(rst.next()) {
                Statement statement = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly);
                photo = new PhotoInfo(rst.getLong(1), rst.getLong(2), rst.getString(3), rst.getString(4));
                tagphoto = new TaggedPhotoInfo(photo);
                ResultSet rst_1 = statement.executeQuery(
                    "SELECT U.User_id, U.First_name, U.Last_name FROM " +
                    UsersTable + " U " +
                    "JOIN " + TagsTable + " T ON T.Tag_subject_id = U.User_id " +
                    "WHERE T.Tag_photo_id = " + rst.getLong(1) + " " +
                    " AND U.Last_name IS NOT NULL " +
                    "ORDER BY U.User_id ASC");
                while(rst_1.next()) {
                    user = new UserInfo(rst_1.getLong(5), rst_1.getString(6), rst_1.getString(7));
                    tagphoto.addTaggedUser(user);
                }
                results.add(tagphoto);
                rst_1.close();
                statement.close();
            }
            
            rst.close();
            stmt.close();
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
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                EventStateInfo info = new EventStateInfo(50);
                info.addState("Kentucky");
                info.addState("Hawaii");
                info.addState("New Hampshire");
                return info;
            */
            return new EventStateInfo(-1); // placeholder for compilation
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new EventStateInfo(-1);
        }
    }

    @Override

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

            ResultSet rst = stmt.executeQuery(
                "SELECT User1.User_id, User1.First_name, User1.Last_name, User2.User_id, User2.First_name, User2.Last_name " + 
                "FROM (SELECT * FROM (SELECT T.U1, T.U2, COUNT(*) as NUM " +
                "FROM (SELECT FT1.User2_id AS U1, FT2.User2_id AS U2 " +
                "FROM " + FriendsTable + " FT1 " +
                "JOIN " + FriendsTable + " FT2 " +
                "ON FT1.User1_id != FT1.User2_id " +   
                "AND FT2.User1_id != FT2.User2_id " +
                "AND FT1.User1_id = FT2.User1_id " +
                "AND FT1.User2_id < FT2.User2_id " +
                "AND (FT1.User2_id, FT2.User2_id) NOT IN (SELECT * FROM " + FriendsTable + ") " +
                "UNION ALL SELECT FT3.User2_id AS U1, FT4.User1_id AS U2 " + 
                "FROM " + FriendsTable + " FT3 " + 
                "JOIN " + FriendsTable + " FT4 " +
                "ON FT3.User1_id != FT3.User2_id " +  
                "AND FT4.User1_id != FT4.User2_id " +
                "AND FT3.User1_id = FT4.User2_id " +
                "AND FT3.User2_id < FT4.User1_id " +
                "AND (FT3.User2_id, FT4.User1_id) NOT IN (SELECT * FROM " + FriendsTable + ") " +
                "UNION ALL SELECT FT5.User1_id AS U1, FT6.User1_id AS U2 " +
                "FROM " + FriendsTable + " FT5 " +
                "JOIN " + FriendsTable + " FT6 " +
                "ON FT5.User1_id != FT5.User2_id " +  
                "AND FT6.User1_id != FT6.User2_id " +
                "AND FT5.User2_id = FT6.User2_id " +
                "AND FT5.User1_id < FT6.User1_id " +
                "AND (FT5.User1_id, FT6.User1_id) NOT IN (SELECT * FROM " + FriendsTable + ") " +
                "UNION ALL SELECT FT7.User1_id AS U1, FT8.User2_id AS U2 " +
                "FROM " + FriendsTable + " FT7 " +
                "JOIN " + FriendsTable + " FT8 " +
                "ON FT7.User1_id != FT7.User2_id " +  
                "AND FT8.User1_id != FT8.User2_id " +
                "AND FT7.User2_id = FT8.User1_id " +
                "AND FT7.User1_id < FT8.User2_id " +
                "AND (FT7.User1_id, FT8.User2_id) NOT IN (SELECT * FROM " + FriendsTable + ")) T " +
                "GROUP BY T.U1, T.U2 " +
                "ORDER BY NUM DESC, T.U1 ASC, T.U2 ASC) " +
                "WHERE ROWNUM <= " + num + ") T2 " +
                "LEFT JOIN " + UsersTable + " User1 " +
                "ON T2.U1 = User1.User_id " +
                "LEFT JOIN " + UsersTable + " User2 " +
                "ON T2.U2 = User2.User_id"
            );
            
           

            while(rst.next()){
                long I1 = rst.getLong(1);
                long I2 = rst.getLong(2);
                
                String F1 = rst.getString(3);
                String F2 = rst.getString(4);
                
                String L1 = rst.getString(5);
                String L2 = rst.getString(6);
                
                UserInfo user1 = new UserInfo(I1, F1, L1);
                UserInfo user2 = new UserInfo(I2, F2, L2);
                UsersPair userpair = new UsersPair(user1, user2);
                
                Statement statement = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly);
                
                ResultSet rst_1 = statement.executeQuery(
                    "SELECT User_id, First_name, Last_name " +
                    "FROM (SELECT FT1.User1_id AS U_id " +
                    "FROM " + FriendsTable + " FT1 " +
                    "JOIN " + FriendsTable + " FT2 " +
                    "ON FT1.User1_id = FT2.User1_id " +
                    "AND FT1.User2_id = " + I1 + " " +
                    "AND FT2.User2_id = " + I2 + " " +
                    "UNION " +
                    "SELECT FT1.User1_id AS U_id " +
                    "FROM " + FriendsTable + " FT1 " +
                    "JOIN " + FriendsTable + " FT2 " +
                    "ON FT1.User1_id = FT2.User2_id " +
                    "AND FT1.User2_id = " + I1 + " " +
                    "AND FT2.User1_id = " + I2 + " " +
                    "UNION " +
                    "SELECT FT1.User2_id AS U_id " +
                    "FROM " + FriendsTable + " FT1 " +
                    "JOIN " + FriendsTable + " FT2 " +
                    "ON FT1.User2_id = FT2.User2_id " +
                    "AND FT1.User1_id = " + I1 + " " +
                    "AND FT2.User1_id = " + I2 + " " +
                    "UNION " +
                    "SELECT FT1.User2_id AS U_id " +
                    "FROM " + FriendsTable + " FT1 " +
                    "JOIN " + FriendsTable + " FT2 " +
                    "ON FT1.User2_id = FT2.User1_id " +
                    "AND FT1.User1_id = " + I1 + " " + 
                    "AND FT2.User2_id = " + I2 + ") T " +
                    "LEFT JOIN " + UsersTable + " U1 " +
                    "ON U1.User_id = T.U_id " +
                    "ORDER BY U1.user_id ASC"
                );
                while(rst_1.next()){
                    UserInfo user3 = new UserInfo(rst_1.getLong(7), rst_1.getString(8), rst_1.getString(9));
                    userpair.addSharedFriend(user3);
                }
                results.add(userpair);
                rst_1.close();
            }
            rst.close();
            stmt.close();
            return results;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }