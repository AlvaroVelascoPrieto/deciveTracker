var  multer=require('multer');
var storage = multer.diskStorage({
	destination: function (req, file, cb) {
            cb(null, './public/uploads')
	},
	filename: function (req, file, cb) {
        	var fileFormat = (file.originalname).split(".");
		console.log(fileFormat)
          	cb(null, fileFormat[0] + "." + fileFormat[fileFormat.length - 1]);
      	}
});
var upload = multer({
	storage: storage
});

module.exports = upload;
