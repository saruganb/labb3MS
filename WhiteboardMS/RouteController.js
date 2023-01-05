const express = require('express'); //import express
const router  = express.Router();
const controller = require('Controller');
// 3.
router.post('/save', controller.save);
// 4.
module.exports = router;