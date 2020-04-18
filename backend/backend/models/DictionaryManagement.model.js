const db = require('../utils/db');
module.exports = {
    all: () => db.load('select * from av where Id < 11'),
    add: (table, item) => db.add(table, item),
    update: (table, item, id) => db.update(table, item, id),
    getAllPaging: async (table, condition, orderBy, page, rowPerPage) => {
        const total = await db.getNumberOfRows(table)
        var sum = JSON.stringify(total[0].Sum)
        return {
            ListGrid: await db.getAllPaging(table, condition, orderBy, page, rowPerPage),
            TotalCount: parseInt(sum)
        }
    }
}