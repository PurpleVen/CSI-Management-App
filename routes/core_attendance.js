var express=require('express');
var app=express();
var express=require('express');
var router=express.Router();
var dotenv = require('dotenv');
dotenv.config();
app.use(express.json()); // This line is essential for parsing JSON bodies

// MySQL Connection
var mysql=require('mysql');
const connection=mysql.createConnection({
	host: '128.199.23.207',
    user: "csi",
    password: "csi",
    database: 'csiApp2022'
});

connection.connect(function(err){
    	if(!err){
        	console.log('Connected to MySql! core Attendance');
    	}
	else{	
        	console.log("Not Connected To Mysql! Attendance");
    	}
});

//Attendance Request
router.post('/request',(req,res)=>{
    var ad_id=req.body.ad_id;
	var id=req.body.id; //core_id
	var date=req.body.date;
	var s1 = req.body.s1;
	var s2 = req.body.s2;
	var s3 = req.body.s3;
	var s4 = req.body.s4;
	var s5 = req.body.s5;
	var s6 = req.body.s6;
	var s7 = req.body.s7;
//	var timeslot=req.body.timeslot;

	var missed = req.body.missed;
	var reason=req.body.reason;

	//fetching name from users table
	connection.query('SELECT core_en_fname,core_class FROM core_details WHERE core_details.core_id=?',[id],function(err,rest){
		console.log(rest)
		if (err){
			console.log(err);
			res.sendStatus(400);
		}
		else{
			//pushing into request(attendance_details) table
			//INSERT INTO attendance_details (core_id,core_date,s1,s2,s3,s4,s5,s6,s7,core_timeslot,core_lecsmissed_sub,core_reason) VALUES(5,'2023-10-19',1,0,0,0,0,0,1,'11:00:00',"BI","test s");
			connection.query('INSERT INTO attendance_details(core_id,core_date,s1,s2,s3,s4,s5,s6,s7,core_lecsmissed_sub,core_reason) VALUES(?,?,?,?,?,?,?,?,?,?,?)',[id,date,s1,s2,s3,s4,s5,s6,s7,missed,reason],function(err,results,fields){
				if(err){
					console.log(err);
					res.sendStatus(400);
				}
				else{
					console.log("Data Inserted");
					res.sendStatus(200);
					
				}
			});
		}
	});
});

//Display all the requests
router.post('/requestlist',(req,res)=>{
	connection.query('SELECT cd.core_en_fname, ad.* FROM attendance_details ad JOIN core_details cd ON ad.core_id = cd.core_id WHERE ad.status = "WAITING";',function(error,result){
		if(error){
			//console.log"(Error");
			res.sendStatus(400);
		}
		else
		{
    			res.status(200).send(result);
				console.log(result);
		}
	});
});

//Accept json array,move the record from request to final_list
// Route to update the status of attendance requests
router.post('/finallist', (req, res) => {
	console.log(req.body); // Check the incoming data
	// Extract the 'accepted' array from the request body
	const acceptedIds = req.body.accepted;
  
	if (!acceptedIds || acceptedIds.length === 0) {
	  return res.status(400).send({ message: 'No accepted IDs provided.' });
	}
  
	// Keep track of completed queries
	let completedQueries = 0;
	const totalQueries = acceptedIds.length;
	let encounteredError = false;
  
	// Update each accepted ID in the database
	acceptedIds.forEach(ad_id => {
	  connection.query('UPDATE attendance_details SET status = "ACCEPTED" WHERE ad_id = ?', [ad_id], (error, results) => {
		completedQueries++;
		if (error) {
		  encounteredError = true;
		  console.error('Failed to update ad_id:', ad_id, error);
		  // Send an error response only once
		  if (!res.headersSent) {
			res.status(500).send({ message: 'Error updating attendance status.' });
		  }
		}
		// If all queries have been processed and no error response has been sent, send a success response
		if (completedQueries === totalQueries && !encounteredError && !res.headersSent) {
		  res.status(200).send({ message: 'All attendance statuses updated successfully.' });
		}
	  });
	});
  });



//Accept json array,move the record from request to finallist
// router.post('/finallist', (req, res) =>{

	
	// for(var i=0;i<req.body.accepted.length;i++)
	// {
	// var ad_id=req.body.accepted [i];

	// // for(i in ids){ //index value=i
	// 	//connection.query('DELETE FROM attendance_details WHERE ad_id=?',[ids[i]],function (error,result){
	// 		connection.query('UPDATE attendance_details SET status = "ACCEPTED" WHERE ad_id=?',[ad_id],function (error,result){
			
	// 			//console.log("Error");
	// 			res.sendStatus(400);
			
	
	// 	});
	// }
	// res.sendStatus(200);


  	//for(var i=0;i<req.body.accepted.length;i++)
	// {
	// 	var rid = req.body.accepted[i]
	// 	connection.query('INSERT INTO attendance_details_finallist SELECT * FROM attendance_details WHERE ad_id = ?',[rid],  function (error, fields){ //rid=ad_id
	// 		if (error){
	// 			//console.log(rid);
	// 			res.sendStatus(400);
	// 		}
	// 		else{
	// 			connection.query('DELETE FROM attendance_details WHERE ad_id = ?',[rid], function (error, results, fields) {
	// 				if (error){
	// 					//console.log("Error");
	// 				res.sendStatus(400);
	// 				}
	// 				else{
	// 					//console.log("Deleted succesfully");
	//     					res.sendStatus(200);
	// 				}
	// 			});
	// 			//console.log("Inserted succesfully");
	// 		}
	// 	});
	// }
