// Query 4
// Find user pairs (A,B) that meet the following constraints:
// i) user A is male and user B is female
// ii) their Year_Of_Birth difference is less than year_diff
// iii) user A and B are not friends
// iv) user A and B are from the same hometown city
// The following is the schema for output pairs:
// [
//      [user_id1, user_id2],
//      [user_id1, user_id3],
//      [user_id4, user_id2],
//      ...
//  ]
// user_id is the field from the users collection. Do not use the _id field in users.
// Return an array of arrays.

function suggest_friends(year_diff, dbname) {
    db = db.getSiblingDB(dbname);

    let pairs = [];
    // TODO: implement suggest friends
    db.users.find().forEach(function(user) {
        if(user.gender == "male"){
            db.users.find({gender:"female"}).forEach(function(partner){
                if(partner.hometown.city == user.hometown.city && partner.YOB - user.YOB < year_diff && user.YOB - partner.YOB < year_diff
                    && !user.friends.includes(partner.user_id) && !partner.friends.includes(user.user_id)){
                    pairs.push([user.user_id, partner.user_id]);
                }
            });
        }
    });
    return pairs;
}
