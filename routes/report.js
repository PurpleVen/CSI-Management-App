var express = require('express');
var router = express.Router();
var fs = require("fs");
var pdfkit = require("pdfkit");
var dotenv = require('dotenv');
dotenv.config();
const axios = require('axios');
const {connection , server_url} = require('../serverconfig');
// MySQL Connection
// var mysql=require('mysql');
// const connection = mysql.createConnection({
// 	host: 'localhost',
// 	user: "root",
//     password: "",
// 	database: 'csiApp'
// });

// connection.connect(function(err) {
// 	if (!err){
//         	console.log('Connected to MySql!Report.js');
//     	}
//     	else{
//         	console.log('Not Connected To Mysql!Report.js');
//     	}
// });

//Listing All events ready for report
router.get('/list',(req,res)=>{

	connection.query('SELECT cpm_id,proposals_event_name,proposals_event_category,proposals_event_date FROM core_proposals_manager where proposals_status=3 order by cpm_id DESC',function(err,result){
		if(err){
			console.log("Report listing error");
			res.sendStatus(400);
		}
		else{
			console.log("Succesully Listed Report");
			console.log(result);
			res.status(200).send(result);
		}
	});
});

//Download Report
router.get('/generate',(req,res)=>{
var eid = req.query.eid;

	connection.query('SELECT * FROM core_proposals_manager WHERE cpm_id = ?',[eid], function( err , result){
		if(err) {
    			throw err;
 			} 
		else {
			const a= new pdfkit
			a.pipe(fs.createWriteStream("report/".concat(result[0].proposals_event_name).concat(".pdf")));
			a.fontSize(15).text("Don Bosco Institute of Technology, Kurla(W)",{align: 'center'})
			a.text("Department of Information Technology",{align: 'center'})
			a.moveDown(2);
			a.font('Helvetica-Bold').fontSize(30).text(result[0].proposals_event_name,{align: 'center'})
			a.moveDown();
			a.font('Helvetica-Bold').fontSize(15).text('Name: ', {continued:true}).font('Helvetica').text (result[0].proposals_event_name)
			a.moveDown();
			a.font('Helvetica-Bold').text('Theme: ', {continued:true}).font('Helvetica').text (result[0].proposals_event_category)
			a.moveDown();
			a.font('Helvetica-Bold').text('Description: ', {continued:true}).font('Helvetica').text (result[0].proposals_desc)
			a.moveDown();
			a.font('Helvetica-Bold').text('Event Date: ', {continued:true}).font('Helvetica').text (result[0].proposals_event_date)
			a.moveDown();
			a.font('Helvetica-Bold').text('Venue: ', {continued:true}).font('Helvetica').text (result[0].proposals_venue)
			a.moveDown();
			a.font('Helvetica-Bold').text('Speaker: ', {continued:true}).font('Helvetica').text (result[0].speaker)
			a.moveDown();
			a.font('Helvetica-Bold').text('Finance Summary ')
			a.font('Helvetica').text('Registration fee for CSI members: ',{continued:true}).font('Helvetica').text (result[0].proposals_reg_fee_csi)
			a.moveDown(2);
			a.text('Registration fee for Non-CSI members: ', {continued:true}).font('Helvetica').text (result[0].proposals_reg_fee_noncsi)
			a.moveDown(2);
			a.font('Helvetica-Bold').text('Prize: ', {continued:true}).font('Helvetica').text (result[0].proposals_prize)
			a.moveDown();

		connection.query('SELECT creative_url FROM csiApp2022.core_creative_manager WHERE cpm_id= ?',[eid], function( err , result){
			if (err) {
				throw err;
			} else {
				const imageUrls = result.map(result => result.creative_url);

				const rows = 2;
                const cols = 2;
                const gridSize = 250;

                let currentRow = 0;
                let currentCol = 0;

				imageUrls.forEach(imageUrl => {
					axios.get(imageUrl, { responseType: 'arraybuffer' })
						.then(response => {
							const imageData = Buffer.from(response.data, 'binary');

							if (currentCol === cols) {
								currentCol = 0;
								currentRow++;
							}

							const x = currentCol * gridSize;
							const y = currentRow * gridSize;

							a.addPage();
							a.image(imageData, x, y, { width: 250, height: 250 });

							currentCol++;
						})
						.catch(error => {
							console.error('Error downloading image:', error);
						});
				});
				
			}
		connection.query('SELECT proposals_creative_budget,proposals_publicity_budget,proposals_guest_budget,proposals_total_budget FROM csiApp2022.core_proposals_manager WHERE cpm_id= ?',[eid], function( err , result){
			if(err) {
					throw err;
				} 
			else {
				a.addPage();
				a.font('Helvetica-Bold').fontSize(25).text('Budget:')
				a.lineCap('butt')
					.moveTo(330, 110)
					.lineTo(330, 200)
					.stroke()

				row(a, 110);
				row(a, 132.5);
				row(a, 155);
				row(a, 177.5);

				textInRowFirst(a, 'Creative budget', 115);
				textInRowFirst(a, 'Publicity budget', 137.5);
				textInRowFirst(a, 'Guest budget', 160);
				textInRowFirst(a, 'Total budget', 182.5);
				textInRowSecond(a,result[0].proposals_creative_budget, 115);
				textInRowSecond(a,result[0].proposals_publicity_budget, 137.5);
				textInRowSecond(a,result[0].proposals_guest_budget, 160);
				textInRowSecond(a,result[0].proposals_total_budget, 182.5);

			function textInRowFirst(a, text, heigth) {
				a.y = heigth;
				a.x = 30;
				a.fillColor('black')
				a.font('Helvetica')
				a.fontSize(15)
				a.text(text, {
						paragraphGap: 5,
						indent: 60,
						align: 'justify',
						columns: 1,
					});
				return a
				}

			function textInRowSecond(a, text, heigth) {
				a.y = heigth;
				a.x = 30;
				a.fillColor('black')
				a.font('Helvetica')
				a.text(text, {
						paragraphGap: 5,
						indent: 310,
						columns: 1,
					});
				return a
				}

			function row(a, heigth) {
				a.lineJoin('miter')
					.rect(75, heigth, 500, 22.5)
					.stroke()
				return a
				}
		connection.query('SELECT pr_rcd_amount,pr_spent,pr_member_count FROM csiApp2022.core_pr_manager WHERE cpm_id= ?',[eid], function( err , result){
			if(err) {
					throw err;
				} 
			else {
				a.moveDown(4);
				a.font('Helvetica-Bold').fontSize(25).text('Summary: ',{indent:45})
				a.moveDown(0.5);
				a.font('Helvetica-Bold').fontSize(15).text('Money collected: ', {indent:45,continued:true}).font('Helvetica').text (result[0].pr_rcd_amount)
				a.moveDown();
				a.font('Helvetica-Bold').text('Money spent: ', {indent:45,continued:true}).font('Helvetica').text (result[0].pr_spent)
				a.moveDown();
				
			a.end()  

		connection.query('SELECT proposals_event_name FROM csiApp2022.core_proposals_manager WHERE cpm_id= ?',[eid], function( err , result){
			if(err) {
					throw err;
				} 
			else {
				var file = 'report/'.concat(result[0].proposals_event_name).concat('.pdf');
				var data=fs.readFileSync(file);
				res.status(200).send(data); // Set disposition and send it.
				}
				});
			}
			});
			}
			});
		});
	}

});
});
router.use('/report', express.static(__dirname + '/report'));
module.exports = router;