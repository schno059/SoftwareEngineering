const mongojs = require('mongojs'); //imports 'mongojs'
const assert = require('assert'); //Assertion for queries

// Connection URL
const url = "mongodb://localhost:27017/YOURDBHERE"; 
//URL with database included for local mongo db

// Database Name
const collections=[restaurants]; //list of collections that you will be accessing. 
mongodb = mongojs(url, collections);

module.exports = {
        
    printAllInCollection : function(collectionName, callback){
        var cursor = mongodb.collection(collectionName).find({}).limit(10, function(err, docs){

            if(err || !docs) {
                console.log("Cannot print database or database is empty\n");
            }
            else {
                //console.log(collectionName, docs);

                callback(docs);
            }
        });

    }
}

