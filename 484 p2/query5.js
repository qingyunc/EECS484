// Query 5
// Find the oldest friend for each user who has a friend. For simplicity,
// use only year of birth to determine age, if there is a tie, use the
// one with smallest user_id. You may find query 2 and query 3 helpful.
// You can create selections if you want. Do not modify users collection.
// Return a javascript object : key is the user_id and the value is the oldest_friend id.
// You should return something like this (order does not matter):
// {user1:userx1, user2:userx2, user3:userx3,...}

function oldest_friend(dbname) {
    db = db.getSiblingDB(dbname);

    let results = {};
    // TODO: implement oldest friends

    db.users.find().forEach(function(user) {
        var oldest_friend = null;
        var earliest_birth = Infinity;

        db.users.find(
            {friends: {$in: [user.user_id]}}
        ).forEach((friend_s) => {
            if (friend_s.YOB <= earliest_birth) {
                if(friend_s.YOB < earliest_birth){
                    oldest_friend = friend_s;
                    earliest_birth = friend_s.YOB;
                }else{
                    if(friend_s.user_id < oldest_friend.user_id){
                        oldest_friend = friend_s;
                        earliest_birth = friend_s.YOB;
                    }
                }

            }
        });

        for (var i = 0; i < user.friends.length; i++) {
            var friend_l = db.users.findOne({ user_id: user.friends[i]});
            if (friend_l && friend_l.YOB <= earliest_birth) {
                if(friend_l.YOB < earliest_birth){
                    oldest_friend = friend_l;
                    earliest_birth = friend_l.YOB;
                }else{
                    if(friend_l.user_id < oldest_friend.user_id){
                        oldest_friend = friend_l;
                        earliest_birth = friend_l.YOB;
                    }
                }
            }
        }
        if(oldest_friend) results[user.user_id] = oldest_friend.user_id;
    });
    

    return results;
}
