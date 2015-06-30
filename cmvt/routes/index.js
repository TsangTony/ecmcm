var express = require('express');
var router = express.Router();
var sql = require('mssql'); 
var config = {
	user: 'sa',
	password: 'P@ssw0rd',
	server: 'localhost',
	database: 'ECMCM',
	
	options: {
		encrypt: false
	}
}

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.get('/getTeam', function(req, res, next) {
	var connection = new sql.Connection(config, function(err) {
		// ... error checks 
		if (err) {
			console.log('connection error');
		}
		var request = new sql.Request(connection); // or: var request = connection.request(); 
		request.query('select * from team', function(err, recordset) {
			// ... error checks 
			if (err) {
				console.log('query error');
			}
			res.send(recordset);
		});	
	});
});

router.get('/getDoc', function(req, res, next) {
	var connection = new sql.Connection(config, function(err) {
		// ... error checks 
		if (err) {
			console.log('connection error');
		}
		var request = new sql.Request(connection); // or: var request = connection.request(); 
		request.query('select * from identified_doc', function(err, recordset) {
			// ... error checks 
			if (err) {
				console.log('query error');
			}
			res.send(recordset);
		});	
	});
});

router.get('/updateStatus', function(req, res, next) {
	var connection = new sql.Connection(config, function(err) {
		// ... error checks 
		if (err) {
			console.log('connection error');
		}
		var request = new sql.Request(connection); // or: var request = connection.request(); 
		//implement update
	});
});

module.exports = router;