const express = require('express')
const router = express.Router();
const avModel = require('../models/DictionaryManagement.model');
router.get('/getPage', async (req, res) => {
    const page = req.query.page
    const rowsPerPage = req.query.rowsPerPage
    const list = await avModel.getAllPaging('av', null, null, page, rowsPerPage)
    res.json(list)
})
router.post('/addOrUpdate', async (req, res) => {
    var id = -1
    var item = req.body
    if (item.Id == 0) {
        result = await avModel.add('av', item)
        if (result.affectedRows > 0 && result.insertId) id = result.insertId
    }
    else if (item.Id > 0) {
        result = await avModel.update('av', item, item.Id)
        if (result.affectedRows > 0) id = item.Id
    }
    res.json(id)
})
module.exports = router;