// });

// Attendance Reject
router.post('/reject', (req, res) => {
    const ids = req.body.rejected;
    console.log('Received IDs:', ids);

    // Ensure ids is an array
    if (!Array.isArray(ids)) {
        return res.status(400).send('Invalid data format');
    }

    const promises = ids.map(id =>
        new Promise((resolve, reject) => {
            connection.query('UPDATE attendance_details SET status = "REJECTED" WHERE ad_id = ?', [id], (error, result) => {
                if (error) {
                    console.error(`Error updating core_id ${id}:`, error);
                    return reject(error);
                }
                console.log(`Successfully updated core_id ${id}`);
                resolve(result);
            });
        })
    );

    Promise.allSettled(promises).then(results => {
        const errors = results.filter(r => r.status === 'rejected');
        if (errors.length > 0) {
            // Handle partial failure or complete failure
            console.error("Errors occurred:", errors);
            res.status(400).send('Some updates failed');
        } else {
            // All queries succeeded
            res.sendStatus(200);
        }
    });
});



// router.post('/reject', (req, res) => {
// 	console.log(req.body); // Check the incoming data
// 	const ids = req.body.rejected;
  
// 	// Check if ids are provided
// 	if (!ids || ids.length === 0) {
// 	  return res.status(400).send({ message: 'No rejected IDs provided.' });
// 	}
  
// 	// Create a promise for each update operation
// 	const updatePromises = ids.map(ad_id => {
// 	  return new Promise((resolve, reject) => {
// 		connection.query('UPDATE attendance_details SET status = "REJECTED" WHERE core_id = ?', [ad_id], (error, result) => {
// 		  if (error) {
// 			reject(error);
// 		  } else {
// 			resolve(result);
// 		  }
// 		});
// 	  });
// 	});
  
// 	// Wait for all promises to settle
// 	Promise.allSettled(updatePromises)
// 	  .then(results => {
// 		// Check if any promise was rejected
// 		const rejectedOperation = results.find(result => result.status === 'rejected');
// 		if (rejectedOperation) {
// 		  // At least one operation failed
// 		  res.status(500).send({ message: 'Failed to update some or all attendance records.' });
// 		} else {
// 		  // All operations succeeded
// 		  res.status(200).send({ message: 'All attendance records updated successfully.' });
// 		}
// 	  })
// 	  .catch(error => {
// 		// Handle unexpected errors (this catch block is technically optional here due to Promise.allSettled behavior)
// 		console.error('Unexpected error updating attendance records:', error);
// 		res.status(500).send({ message: 'An unexpected error occurred.' });
// 	  });
//   });
  

//SBC Attendance
// router.post('/view',(req,res)=>{
// 	var id=req.body.id; 
// 	var year=req.body.year;
// 	var name=req.body.name;
	
	// connection.query('SELECT core_en_fname, core_class FROM core_details WHERE core_details.core_id=?',[id],function(error,rest2){
	// 	if(error){
	// 		res.sendStatus(400);
	// 		console.log(error);
	// 	}
	// 	else{
			//connection.query('SELECT sum(s1+s2+s3+s4+s5+s6+s7) as total FROM attendance_details_finallist WHERE adf_id=?',[adf_id], function (error,result){
			//connection.query('SELECT sum(s1+s2+s3+s4+s5+s6+s7) as total FROM attendance_details WHERE status = "ACCEPTED"', function (error,result){
			// connection.query('SELECT cd.core_en_fname,cd.core_class,sum(ad.s1+ad.s2+ad.s3+ad.s4+ad.s5+ad.s6+ad.s7) as hours_spent from core_details cd inner join attendance_details ad on cd.core_id=ad.core_id AND status= "ACCEPTED" group by cd.core_id;',function (error , result){
			// 	if(error){
			// 		res.sendStatus(400);
			// 		console.log(error);
			// 	}
			// 	else{
			// 		res.status(200).send(result);
			// 		console.log("Successfull");
			// 	}
			// });
				
				//SELECT sum(s1+s2+s3+s4+s5+s6+s7) as total FROM attendance_details WHERE status = "ACCEPTED"', function (error,result){	
			
			//});
	//	}
	//});
// });

//SBC Attendance
router.post('/view',(req,res)=>{
	var id=req.body.id; 
	var year=req.body.year;
	var name=req.body.name;
	var hours_spent=req.body.hours_spent;

connection.query('SELECT cd.core_en_fname as Name,cd.core_rollno as RollNo ,sum(ad.s1+ad.s2+ad.s3+ad.s4+ad.s5+ad.s6+ad.s7) as hours_spent from core_details cd inner join attendance_details ad on cd.core_id=ad.core_id AND status="ACCEPTED"  where cd.core_class = ? group by cd.core_id',[year],function(error,result){
	//connection.query('SELECT cd.core_en_fname as Name,cd.core_class ,sum(ad.s1+ad.s2+ad.s3+ad.s4+ad.s5+ad.s6+ad.s7) as hours_spent from core_details cd inner join attendance_details ad on cd.core_id=ad.core_id AND status="ACCEPTED" group by cd.core_id',function(error,result){
		if(error){
			res.sendStatus(400);
			console.log(error);
		}
		else{
			res.status(200).send(result);
			console.log("Successfull");
		}
	})
});



module.exports = router;



