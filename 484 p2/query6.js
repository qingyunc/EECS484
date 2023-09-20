// Query 6
// Find the average friend count per user.
// Return a decimal value as the average user friend count of all users in the users collection.

function find_average_friendcount(dbname) {
    db = db.getSiblingDB(dbname);

    var friend_count = 0;
    var user_count = 0;
    // TODO: calculate the average friend count
    db.users.find().forEach(function(user) {
        friend_count += user.friends.length;
        user_count += 1;
    });
    return friend_count / user_count;
}
