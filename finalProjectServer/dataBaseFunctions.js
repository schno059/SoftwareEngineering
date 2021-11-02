/*
========== INITIALIZE DB FUNCTIONS ==========
*/

var mongojs = require("mongojs");
var url = 'mongodb://ukko.d.umn.edu:12009/finalProject';
var collections = ['names'];
var assert = require('assert');
var DBRef = mongojs(url, collections);

/*
========== SIMPLE GET FUNCTIONS ==========
*/

module.exports.getByYear = function(year, callback) {
  DBRef.collection('names').find({"year": Number(year)}).toArray(function(err, docs) {
    if (err) {
      console.log(err);
    } else {
      callback(docs);
    }
  });
};

module.exports.getByName = function(name, callback) {
  DBRef.collection('names').find({"name": name}).toArray(function(err, docs) {
    if (err) {
      console.log(err);
    } else {
      callback(docs);
    }
  });
};

module.exports.getBySex = function(sex, callback) {
  DBRef.collection('names').find({"sex": sex}).toArray(function(err, docs) {
    if (err) {
      console.log(err);
    } else {
      callback(docs);
    }
  });
};

module.exports.getAllNames = function(callback) {
  DBRef.collection('names').find({}).toArray(function(err, docs) {
    if (err) {
      console.log(err);
    } else {
      callback(docs);
    }
  });
};

module.exports.getScoreData = function(callback) {
  //return scores sorted by high scores, low times, alphabetical names, and _id if all are equal
  DBRef.collection('scores').aggregate([{$sort:{score:-1, time:1, name:1, _id:1}}]).toArray(function(err, docs) {
    if (err) {
      console.log(err);
    } else {
      callback(docs);
    }
  });
};

/*
========== ADVANCED GET FUNCTIONS ==========
*/

module.exports.getNameAnswers = function(name, callback) {
  DBRef.collection('names').find({"name": name}).toArray(function(err, docs) {
    if (err) {
      console.log(err);
    } else if (docs.length==0) {
      console.log("\'" + name + "\' is not an available name. Is it formatted correctly?");
    } else {
      //get full shuffled list with correct answer in 0th position
      var correctIndex = 0;
      var correctAnswer = docs[correctIndex];
      var maxYear = correctAnswer.year;
      for (var i = docs.length - 1; i >= 0; i--) {
        var j = Math.floor(Math.random() * (i + 1));
        var temp = docs[i];
        docs[i] = docs[j];
        docs[j] = temp;

        if(docs[i].percent>=correctAnswer.percent) {
          correctIndex = i;
          correctAnswer = docs[correctIndex];
        }
        if(docs[i].year>maxYear) {
          maxYear = docs[i].year;
        }
      }
      docs[correctIndex] = docs[0];
      docs[0] = correctAnswer;

      //force list to be 5 elements long
      while(docs.length<5) {
        var fakeAnswer = {};
        fakeAnswer.year = ++maxYear;
        docs.push(fakeAnswer);
      }
    }
    callback(docs.slice(0,5));
  });
};

module.exports.getNameOverTime = function(name, callback) {
  DBRef.collection('names').find({"name": name}).toArray(function(err, docs) {
    if (err) {
      console.log(err);
    } else {
      var nameOverTime = {};
      for (i = 0; i < docs.length; i++) {
        nameOverTime[docs[i].year] = docs[i].percent;
      }
      callback(nameOverTime);
    }
  });
};

module.exports.getYearAnswers = function(year, callback) {
  DBRef.collection('names').find({"year": Number(year)}).toArray(function(err, docs) {
    if (err) {
      console.log(err);
    } else if (docs.length==0) {
      console.log("\'" + year + "\' is not an available year. Is it formatted correctly?");
    } else {
      //get full shuffled list
      for (var i = docs.length - 1; i >= 0; i--) {
        var j = Math.floor(Math.random() * (i + 1));
        var temp = docs[i];
        docs[i] = docs[j];
        docs[j] = temp;
      }

      //grab first five elements
      docs = docs.slice(0,5);

      //of those five, put the most popular in the 0th position
      var correctIndex = 0;
      var correctAnswer = docs[correctIndex];
      for (var i = docs.length - 1; i > 0; i--) {
        if(docs[i].percent>=correctAnswer.percent) {
          correctIndex = i;
          correctAnswer = docs[correctIndex];
        }
      }
      docs[correctIndex] = docs[0];
      docs[0] = correctAnswer;
    }
    callback(docs);
  });
};

module.exports.getNamesOfYear = function(year, callback) {
  DBRef.collection('names').find({"year": Number(year)}).toArray(function(err, docs) {
    if (err) {
      console.log(err);
    } else {
      var namesOfYear = {};
      for (i = 0; i < docs.length; i++) {
        namesOfYear[docs[i].name] = docs[i].percent;
      }
      callback(namesOfYear);
    }
  });
};

module.exports.getRandomObject = function(callback) {
  var year = Math.floor(Math.random() * (2008 - 1880 + 1)) + 1880;
  DBRef.collection('names').find({"year": Number(year)}).toArray(function(err, docs) {
    if (err) {
      console.log(err);
    } else {
      callback(docs[Math.floor(Math.random() * docs.length)]);
    }
  });
};

/*
========== PUT REQUESTS ==========
*/

module.exports.putScoreData = function(scoreData, callback) {
  DBRef.collection('scores').save(scoreData, function(err, result){
    callback(result);
  });
};
