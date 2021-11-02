/*
========== INITIALIZE NODE SERVER ==========
*/

var express = require('express');
var queryParser = require('body-parser');
var app = express();
app.set("port", 32006);
app.use(queryParser.urlencoded({
    extended: true
}));
app.use(queryParser.json());
var dataBase = require('./dataBaseFunctions.js');

/*
========== SIMPLE GET FUNCTIONS ==========
*/

app.get('/getByYear/:year', function (req, res) {
  dataBase.getByYear(req.params.year, function (docs) {
    res.send(docs);
  });
});

app.get('/getByName/:name', function (req, res) {
  dataBase.getByName(req.params.name, function (docs) {
    res.send(docs);
  });
});

app.get('/getBySex/:sex', function (req, res) {
  dataBase.getBySex(req.params.sex, function (docs) {
    res.send(docs);
  });
});

app.get('/getAllNames', function (req, res) {
  dataBase.getAllNames(function (docs) {
    res.send(docs);
  });
});

app.get('/getScoreData', function (req, res) {
  dataBase.getScoreData(function (docs) {
    res.send(docs);
  });
});

/*
========== ADVANCED GET FUNCTIONS ==========
*/

app.get('/getNameAnswers/:name', function (req, res) {
  dataBase.getNameAnswers(req.params.name, function (docs) {
    res.send(docs);
  });
});

app.get('/getNameOverTime/:name', function (req, res) {
  dataBase.getNameOverTime(req.params.name, function (docs) {
    res.send(docs);
  });
});

app.get('/getYearAnswers/:year', function (req, res) {
  dataBase.getYearAnswers(req.params.year, function (docs) {
    res.send(docs);
  });
});

app.get('/getNamesOfYear/:year', function (req, res) {
  dataBase.getNamesOfYear(req.params.year, function (docs) {
    res.send(docs);
  });
});

app.get('/getRandomObject', function (req, res) {
  dataBase.getRandomObject(function (docs) {
    res.send(docs);
  });
});

/*
========== PUT REQUESTS ==========
*/

app.put('/putScoreData', function (req, res) {
  if (!req.body){
    return res.sendStatus(400);
  }
  var scoreData = {
  	"name" : req.body.name,
    "score" : req.body.score,
    "time" : req.body.time
  };
  dataBase.putScoreData(scoreData, function (doc){
    res.send(doc);
  });
});

/*
========== PORT LISTENING ==========
*/

app.listen(app.get("port"), function () {
    console.log('finalProjectServer running on port ', app.get("port"));
});
